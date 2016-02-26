package simon.sormain.KeyValueStore.system;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
import se.sics.kompics.config.*;
import se.sics.kompics.config.Config.Builder;

public class NodeLaunch extends ComponentDefinition {

	public NodeLaunch() {
		
		TAddress self = null;
		try {
			self = new TAddress(InetAddress.getByName("192.168.1.4"), 45671);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Builder modifConfig = config().modify(id());
		modifConfig.setValue("keyvaluestore.self", ((Object) self));
		Component NodeHost1 = create(NodeHost.class, Init.NONE, modifConfig.finalise()); //TODO
		try {
			self = new TAddress(InetAddress.getByName("192.168.1.4"), 45672);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modifConfig = config().modify(id());
		modifConfig.setValue("keyvaluestore.self", ((Object) self));
		Component NodeHost2 = create(NodeHost.class, Init.NONE, modifConfig.finalise()); //TODO
	}
	
}
