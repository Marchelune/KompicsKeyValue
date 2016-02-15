package simon.sormain.KeyValueStore.system;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.network.TAddress;


public class NodeHost extends ComponentDefinition {
    
	public NodeHost() {
		
		TAddress self = config().getValue("keyvaluestore.self", TAddress.class);
		//create and connect all components except timer and network
		
		Component timer = create(JavaTimer.class, Init.NONE ); 
		Component network = create(NettyNetwork.class, new NettyInit(self));
        Component nodeParent = create(NodeParent.class, Init.NONE); //TODO

        connect(nodeParent.getNegative(Timer.class), timer.getPositive(Timer.class), Channel.TWO_WAY);
        connect(nodeParent.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
	}


}
