package simon.sormain.KeyValueStore.rBroadcast;


import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.network.TAddress;

public class Deliver implements KompicsEvent {
	
	private final TAddress src;
	private final KompicsEvent payload;

	public Deliver( TAddress src, KompicsEvent payload) {
		this.src = src;
		this.payload = payload;
	}

	public TAddress getSrc() {
		return src;
	}

	public KompicsEvent getPayload() {
		return payload;
	}
}
