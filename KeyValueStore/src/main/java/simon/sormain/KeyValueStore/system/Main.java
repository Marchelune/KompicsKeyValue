package simon.sormain.KeyValueStore.system;

import se.sics.kompics.Kompics;
import se.sics.kompics.config.Conversions;
import se.sics.kompics.network.netty.serialization.Serializers;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TAddressConverter;
import simon.sormain.KeyValueStore.epfd.HeartbeatReplyMessage;
import simon.sormain.KeyValueStore.epfd.HeartbeatRequestMessage;
import simon.sormain.KeyValueStore.network.EPFDSerializer;
import simon.sormain.KeyValueStore.network.NetSerializer;
import simon.sormain.KeyValueStore.network.SetTAddressConverter;
import simon.sormain.KeyValueStore.network.THeader;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.system.NodeParent;
import simon.sormain.KeyValueStore.system.NodeHost;

public class Main {
    static {
    	// register
        Serializers.register(new NetSerializer(), "netS");
        Serializers.register(new EPFDSerializer(), "epfdS");
        // map
        Serializers.register(TAddress.class, "netS");
        Serializers.register(THeader.class, "netS");
        Serializers.register(TMessage.class, "netS");
        Serializers.register(HeartbeatReplyMessage.class, "epfdS");
        Serializers.register(HeartbeatRequestMessage.class, "epfdS");
        // conversions
        Conversions.register(new TAddressConverter());
        Conversions.register(new SetTAddressConverter());
    }
    
    
    public static void main(String[] args) {
                Kompics.createAndStart(NodeLaunch.class, 2);
                System.out.println("Starting Node");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    System.exit(1);
                }
                Kompics.shutdown();
                System.exit(0);

    }
}
