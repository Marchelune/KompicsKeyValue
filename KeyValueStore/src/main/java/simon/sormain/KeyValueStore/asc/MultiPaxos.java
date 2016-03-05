package simon.sormain.KeyValueStore.asc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.simulator.util.GlobalView;
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.sim.multipaxos.OpSequence;
import se.sics.kompics.ClassMatchedHandler;


/**
 * Component implementing abortable sequential concensus with customized paxos
 * @author remi
 *
 */
public class MultiPaxos extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(MultiPaxos.class);

	private Negative<AbortableSequenceConsensusPort> asc = provides(AbortableSequenceConsensusPort.class);
	private Positive<Network> net = requires(Network.class);
	
	private int t, prepts, ats, pts,al,pl, N, selfRank;
	private ArrayList<Object> av, pv, proposedValues;
	private HashMap<TAddress,Integer> accepted, decided;
	private HashMap<TAddress, ProposedPair> readList;
	private TAddress self;
	private Set<TAddress> allAddresses;
	

	public MultiPaxos(MultiPaxosInit event) {
		logger.info("Constructing MultiPaxos component.");
		subscribe(handleStart, control);
		subscribe(handlePropose, asc);
		subscribe(handleNack,net);
		subscribe(handlePrepare,net);
		subscribe(handlePrepareAck, net);
		subscribe(handleAccept,net);
		subscribe(handleAcceptAck,net);
		subscribe(handleDecide, net);
		
		self = event.getSelfAddress();
		allAddresses = event.getAllAddresses();
		N = allAddresses.size();
		selfRank = event.getSelfRank();
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("{} Multipaxos started.", config().getValue("keyvaluestore.self.addr", TAddress.class));
			t = prepts = ats = pts =al = pl = 0;
			av = new ArrayList<Object>();
			pv = new ArrayList<Object>();
			proposedValues = new ArrayList<Object>();
			accepted = new HashMap<TAddress, Integer>();
			decided = new HashMap<TAddress, Integer>();
			readList = new HashMap<TAddress, ProposedPair>();
		}
	};
	
	private Handler<AscPropose> handlePropose = new Handler<AscPropose>() {
		@Override
		public void handle(AscPropose event) {
			
			//Simu
			ProposedOp((Operation) event.getValue());
			
			
			t++;
			if(pts==0){ //proposer timestamp
				pts = N*t + selfRank;
				pv = prefix(av, al);
				pl = 0; // proposer length of learned seq ?
				proposedValues = new ArrayList<Object>();
				proposedValues.add(event.getValue());
				accepted = new HashMap<TAddress, Integer>();
				decided = new HashMap<TAddress, Integer>();
				readList = new HashMap<TAddress, ProposedPair>();
				for(TAddress p : allAddresses){
					trigger(new TMessage(self, p, Transport.TCP, new PrepareMessage(pts, al,t)), net);
				}
			}else if(readList.size() <= Math.floor(N/2.0)){
				proposedValues.add(event.getValue());
			}else if(!(pv.contains(event.getValue()))){ //no duplicates
				pv.add(event.getValue());
				for(TAddress p : readList.keySet()){
					/// TODO maybe an issue here. The value could be modified if it is not a immutable object
					/// but having 0 information on what the value type is, we cannot copy it. So ... ?
					ArrayList<Object> v = new ArrayList<Object>(); v.add(event.getValue());
					trigger(new TMessage(self, p, Transport.TCP, new AcceptMessage(pts, v, pv.size()-1, t) ), net);
				}
			}
		}
	};
	
	private ClassMatchedHandler<PrepareMessage, TMessage> handlePrepare = new ClassMatchedHandler<PrepareMessage, TMessage>() {
		@Override
		public void handle(PrepareMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(content.getTimestamp() < prepts){ 
				trigger(new TMessage(self, context.getSource(), Transport.TCP, new NackMessage(content.getTimestamp(), t)), net);
			}else{
				prepts = content.getTimestamp();
				int l = content.getLength();
				trigger(new TMessage(self, context.getSource(), Transport.TCP, new PrepareAckMessage(content.getTimestamp(), ats, suffix(av,l), al, t)),net);
			}
		}
	};
	
	private ClassMatchedHandler<NackMessage, TMessage> handleNack = new ClassMatchedHandler<NackMessage, TMessage>() {
		@Override
		public void handle(NackMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(pts == content.getTimestamp()){
				pts =0;
				trigger(new AscAbort(),asc);
			}
		}
	};
	
	private ClassMatchedHandler<PrepareAckMessage, TMessage> handlePrepareAck = new ClassMatchedHandler<PrepareAckMessage, TMessage>() {
		@Override
		public void handle(PrepareAckMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(content.getTimestamp() == pts){
				readList.put(context.getSource(),new ProposedPair(content.getAcceptorTimestamp(),content.getSuffix()));
				decided.put(context.getSource(), content.getDecidedLength());
				if(readList.size() == Math.floor(N/2.0)+1){
					ProposedPair maximum = max(readList);
					pv.addAll(maximum.getAcceptedValue());
					
					for(Object value : proposedValues){
						if(!(pv.contains(value))) pv.add(value);
					}
					int tempL;
					for(TAddress p : readList.keySet()){
						tempL = decided.get(p);
						trigger(new TMessage(self, p, Transport.TCP, new AcceptMessage(pts,suffix(pv,tempL),tempL,t) ), net);
					}
				}else if(readList.size() > Math.floor(N/2.0)+1){
					int tempL = content.getDecidedLength();
					trigger(new TMessage(self, context.getSource(), Transport.TCP, new AcceptMessage(pts,suffix(pv,tempL),tempL,t) ), net);
					if(pl != 0){
						trigger(new TMessage(self, context.getSource(), Transport.TCP, new DecideMessage(pts, pl, t)), net);
					}
				}
			}
			
		}
	};
	
	private ClassMatchedHandler<AcceptMessage, TMessage> handleAccept = new ClassMatchedHandler<AcceptMessage, TMessage>() {
		@Override
		public void handle(AcceptMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(content.getTimestamp() != prepts) { // TODO Weird ... shouldn't we allow higher proposer timestamp ?
				trigger(new TMessage(self, context.getSource(), Transport.TCP, new NackMessage(content.getTimestamp(), t)), net);
			}else{
				ats = content.getTimestamp();
				if(content.getLength() < av.size()) av = prefix(av,content.getLength());
				av.addAll(content.getValue());
				trigger(new TMessage(self, context.getSource(), Transport.TCP, 
						new AcceptAckMessage(content.getTimestamp(), av.size(), t)), net);
			}
		}
	};
	
	private ClassMatchedHandler<AcceptAckMessage, TMessage> handleAcceptAck = new ClassMatchedHandler<AcceptAckMessage, TMessage>() {
		@Override
		public void handle(AcceptAckMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(pts == content.getTimestamp()){
				accepted.put(context.getSource(), content.getDecidedLength());
				if (pl < content.getDecidedLength() 
						&& ( countAboveL(accepted, content.getDecidedLength()) > Math.floor(N/2.0) )){
					pl = content.getDecidedLength();
					for(TAddress p : readList.keySet()){
						
						trigger(new TMessage(self, p, Transport.TCP,new DecideMessage(pts, pl, t) ), net);
					}
				}
			}
		}
	};
	
	private ClassMatchedHandler<DecideMessage, TMessage> handleDecide = new ClassMatchedHandler<DecideMessage, TMessage>() {
		@Override
		public void handle(DecideMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(prepts == content.getTimestamp()){
				while(al < content.getDecidedLength()){
					trigger(new AscDecide(av.get(al) ), asc);
					DecidedOp((Operation) av.get(al));	//Simu
					al++;
				}
			}
			
		}
	};
	
	private int countAboveL(HashMap<TAddress, Integer> accepted, int length){
		int count = 0;
		for(int l : accepted.values()){
			if(l >= length) count++;
		}
		return count;
	}
	/**
	 * 
	 * @param readList
	 * @return the maximum pair of the readlist (highest timestamp and length)
	 */
	private ProposedPair max(HashMap<TAddress, ProposedPair> readList) {	
		int tempTF = 0;
		ArrayList<Object> tempVSUF = new ArrayList<Object>();
		for (ProposedPair pair : readList.values())
		{
		    if(tempTF<pair.getAcceptedValueRound()
		    		|| (tempTF==pair.getAcceptedValueRound() && tempVSUF.size()<pair.getAcceptedValue().size())){
		    	tempTF = pair.getAcceptedValueRound();
		    	tempVSUF.clear();
		    	tempVSUF.addAll(pair.getAcceptedValue());
		    }
		}
		return new ProposedPair(tempTF, tempVSUF);
	}
	/**
	 *
	 * @param sigma a sequence
	 * @param k the number of element in the prefix
	 * @return the prefix of sequence sigma with at most k elements.
	 */
	private ArrayList<Object>  prefix(ArrayList<Object> sigma,int k){
		ArrayList<Object> result = new ArrayList<Object>();
		result.addAll(sigma.subList(0, k));
		return result;
	}
	
	/**
	 * 
	 * @param sigma a sequence
	 * @param k
	 * @return the suffix of sigma by skipping the k first elements and returning the rest
	 */
	private ArrayList<Object>  suffix(ArrayList<Object> sigma,int k){
		ArrayList<Object> result = new ArrayList<Object>();
		result.addAll(sigma.subList(k, sigma.size()));
		return result;
	}
	
	
	//Simu
	private void DecidedOp(Operation op) {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        TAddress selfaddr= config().getValue("keyvaluestore.self.addr", TAddress.class);
        OpSequence DecidedSeq;
        switch (selfaddr.getPort()) {
        case 10000 :
        	DecidedSeq = gv.getValue("simulation.seqdecided1", OpSequence.class);
        	DecidedSeq.add(op);
        	gv.setValue("simulation.seqdecided1", DecidedSeq);
        	break;
        case 20000 :
        	DecidedSeq = gv.getValue("simulation.seqdecided2", OpSequence.class);
        	DecidedSeq.add(op);
        	gv.setValue("simulation.seqdecided2", DecidedSeq);
        	break;
        case 30000 :
        	DecidedSeq = gv.getValue("simulation.seqdecided3", OpSequence.class);
        	DecidedSeq.add(op);
        	gv.setValue("simulation.seqdecided3", DecidedSeq);
        	break;
        case 40000 :
        	DecidedSeq = gv.getValue("simulation.seqdecided4", OpSequence.class);
        	DecidedSeq.add(op);
        	gv.setValue("simulation.seqdecided4", DecidedSeq);
        	break;
        case 50000 :
        	DecidedSeq = gv.getValue("simulation.seqdecided5", OpSequence.class);
        	DecidedSeq.add(op);
        	gv.setValue("simulation.seqdecided5", DecidedSeq);
        	break;
    		
        }
	}
	
	private void ProposedOp(Operation op){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		OpSequence ProposedSeq = gv.getValue("simulation.proposedcommands", OpSequence.class);
		ProposedSeq.add(op);
		gv.setValue("simulation.proposedcommands", ProposedSeq);
	}
}
