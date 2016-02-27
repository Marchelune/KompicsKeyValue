package simon.sormain.KeyValueStore.asc;

import java.io.Serializable;
import java.util.ArrayList;

import se.sics.kompics.KompicsEvent;

public class PrepareAckMessage implements KompicsEvent, Serializable {

	private static final long serialVersionUID = 1L;

	private final int timestamp;
	private final int acceptorTimestamp;
	private final ArrayList<Object> suffix;
	private final int decidedLength;
	private final int logicalClock;
	
	public PrepareAckMessage(int timestamp, int acceptorTimestamp, ArrayList<Object> suffix, int decidedLength,
			int logicalClock) {
		this.timestamp = timestamp;
		this.acceptorTimestamp = acceptorTimestamp;
		this.suffix = suffix;
		this.decidedLength = decidedLength;
		this.logicalClock = logicalClock;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public int getAcceptorTimestamp() {
		return acceptorTimestamp;
	}
	public ArrayList<Object> getSuffix() {
		return suffix;
	}
	public int getDecidedLength() {
		return decidedLength;
	}
	public int getLogicalClock() {
		return logicalClock;
	}
	
	

}
