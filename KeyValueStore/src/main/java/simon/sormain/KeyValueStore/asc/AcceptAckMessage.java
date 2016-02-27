package simon.sormain.KeyValueStore.asc;

import java.io.Serializable;
import java.util.ArrayList;

import se.sics.kompics.KompicsEvent;

public class AcceptAckMessage implements KompicsEvent, Serializable {

	private static final long serialVersionUID = 1L;

	private final int timestamp;
	private final int decidedLength;
	private final int logicalClock;
	
	public AcceptAckMessage(int timestamp, int decidedLength, int logicalClock) {
		super();
		this.timestamp = timestamp;
		this.decidedLength = decidedLength;
		this.logicalClock = logicalClock;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	public int getDecidedLength() {
		return decidedLength;
	}
	public int getLogicalClock() {
		return logicalClock;
	}
	
}
