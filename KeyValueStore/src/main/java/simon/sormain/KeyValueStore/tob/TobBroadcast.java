package simon.sormain.KeyValueStore.tob;

import se.sics.kompics.KompicsEvent;

public class TobBroadcast implements KompicsEvent {

	private final TobDeliver deliverEvent;

	public TobBroadcast(TobDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}

	public final TobDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
