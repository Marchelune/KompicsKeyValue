package simon.sormain.KeyValueStore.asc;

import java.io.Serializable;
import java.util.ArrayList;

import se.sics.kompics.KompicsEvent;

public class PrepareMessage implements KompicsEvent,Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int timestamp;
	private final int length;
	private final int logicalClock;
	
	public PrepareMessage(int timestamp, int length, int logicalClock) {
		this.timestamp = timestamp;
		this.length = length;
		this.logicalClock = logicalClock;
	}

	public int getTimestamp() {
		return timestamp;
	}


	public int getLength() {
		return length;
	}

	public int getLogicalClock() {
		return logicalClock;
	}
	
	
	

}
