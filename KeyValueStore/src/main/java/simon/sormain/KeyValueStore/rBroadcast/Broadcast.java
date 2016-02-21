package simon.sormain.KeyValueStore.rBroadcast;

import java.util.Set;
import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.network.TAddress;


public class Broadcast implements KompicsEvent {

	private TAddress src;
	private Set<TAddress> dst;
	private KompicsEvent payload;

	public Broadcast(TAddress src, Set<TAddress> dst, KompicsEvent payload) {
		this.src = src;
		this.dst = dst;
		this.payload = payload;
	}

	public TAddress getSrc() {
		return src;
	}

	public Set<TAddress> getDst() {
		return dst;
	}

	public KompicsEvent getPayload() {
		return payload;
	}



}
