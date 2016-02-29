package simon.sormain.KeyValueStore.tob;

import org.slf4j.Logger;

import java.util.HashSet;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.asc.AbortableSequenceConsensusPort;
import simon.sormain.KeyValueStore.asc.AscAbort;
import simon.sormain.KeyValueStore.asc.AscDecide;
import simon.sormain.KeyValueStore.asc.AscPropose;
import simon.sormain.KeyValueStore.eld.EventualLeaderDetectorPort;
import simon.sormain.KeyValueStore.eld.Trust;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;

public class Tob extends ComponentDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(Tob.class);
	private Negative<TotalOrderBroadcastPort> tob = provides(TotalOrderBroadcastPort.class);
	private Positive<EventualLeaderDetectorPort> eld = requires(EventualLeaderDetectorPort.class);
	private Positive<AbortableSequenceConsensusPort> asc = requires(AbortableSequenceConsensusPort.class);
	private Positive<Network> net = requires(Network.class);

	private final TAddress self;
	private TAddress leader;
	private HashSet<Operation> undecided;
	private HashSet<Operation> decided; //maybe not useful; but better be sure that we don't have duplicates
	
	
	public Tob(TobInit init) {
		logger.info("Creating TOB.");
		subscribe(handleStart, control);
		subscribe(handleTobBroadcast,tob);
		subscribe(handleOperation, net);
		subscribe(handleTrust, eld);
		subscribe(handleAbort, asc);
		subscribe(handleDecide, asc);
		
		leader = self = init.getSelfAddress(); // at least before the first trust event
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			undecided = new HashSet<Operation>();
			decided = new HashSet<Operation>();
		}
	};
	
	private Handler<TobBroadcast> handleTobBroadcast = new Handler<TobBroadcast>() {
		@Override
		public void handle(TobBroadcast event) {
			undecided.add(event.getOp());
			if(trusted()){
				trigger(new AscPropose(event.getOp()), asc);
			}else{
				trigger(new TMessage(self, leader, Transport.TCP, new TobDeliver( event.getOp())), net);
			}
		}
	};
	
	private ClassMatchedHandler<TobDeliver, TMessage> handleOperation = new ClassMatchedHandler<TobDeliver, TMessage>() {
		@Override
		public void handle(TobDeliver content, TMessage context) {
			if(trusted()){
				Operation op = content.getOp();
				if(!decided.contains(op)){ //is it useful ? not sure if messages could get lost and arrive after
					undecided.add(op);
					trigger(new AscPropose(op), asc);
				}
			}
			
		}
	};
	
	private Handler<Trust> handleTrust = new Handler<Trust>() {
		@Override
		public void handle(Trust event) {
			leader = event.getLeader();
			if(!trusted()){
				for(Operation op : undecided){
					trigger(new TMessage(self, leader, Transport.TCP, new TobDeliver(op)), net);
				}
			}else{
				for(Operation op : undecided){
					trigger(new AscPropose(op), asc);
				}
			}
		}
	};
	
	private Handler<AscAbort> handleAbort = new Handler<AscAbort>() {
		@Override
		public void handle(AscAbort event) {
			if(trusted()){
				for(Operation op : undecided){
					trigger(new AscPropose(op), asc);
				}
			}
			
		}
	};
	
	private Handler<AscDecide> handleDecide = new Handler<AscDecide>() {
		@Override
		public void handle(AscDecide event) {
			undecided.remove((Operation) event.getValue());
			decided.add((Operation)event.getValue());
			trigger( new TobDeliver((Operation) event.getValue() ) , tob);
			
		}
	};
	
	private boolean trusted() {
		return self == leader;
	}
	



}
