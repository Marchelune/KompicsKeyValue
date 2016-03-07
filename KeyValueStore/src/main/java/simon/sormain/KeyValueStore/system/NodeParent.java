package simon.sormain.KeyValueStore.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeMap;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.epfd.EpfdInit;
import simon.sormain.KeyValueStore.epfd.EventuallyPerfectFailureDetectorPort;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastComponent;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;
import simon.sormain.KeyValueStore.tob.Tob;
import simon.sormain.KeyValueStore.tob.TobInit;
import simon.sormain.KeyValueStore.tob.TotalOrderBroadcastPort;
import simon.sormain.KeyValueStore.app.Router;
import simon.sormain.KeyValueStore.app.RouterInit;
import simon.sormain.KeyValueStore.app.RouterPort;
import simon.sormain.KeyValueStore.app.Store;
import simon.sormain.KeyValueStore.app.StoreInit;
import simon.sormain.KeyValueStore.asc.AbortableSequenceConsensusPort;
import simon.sormain.KeyValueStore.asc.MultiPaxos;
import simon.sormain.KeyValueStore.asc.MultiPaxosInit;
import simon.sormain.KeyValueStore.converters.MapRanges;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.eld.EventualLeaderDetectorPort;
import simon.sormain.KeyValueStore.eld.Omega;
import simon.sormain.KeyValueStore.eld.OmegaInit;



public class NodeParent extends ComponentDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(NodeParent.class); //test

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    
	public NodeParent() {
		TAddress selfAddress = config().getValue("keyvaluestore.self.addr", TAddress.class);
		int selfRank = config().getValue("keyvaluestore.self.rank", Integer.class);
		MapRanks mRanks = config().getValue("keyvaluestore.self.ranks", MapRanks.class);
		TreeMap<Integer, TAddress> ranks = mRanks.getMap();
		logger.info("{} ranks : {}", new Object[]{selfAddress, ranks}); // test
		MapRanges mRanges = config().getValue("keyvaluestore.self.ranges", MapRanges.class);
		HashMap<int[], Set<TAddress>> Ranges = mRanges.getMap();
		

		//
		Set<int[]> keys = Ranges.keySet();
	    Iterator<int[]> iterator = keys.iterator();
	    while(iterator.hasNext()) {
	    	int[] setElement = iterator.next();
	    	logger.info("range :{}  addr: {}", new Object[]{Arrays.toString(setElement), Ranges.get(setElement)}); //test
	    }
	    //
		
		
		long initialDelay = config().getValue("keyvaluestore.epfd.initDelay", Long.class);
		long deltaDelay = config().getValue("keyvaluestore.epfd.deltaDelay", Long.class);
		
		// Get all addrs using Ranks
		HashSet<TAddress> alladdr = new HashSet<TAddress>(ranks.values());
		
		
		
		//create and connect all components except timer and network
        Component epfd = create(Epfd.class, new EpfdInit(selfAddress, alladdr, initialDelay, deltaDelay)); 
        Component beb = create(BEBroadcastComponent.class, Init.NONE);
        Component asc = create(MultiPaxos.class, new MultiPaxosInit(selfAddress, selfRank, alladdr)); //TODO rank
        Component eld = create(Omega.class, new OmegaInit(ranks)); 
        Component routy = create(Router.class, new RouterInit(Ranges, selfAddress));
        Component tob = create(Tob.class, new TobInit(selfAddress, alladdr));
        Component app = create(Store.class, new StoreInit(selfAddress));


      //connect required internal components to network and timer
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        
        connect(beb.getNegative(Network.class), network, Channel.TWO_WAY);
        
        connect(asc.getNegative(Network.class), network, Channel.TWO_WAY);
        
        connect(routy.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(routy.getNegative(BEBroadcastPort.class), beb.getPositive(BEBroadcastPort.class), Channel.TWO_WAY);
        
        connect(eld.getNegative(EventuallyPerfectFailureDetectorPort.class),epfd.getPositive(EventuallyPerfectFailureDetectorPort.class), Channel.TWO_WAY);
        
        connect(tob.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(tob.getNegative(EventualLeaderDetectorPort.class),eld.getPositive(EventualLeaderDetectorPort.class), Channel.TWO_WAY);
        connect(tob.getNegative(AbortableSequenceConsensusPort.class),asc.getPositive(AbortableSequenceConsensusPort.class), Channel.TWO_WAY);
        
        connect(app.getNegative(TotalOrderBroadcastPort.class),tob.getPositive(TotalOrderBroadcastPort.class), Channel.TWO_WAY);
        connect(app.getNegative(RouterPort.class),routy.getPositive(RouterPort.class), Channel.TWO_WAY);
        connect(app.getNegative(Network.class), network, Channel.TWO_WAY);
        
	}
}
