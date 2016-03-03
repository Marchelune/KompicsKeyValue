package simon.sormain.KeyValueStore.sim.tob;

import java.util.HashSet;
import java.util.TreeMap;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.asc.AbortableSequenceConsensusPort;
import simon.sormain.KeyValueStore.asc.MultiPaxos;
import simon.sormain.KeyValueStore.asc.MultiPaxosInit;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.eld.EventualLeaderDetectorPort;
import simon.sormain.KeyValueStore.eld.Omega;
import simon.sormain.KeyValueStore.eld.OmegaInit;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.epfd.EpfdInit;
import simon.sormain.KeyValueStore.epfd.EventuallyPerfectFailureDetectorPort;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.tob.Tob;
import simon.sormain.KeyValueStore.tob.TobInit;

public class NodeParenttob extends ComponentDefinition {
	
    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

	public NodeParenttob() {
		TAddress selfAddress = config().getValue("keyvaluestore.self.addr", TAddress.class);
		int selfRank = config().getValue("keyvaluestore.self.rank", Integer.class);
		MapRanks mRanks = config().getValue("keyvaluestore.self.ranks", MapRanks.class);
		TreeMap<Integer, TAddress> Ranks = mRanks.getMap();
		
		long initialDelay = config().getValue("keyvaluestore.epfd.initDelay", Long.class);
		long deltaDelay = config().getValue("keyvaluestore.epfd.deltaDelay", Long.class);
		
		// Get all addrs using Ranks
		HashSet<TAddress> alladdr = new HashSet<TAddress>(Ranks.values());
		
		//create and connect all components except timer and network
        Component epfd = create(Epfd.class, new EpfdInit(selfAddress, alladdr, initialDelay, deltaDelay)); 
        Component asc = create(MultiPaxos.class, new MultiPaxosInit(selfAddress, selfRank, alladdr)); //TODO rank
        Component eld = create(Omega.class, new OmegaInit(Ranks)); 
        Component tob = create(Tob.class, new TobInit(selfAddress, alladdr));
        
      //connect required internal components to network and timer
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        
        connect(asc.getNegative(Network.class), network, Channel.TWO_WAY);
        
        connect(eld.getNegative(EventuallyPerfectFailureDetectorPort.class),epfd.getPositive(EventuallyPerfectFailureDetectorPort.class), Channel.TWO_WAY);
        
        connect(tob.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(tob.getNegative(EventualLeaderDetectorPort.class),eld.getPositive(EventualLeaderDetectorPort.class), Channel.TWO_WAY);
        connect(tob.getNegative(AbortableSequenceConsensusPort.class),asc.getPositive(AbortableSequenceConsensusPort.class), Channel.TWO_WAY);
	}
}
