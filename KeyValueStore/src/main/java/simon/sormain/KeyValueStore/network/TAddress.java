package simon.sormain.KeyValueStore.network;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import se.sics.kompics.network.Address;


/**
 * 
 * From Kompics tutorials
 *
 */
public class TAddress implements Address, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5977196620574973717L;
	private final InetSocketAddress isa;

    public TAddress(InetAddress addr, int port) {
        this.isa = new InetSocketAddress(addr, port);
    }

    public InetAddress getIp() {
        return this.isa.getAddress();
    }

    public int getPort() {
        return this.isa.getPort();
    }

    public InetSocketAddress asSocket() {
        return this.isa;
    }

    public boolean sameHostAs(Address other) {
        return this.isa.equals(other.asSocket());
    }

    // Not required but strongly recommended

    @Override
    public final String toString() {
        return isa.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.isa != null ? this.isa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TAddress other = (TAddress) obj;
        if (this.isa != other.isa && (this.isa == null || !this.isa.equals(other.isa))) {
            return false;
        }
        return true;
    }

}
