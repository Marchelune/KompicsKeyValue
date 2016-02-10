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
import se.sics.kompics.ClassMatchedHandler;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
import se.sics.kompics.network.Transport;

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
				trigger(new TMessage(selfAddress, p, Transport.TCP, new HeartbeatRequestMessage(seqnum)), net);
			}
			
			alive = new HashSet<TAddress>();
			ScheduleTimeout st = new ScheduleTimeout(delay);
			st.setTimeoutEvent(new CheckTimeout(st));
			trigger(st, timer);
		}
	};
	
	ClassMatchedHandler<HeartbeatRequestMessage, TMessage> handleHeartbeatRequestMessage = new ClassMatchedHandler<HeartbeatRequestMessage, TMessage>() {
		public void handle(HeartbeatRequestMessage content, TMessage context) {
			trigger(new TMessage(selfAddress, context.getSource(), Transport.TCP, new HeartbeatReplyMessage(content.getSeqnum())), net);
		}
	};
	
	ClassMatchedHandler<HeartbeatReplyMessage, TMessage> handleHeartbeatReplyMessage = new ClassMatchedHandler<HeartbeatReplyMessage, TMessage>() {
		public void handle(HeartbeatReplyMessage content, TMessage context) {
			TAddress p = context.getSource();
			if(suspected.contains(p) || seqnum == content.getSeqnum()) alive.add(p);
		}
	};
}