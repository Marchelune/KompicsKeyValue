package simon.sormain.KeyValueStore.asc;

import java.io.Serializable;

import se.sics.kompics.KompicsEvent;

public class NackMessage implements KompicsEvent,Serializable  {

	private static final long serialVersionUID = 1L;
	private final int timestamp;
	private final int logicalClock;
	
	public NackMessage(int timestamp, int logicalClock) {
		this.timestamp = timestamp;
		this.logicalClock = logicalClock;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public int getLogicalClock() {
		return logicalClock;
	}
	
	

}
