package simon.sormain.KeyValueStore.rBroadcast;


import java.util.Set;

import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.network.TAddress;

public class BroadcastedMessage implements KompicsEvent {
	

	private final KompicsEvent payload;
	private final int seqnum ;
	private final Set<TAddress> dst;
	private final TAddress src;
	
	public BroadcastedMessage(KompicsEvent payload, int seqnum, Set<TAddress> dst, TAddress src) {
		this.payload = payload;
		this.seqnum = seqnum;
		this.dst = dst;
		this.src = src;
	}
	
	public Set<TAddress> getDst() {
		return dst;
	}

	public TAddress getSrc() {
		return src;
	}

	public int getSeqnum() {
		return seqnum;
	}

	public KompicsEvent getPayload() {
		return payload;
	}
}
