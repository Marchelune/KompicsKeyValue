package simon.sormain.KeyValueStore.sim.app;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.client.Client;
import simon.sormain.KeyValueStore.client.ClientHost;
import simon.sormain.KeyValueStore.client.ClientInit;
import simon.sormain.KeyValueStore.client.ConsolePort;
import simon.sormain.KeyValueStore.network.TAddress;

public class ClientHostSimu extends ComponentDefinition {
	public ClientHostSimu() {
		
	    Positive<Network> network = requires(Network.class);
	    Positive<Timer> timer = requires(Timer.class);
		
		try {
			TAddress self = new TAddress(InetAddress.getByName("192.168.0.1"), 15000);
			TAddress kvStore = new TAddress(InetAddress.getByName("192.168.0.1"), 10000);
			
			//create and connect all components except timer and network
			Component putter = create(CreateCommandsComponent.class, new CreateCommandsInit("PUT",2000));
			Component getter = create(CreateCommandsComponent.class, new CreateCommandsInit("GET",4000));
			Component client = create(Client.class, new ClientInit(self, kvStore));
			
			//connect required internal components to network and timer
			connect(client.getNegative(Network.class), network, Channel.TWO_WAY);
			connect(client.getNegative(ConsolePort.class) , putter.getPositive(ConsolePort.class), Channel.TWO_WAY);
			connect(client.getNegative(ConsolePort.class) , getter.getPositive(ConsolePort.class), Channel.TWO_WAY);
			
			connect(putter.getNegative(Timer.class), timer, Channel.TWO_WAY);
			connect(getter.getNegative(Timer.class), timer, Channel.TWO_WAY);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}

	
}
