package simon.sormain.KeyValueStore.system;

import java.util.HashSet;

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
import simon.sormain.KeyValueStore.network.SetTAddress;
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
import simon.sormain.KeyValueStore.eld.EventualLeaderDetectorPort;
import simon.sormain.KeyValueStore.eld.Omega;
import simon.sormain.KeyValueStore.eld.OmegaInit;



public class NodeParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    
	public NodeParent() {
		TAddress selfAddress = config().getValue("keyvaluestore.self", TAddress.class);
		HashSet<TAddress> alladdr = config().getValue("keyvaluestore.epfd.allAddr", SetTAddress.class).get();//still think this is weird :p
		long initialDelay = config().getValue("keyvaluestore.epfd.initDelay", Long.class);
		long deltaDelay = config().getValue("keyvaluestore.epfd.deltaDelay", Long.class);
		
		//create and connect all components except timer and network
        Component epfd = create(Epfd.class, new EpfdInit(selfAddress, alladdr, initialDelay, deltaDelay)); 
        Component beb = create(BEBroadcastComponent.class, Init.NONE);
        Component asc = create(MultiPaxos.class, new MultiPaxosInit(selfAddress, 0, alladdr)); //TODO rank
        Component eld = create(Omega.class, new OmegaInit(null)); //TODO ranks
        Component routy = create(Router.class, new RouterInit(null, selfAddress)); //TODO Ranges
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
        
	}
}
