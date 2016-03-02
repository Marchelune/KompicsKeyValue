package simon.sormain.KeyValueStore.eld;

import java.util.HashMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Eventual leader election component based on an epfd, slightly customized.
 * Election based on minimal rank.
 * @author remi
 *
 */
public class Omega extends ComponentDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(Omega.class);
	

	private Positive<EventuallyPerfectFailureDetectorPort> epfd = requires(EventuallyPerfectFailureDetectorPort.class);
	private Negative<EventualLeaderDetectorPort> eld = provides(EventualLeaderDetectorPort.class);
	
	private TreeMap<Integer, TAddress> allAddresses;
	private HashMap<TAddress, Integer> allRanks = new HashMap<TAddress, Integer>();
	private TAddress currentLeader;
	
	public Omega(OmegaInit init) {
		logger.info("Creating Omega.");
		subscribe(handleStart, control);
		subscribe(handleSuspect, epfd);
		subscribe(handleRestore, epfd);
		
		allAddresses = init.getAllAddresses();
		for (Integer i : allAddresses.keySet()) allRanks.put(allAddresses.get(i), i);
	}
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("Omega started." );
			currentLeader = allAddresses.firstEntry().getValue();
			trigger(new Trust(currentLeader), eld);
		}
	};
	
	private Handler<Suspect> handleSuspect = new Handler<Suspect>() {
		@Override
		public void handle(Suspect event) {
			allAddresses.remove(allRanks.get(event.getSuspected()));
			check();
		}
	};
	
	private Handler<Restore> handleRestore = new Handler<Restore>() {
		@Override
		public void handle(Restore event) {
			allAddresses.put(allRanks.get(event.getRestored()), event.getRestored());
			check();
		}
	};
	
	/**
	 * Check if the last trusted leader is still the good one, triggers a trust event if not.
	 */
	private void check(){
		TAddress tempLeader = allAddresses.firstEntry().getValue();
		if(!tempLeader.equals(currentLeader)){
			currentLeader = tempLeader;
			trigger(new Trust(currentLeader), eld);
			
		}
	}

	
	

}
