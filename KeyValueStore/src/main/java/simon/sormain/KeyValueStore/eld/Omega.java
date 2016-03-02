package simon.sormain.KeyValueStore.eld;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.simulator.util.GlobalView;
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
			//Simu
			leaderChange();
			logger.info("{} I initially trust : {}", new Object[]{config().getValue("keyvaluestore.self.addr", TAddress.class), currentLeader});
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
			//Simu
			leaderChange();
			logger.info("{} I know trust : {}", new Object[]{config().getValue("keyvaluestore.self.addr", TAddress.class), currentLeader});
		}
	}

	//Simu
	private void leaderChange() {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        TAddress selfaddr= config().getValue("keyvaluestore.self.addr", TAddress.class);
        switch (selfaddr.getPort()) {
        case 10000 :
        	gv.setValue("simulation.leader1", currentLeader);
        	break;
        case 20000 :
        	gv.setValue("simulation.leader2", currentLeader);
        	break;
        case 30000 :
        	gv.setValue("simulation.leader3", currentLeader);
        	break;
        case 40000 :
        	gv.setValue("simulation.leader4", currentLeader);
        	break;
        case 50000 :
        	gv.setValue("simulation.leader5", currentLeader);
        	break;
    		
        }
	}
	

}
