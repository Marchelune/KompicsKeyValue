package simon.sormain.KeyValueStore.epfd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.network.TAddress;


public class Epfd extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Epfd.class);
	
	//Ports
	private Positive<Timer> timer = requires(Timer.class);
	Positive<Network> net = requires(Network.class);
	private Negative<EventuallyPerfectFailureDetectorPort> epfd = provides(EventuallyPerfectFailureDetectorPort.class);
	
	private int seqnum;
	private HashSet<TAddress> alive;
	private HashSet<TAddress> suspected;
	private long delay;

	private final TAddress selfAddress;
	private final Set<TAddress> allAddresses;
	private final long initialDelay;
	private final long deltaDelay;
	
	public Epfd(EpfdInit init) {
		subscribe(handleStart, control);
		subscribe(handleCheckTimeout, timer);
		subscribe(handleHeartbeatReplyMessage, net);
		subscribe(handleHeartbeatRequestMessage, net);
		
		selfAddress = init.getSelfAddress();
		allAddresses = init.getAllAddresses();
		initialDelay = init.getInitialDelay();
		deltaDelay = init.getDeltaDelay();
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		public void handle(Start event) {
			logger.info("EPFD starting ...");
			seqnum = 0;
			suspected = new HashSet<TAddress>();
			alive = new HashSet<TAddress>(allAddresses);
			delay = initialDelay;
			//startTimer(delay, Check)
			ScheduleTimeout st = new ScheduleTimeout(delay);
			st.setTimeoutEvent(new CheckTimeout(st));
			trigger(st, timer);
		}
	};
	
	private Handler<Timeout> handleCheckTimeout = new Handler<Timeout>() {
		@Override
		public void handle(Timeout event) {
			HashSet<TAddress> intersection = alive;
			intersection.retainAll(suspected);
			if(!intersection.isEmpty()) delay += deltaDelay;
			seqnum++;
			for(Iterator<TAddress> i = allAddresses.iterator(); i.hasNext() ;) {
				TAddress p = i.next();
				if(!(alive.contains(p)) && !(suspected.contains(p)) ){
					suspected.add(p);
					trigger(new Suspect(p), epfd);
				}else if(intersection.contains(p)){
					suspected.remove(p);
					trigger(new Restore(p), epfd);
				}
				trigger(new HeartbeatRequestMessage(selfAddress,p), net);
			}
			
			alive = new HashSet<TAddress>();
			ScheduleTimeout st = new ScheduleTimeout(delay);
			st.setTimeoutEvent(new CheckTimeout(st));
			trigger(st, timer);
		}
	};
	
	private Handler<HeartbeatRequestMessage> handleHeartbeatRequestMessage = new Handler<HeartbeatRequestMessage>() {
		public void handle(HeartbeatRequestMessage event) {
			trigger (new HeartbeatReplyMessage(selfAddress, event.getSource()), net);
		}
	}; 
	
	private Handler<HeartbeatReplyMessage> handleHeartbeatReplyMessage = new Handler<HeartbeatReplyMessage>() {
		public void handle(HeartbeatReplyMessage event) {
			TAddress p = event.getSource();
			if(suspected.contains(p)) alive.add(p);
		}
	};
}