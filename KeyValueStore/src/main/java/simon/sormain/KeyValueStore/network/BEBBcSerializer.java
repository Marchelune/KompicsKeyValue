package simon.sormain.KeyValueStore.network;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import java.net.InetAddress;
import java.net.UnknownHostException;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import simon.sormain.KeyValueStore.rBroadcast.*;


public class BEBBcSerializer extends NeedSerialAddr implements Serializer {
	
	private static final byte BEBDel = 1;

    public int identifier() {
    	return 300;
    }
	
    
    public void toBinary(Object o, ByteBuf buf) {
    	if (o instanceof BEDeliver) {
    		BEDeliver BEDel = (BEDeliver) o;
    		buf.writeByte(BEBDel); // 1 byte
    		addressToBinary(BEDel.getSrc(), buf); // 6 bytes
    		Serializers.toBinary(BEDel.getPayload(), buf);
    	} 
    }

    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
    	byte type = buf.readByte(); // 1 byte
    	if(type == BEBDel) {
    		TAddress src = addressFromBinary(buf); // 6 bytes
    		KompicsEvent payload = (KompicsEvent) Serializers.fromBinary(buf, Optional.absent()); // don't know what it is but KompicsEvent is the upper bound
    		return new BEDeliver(payload, src);
    	}
    	return null;
    }
}
