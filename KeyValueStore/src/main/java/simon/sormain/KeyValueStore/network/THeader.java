package simon.sormain.KeyValueStore.network;

import se.sics.kompics.network.Header;
import se.sics.kompics.network.Transport;

public class THeader implements Header<TAddress> {

    public final TAddress src;
    public final TAddress dst;
    public final Transport proto;

    public THeader(TAddress src, TAddress dst, Transport proto) {
        this.src = src;
        this.dst = dst;
        this.proto = proto;
    }

    public TAddress getSource() {
        return src;
    }

    public TAddress getDestination() {
        return dst;
    }

    public Transport getProtocol() {
        return proto;
    }

}
