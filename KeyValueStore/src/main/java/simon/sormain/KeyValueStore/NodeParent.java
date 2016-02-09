package simon.sormain.KeyValueStore;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.epfd.EpfdInit;
import simon.sormain.KeyValueStore.network.TAddress;




public class NodeParent extends ComponentDefinition {

	public NodeParent(Init init) {
		Component timer = create(JavaTimer.class, init.NONE ); 
		Component network = create(NettyNetwork.class, new NettyInit(init.self));
        Component epfd = create(Epfd.class, new EpfdInit(null, null, 0, 0)); //TODO

        connect(epfd.getNegative(Timer.class), timer.getPositive(Timer.class), Channel.TWO_WAY);

        connect(epfd.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
	}


	
	public static class Init extends se.sics.kompics.Init<NodeParent> {

        public final TAddress self;
        public final TAddress master;

        public Init(TAddress self, TAddress master) {
            this.self = self;
            this.master = master;
        }
	}
}
