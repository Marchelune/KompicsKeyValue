package simon.sormain.KeyValueStore.rBroadcast;


import java.io.Serializable;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import simon.sormain.KeyValueStore.network.TAddress;

public class BEDeliver implements KompicsEvent , Serializable, PatternExtractor<Class, KompicsEvent>{
	

	private static final long serialVersionUID = 1L;
	private final KompicsEvent payload;
	private final TAddress src;


	
	public BEDeliver(KompicsEvent payload, TAddress src) {
		this.payload = payload;
		this.src = src;
	}


	public TAddress getSrc() {
		return src;
	}

	public KompicsEvent getPayload() {
		return payload;
	}


	public Class extractPattern() {
		return payload.getClass();
	}


	public KompicsEvent extractValue() {
		return payload;
	}
}
