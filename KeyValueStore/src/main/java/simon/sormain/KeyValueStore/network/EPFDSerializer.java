package simon.sormain.KeyValueStore.network;


import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import java.net.InetAddress;
import java.net.UnknownHostException;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import simon.sormain.KeyValueStore.epfd.HeartbeatReplyMessage;
import simon.sormain.KeyValueStore.epfd.HeartbeatRequestMessage;

public class EPFDSerializer implements Serializer {

    private static final byte HBREQ = 1;
    private static final byte HBREPLY = 2;
    
   

    public int identifier() {
    	return 200;
    }

    public void toBinary(Object o, ByteBuf buf) {
    	if (o instanceof HeartbeatRequestMessage) {
    		HeartbeatRequestMessage hbReq = (HeartbeatRequestMessage) o;
    		buf.writeByte(HBREQ); // 1 byte
    		buf.writeShort(hbReq.getSeqnum()); // we only need 2 bytes here
    		// total 3 bytes
    	} else if (o instanceof HeartbeatReplyMessage) {
    		HeartbeatReplyMessage hbReply = (HeartbeatReplyMessage) o;
    		buf.writeByte(HBREPLY); // 1 byte
    		buf.writeShort(hbReply.getSeqnum()); // we only need 2 bytes here
    		// total 3 bytes
    	}
    }

    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
    	byte type = buf.readByte(); // 1 byte
    	switch (type) {
    	case HBREQ:
    		int seqnum = buf.readUnsignedShort(); // 3 bytes total, check
    		return new HeartbeatRequestMessage(seqnum);
    	case HBREPLY:
    		int seq = buf.readUnsignedShort(); // 3 bytes total, check
    		return new HeartbeatReplyMessage(seq);
    		
    	}
    	return null;
    }
}


