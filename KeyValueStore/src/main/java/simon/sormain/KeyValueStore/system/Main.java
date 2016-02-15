package simon.sormain.KeyValueStore.system;

import se.sics.kompics.Kompics;
import se.sics.kompics.config.Conversions;
import se.sics.kompics.network.netty.serialization.Serializers;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TAddressConverter;
import simon.sormain.KeyValueStore.network.THeader;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.system.NodeParent;
import simon.sormain.KeyValueStore.system.NodeHost;

public class Main {
    static {

        // conversions
        Conversions.register(new TAddressConverter());
    }
    
    
    public static void main(String[] args) {
                Kompics.createAndStart(NodeHost.class, 2);
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
