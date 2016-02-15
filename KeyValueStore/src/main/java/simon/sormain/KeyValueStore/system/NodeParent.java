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




public class NodeParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    
	public NodeParent() {
		//create and connect all components except timer and network
        Component epfd = create(Epfd.class, Init.NONE); //TODO

      //connect required internal components to network and timer
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
	}
}
