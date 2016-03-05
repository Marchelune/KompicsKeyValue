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
import se.sics.kompics.simulator.util.GlobalView;
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
import simon.sormain.KeyValueStore.sim.multipaxos.OpSequence;
import simon.sormain.KeyValueStore.sim.tob.OpSet;

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
			logger.info("{} TOB started.", config().getValue("keyvaluestore.self.addr", TAddress.class));
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
			//Simu
			BroadcastOp(event.getOp());
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
			DeliveredOp((Operation) event.getValue()); // Simu
			
		}
	};
	
	private boolean trusted() {
		return self.equals(leader);
	}
	
	//Simu
	private void DeliveredOp(Operation op) {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        TAddress selfaddr= config().getValue("keyvaluestore.self.addr", TAddress.class);
        
        OpSet GlobalDelivered = gv.getValue("simulation.globaldelivered", OpSet.class);
        GlobalDelivered.add(op);
        gv.setValue("simulation.globaldelivered", GlobalDelivered);
        
        OpSequence DeliveredSeq;
        switch (selfaddr.getPort()) {
        case 10000 :
        	DeliveredSeq = gv.getValue("simulation.seqdelivered1", OpSequence.class);
        	DeliveredSeq.add(op);
        	gv.setValue("simulation.seqdelivered1", DeliveredSeq);
        	break;
        case 20000 :
        	DeliveredSeq = gv.getValue("simulation.seqdelivered2", OpSequence.class);
        	DeliveredSeq.add(op);
        	gv.setValue("simulation.seqdelivered2", DeliveredSeq);
        	break;
        case 30000 :
        	DeliveredSeq = gv.getValue("simulation.seqdelivered3", OpSequence.class);
        	DeliveredSeq.add(op);
        	gv.setValue("simulation.seqdelivered3", DeliveredSeq);
        	break;
        case 40000 :
        	DeliveredSeq = gv.getValue("simulation.seqdelivered4", OpSequence.class);
        	DeliveredSeq.add(op);
        	gv.setValue("simulation.seqdelivered4", DeliveredSeq);
        	break;
        case 50000 :
        	DeliveredSeq = gv.getValue("simulation.seqdelivered5", OpSequence.class);
        	DeliveredSeq.add(op);
        	gv.setValue("simulation.seqdelivered5", DeliveredSeq);
        	break;
    		
        }
	}

	private void BroadcastOp(Operation op) {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        TAddress selfaddr= config().getValue("keyvaluestore.self.addr", TAddress.class);
        OpSequence BcSeq;
        switch (selfaddr.getPort()) {
        case 20000 :
        	BcSeq = gv.getValue("simulation.seqbc2", OpSequence.class);
        	BcSeq.add(op);
        	gv.setValue("simulation.seqbc2", BcSeq);
        	break;
        case 30000 :
        	BcSeq = gv.getValue("simulation.seqbc3", OpSequence.class);
        	BcSeq.add(op);
        	gv.setValue("simulation.seqbc3", BcSeq);
        	break;
    		
        }
	}

}
