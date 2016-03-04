package simon.sormain.KeyValueStore.network;

import java.io.Serializable;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import se.sics.kompics.network.Msg;
import se.sics.kompics.network.Transport;

public class TMessage implements Msg<TAddress, THeader>, PatternExtractor<Class, KompicsEvent>, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7894724505105288438L;
	public final THeader header;
    public final KompicsEvent payload;

    public TMessage(TAddress src, TAddress dst, Transport protocol, KompicsEvent payload) {
        this.header = new THeader(src, dst, protocol);
        this.payload = payload;
    }

    TMessage(THeader header, KompicsEvent payload) {
        this.header = header;
        this.payload = payload;
    }

    public THeader getHeader() {
        return this.header;
    }

    public TAddress getSource() {
        return this.header.src;
    }

    public TAddress getDestination() {
        return this.header.dst;
    }

    public Transport getProtocol() {
        return this.header.proto;
    }

    public Class extractPattern() {
        return //(Class<Pong>) ???
        		payload.getClass();
    }

    public KompicsEvent extractValue() {
        return payload;
    }

}
