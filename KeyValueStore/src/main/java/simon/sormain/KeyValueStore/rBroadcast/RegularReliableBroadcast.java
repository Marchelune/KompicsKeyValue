package simon.sormain.KeyValueStore.rBroadcast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import simon.sormain.KeyValueStore.epfd.EventuallyPerfectFailureDetectorPort;
import simon.sormain.KeyValueStore.epfd.Restore;
import simon.sormain.KeyValueStore.epfd.Suspect;
import simon.sormain.KeyValueStore.network.TAddress;

/**
 * \brief implements a regular lazy reliable broadcast slightly customized
 * (keeps a set of suspected instead of correct, and remember the targets of the broadcast)
 * 
 * @author remi
 *
 */
public class RegularReliableBroadcast extends ComponentDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(RegularReliableBroadcast.class);
	
	private Positive<EventuallyPerfectFailureDetectorPort> epfd = requires(EventuallyPerfectFailureDetectorPort.class);
	private Positive<BEBroadcastPort> beb = requires(BEBroadcastPort.class);
	private Negative<RegularReliableBroadcastPort> rrb = provides(RegularReliableBroadcastPort.class);
	
	private int seqnum;
	private Set<TAddress> suspected;
	private HashMap<TAddress, BroadcastedMessage> lastDelivered;

	public RegularReliableBroadcast() {
		subscribe(handleStart, control);
		subscribe(handleBroadcast, rrb);
		subscribe(handleBEDeliver, beb);
		subscribe(handleSuspect, epfd);
		subscribe(handleRestore, epfd);
	}
	
	private Handler<Start> handleStart = new Handler<Start>(){
		@Override
		public void handle(Start event) {
			logger.info("RegularReliableBroadcast started");
			seqnum = 0;
			suspected = new HashSet<TAddress>();
			lastDelivered = new HashMap<TAddress, BroadcastedMessage>();
		}
	};
	
	private Handler<Broadcast> handleBroadcast = new Handler<Broadcast>() {
		@Override
		public void handle(Broadcast event) {
			seqnum++;
			trigger(new BEBroadcast(event.getSrc(), event.getDst(), new BroadcastedMessage(event.getPayload(),seqnum,event.getDst(),event.getSrc())), beb);
		}
	};
	
	///Not really a good use of context/content but I want that patternmatching 
	private ClassMatchedHandler<BroadcastedMessage, BEDeliver> handleBEDeliver = new ClassMatchedHandler<BroadcastedMessage, BEDeliver>() {
		@Override
		public void handle(BroadcastedMessage content, BEDeliver context) {
			BroadcastedMessage last =lastDelivered.get(content.getSrc()); 
			if(last == null || last.getSeqnum() < content.getSeqnum() ){
				if(suspected.contains(content.getSrc())){
					trigger(new BEBroadcast(content.getSrc(), content.getDst(), content), beb);
				}
				lastDelivered.put(content.getSrc(), content);
				trigger(new Deliver(content.getSrc(), content.getPayload()), rrb); 
				// deliver at the end will add some uniformity I guess, that cannot be bad
			}
		}
	};
	
	private Handler<Suspect> handleSuspect = new Handler<Suspect>() {
		@Override
		public void handle(Suspect event) {
			suspected.add(event.getSource());
			BroadcastedMessage last = lastDelivered.get(event.getSource());
			if(last != null){
				trigger(new BEBroadcast(last.getSrc(), last.getDst(), last), beb);
				//we trust our FIFO channel here, we just rebroadcast the last message seen from the supposed crashed
				//node, the previous one should have been delivered before to all other node.
			}
		}
	};

	private Handler<Restore> handleRestore = new Handler<Restore>() {
		@Override
		public void handle(Restore event) {
			suspected.remove(event.getSource());
		}
	};
	

}
