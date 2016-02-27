package simon.sormain.KeyValueStore.asc;

import java.util.ArrayList;
import java.util.HashMap;
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
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
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
		
		self = event.getSelfAddress();
		allAddresses = event.getAllAddresses();
		N = allAddresses.size();
		selfRank = event.getSelfRank();
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("Multipaxos started.");
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
					/// but having 0 information on what the value is, we cannot copy it. So ... ?
					trigger(new TMessage(self, p, Transport.TCP, new AcceptMessage(pts, event.getValue(), pv.size()-1, t) ), net);
				}
			}
		}
	};
	
	ClassMatchedHandler<PrepareMessage, TMessage> prepareHandler = new ClassMatchedHandler<PrepareMessage, TMessage>() {
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
	
	ClassMatchedHandler<NackMessage, TMessage> nackHandler = new ClassMatchedHandler<NackMessage, TMessage>() {
		@Override
		public void handle(NackMessage content, TMessage context) {
			t = Math.max(t, content.getLogicalClock()) +1;
			if(pts == content.getTimestamp()){
				pts =0;
				trigger(new AscAbort(),asc);
			}
		}
	};
	/**
	 *
	 * @param sigma a sequence
	 * @param k the number of element in the prefix
	 * @return the prefix of sequence sigma with at most k elements.
	 */
	private ArrayList<Object>  prefix(ArrayList<Object> sigma,int k){
		ArrayList<Object> result = new ArrayList<Object>();
		sigma.subList(0, k).addAll(result);
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
		sigma.subList(k, sigma.size()).addAll(result);
		return result;
	}
}
