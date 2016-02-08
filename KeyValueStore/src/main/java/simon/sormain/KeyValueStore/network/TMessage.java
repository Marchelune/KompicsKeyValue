package simon.sormain.KeyValueStore.network;

import se.sics.kompics.network.Msg;
import se.sics.kompics.network.Transport;

public abstract class TMessage implements Msg<TAddress, THeader> {
    
    public final THeader header;
    
    public TMessage(TAddress src, TAddress dst) {
        this.header = new THeader(src, dst, Transport.TCP);
    }
    
    TMessage(THeader header) {
        this.header = header;
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
    
}
