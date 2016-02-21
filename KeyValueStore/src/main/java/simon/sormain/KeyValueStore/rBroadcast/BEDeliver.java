package simon.sormain.KeyValueStore.rBroadcast;


import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import simon.sormain.KeyValueStore.network.TAddress;

public class BEDeliver implements KompicsEvent , PatternExtractor<Class, KompicsEvent>{
	

	private final KompicsEvent payload;

	public BEDeliver( KompicsEvent payload) {
		this.payload = payload;
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
