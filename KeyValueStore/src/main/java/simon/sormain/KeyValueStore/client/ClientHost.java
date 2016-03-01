package simon.sormain.KeyValueStore.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import simon.sormain.KeyValueStore.network.TAddress;

/**
 * Launch this class to run a client.
 * @author remi
 *
 */
public class ClientHost extends ComponentDefinition {

	public ClientHost() {
		
		try {
			TAddress self = new TAddress(InetAddress.getByName("127.0.0.1"), 20423);
			TAddress kvStore = new TAddress(InetAddress.getByName("127.0.0.1"), 20424);
			
			Component network = create(NettyNetwork.class, new NettyInit(self));
			Component console = create(JavaConsole.class, Init.NONE);
			Component client = create(Client.class, new ClientInit(self, kvStore));
			
			connect(client.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
			connect(client.getNegative(ConsolePort.class) , console.getPositive(ConsolePort.class), Channel.TWO_WAY);
					
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Kompics.createAndStart(ClientHost.class);
        System.out.println("Starting Client");
	}

	

}
