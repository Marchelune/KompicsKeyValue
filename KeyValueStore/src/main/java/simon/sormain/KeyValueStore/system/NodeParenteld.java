package simon.sormain.KeyValueStore.system;

import java.util.HashSet;
import java.util.TreeMap;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.eld.Omega;
import simon.sormain.KeyValueStore.eld.OmegaInit;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.epfd.EpfdInit;
import simon.sormain.KeyValueStore.epfd.EventuallyPerfectFailureDetectorPort;
import simon.sormain.KeyValueStore.network.TAddress;

public class NodeParenteld extends ComponentDefinition{

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    
    public NodeParenteld(){
    	TAddress selfAddress = config().getValue("keyvaluestore.self.addr", TAddress.class);
		MapRanks mRanks = config().getValue("keyvaluestore.self.ranks", MapRanks.class);
		TreeMap<Integer, TAddress> Ranks = mRanks.getMap();
		// Get all addrs using Ranks
		HashSet<TAddress> alladdr = new HashSet<TAddress>(Ranks.values());
		long initialDelay = config().getValue("keyvaluestore.epfd.initDelay", Long.class);
		long deltaDelay = config().getValue("keyvaluestore.epfd.deltaDelay", Long.class);
		
		//create and connect all components except timer and network
        Component epfd = create(Epfd.class, new EpfdInit(selfAddress, alladdr, initialDelay, deltaDelay)); 
        Component eld = create(Omega.class, new OmegaInit(Ranks)); 
        
      //connect required internal components to network and timer
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        
        connect(eld.getNegative(EventuallyPerfectFailureDetectorPort.class),epfd.getPositive(EventuallyPerfectFailureDetectorPort.class), Channel.TWO_WAY);
    }
}
