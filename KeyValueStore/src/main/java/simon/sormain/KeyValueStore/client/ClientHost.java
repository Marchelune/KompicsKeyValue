package simon.sormain.KeyValueStore.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.config.Conversions;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.serialization.JavaSerializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import simon.sormain.KeyValueStore.converters.RangeConverter;
import simon.sormain.KeyValueStore.converters.RanksConverter;
import simon.sormain.KeyValueStore.converters.SetTAddressConverter;
import simon.sormain.KeyValueStore.converters.TAddressConverter;
import simon.sormain.KeyValueStore.epfd.HeartbeatReplyMessage;
import simon.sormain.KeyValueStore.epfd.HeartbeatRequestMessage;
import simon.sormain.KeyValueStore.network.EPFDSerializer;
import simon.sormain.KeyValueStore.network.NetSerializer;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.THeader;
import simon.sormain.KeyValueStore.network.TMessage;

/**
 * Launch this class to run a client.
 * @author remi
 *
 */
public class ClientHost extends ComponentDefinition {
	
    static {

        // conversions
        Conversions.register(new TAddressConverter());
        Conversions.register(new SetTAddressConverter());
        Conversions.register(new RangeConverter());
        Conversions.register(new RanksConverter());
    }

	public ClientHost() {
		
		try {
			TAddress self = new TAddress(InetAddress.getByName("192.168.1.3"), 44000);
			TAddress kvStore = new TAddress(InetAddress.getByName("192.168.1.3"), 45673);
			
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
