package simon.sormain.KeyValueStore.system;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.network.SetTAddress;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastComponent;
import simon.sormain.KeyValueStore.asc.MultiPaxos;
import simon.sormain.KeyValueStore.asc.MultiPaxosInit;



public class NodeParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    
	public NodeParent() {
		TAddress selfAddress = config().getValue("keyvaluestore.self", TAddress.class);
		SetTAddress alladdr = config().getValue("keyvaluestore.epfd.allAddr", SetTAddress.class);//still think this is weird :p
		
		//create and connect all components except timer and network
        Component epfd = create(Epfd.class, Init.NONE); 
        Component beb = create(BEBroadcastComponent.class, Init.NONE);
        Component asc = create(MultiPaxos.class, new MultiPaxosInit(selfAddress, 0, alladdr.get())); //TODO rank


      //connect required internal components to network and timer
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(beb.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(asc.getNegative(Network.class), network, Channel.TWO_WAY);
        //connect(rb.getNegative(BEBroadcastPort.class), beb.getPositive(BEBroadcastPort.class), Channel.TWO_WAY);
        
	}
}
