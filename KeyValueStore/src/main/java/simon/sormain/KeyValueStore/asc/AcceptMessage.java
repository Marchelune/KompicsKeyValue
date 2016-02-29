package simon.sormain.KeyValueStore.asc;

import java.io.Serializable;
import java.util.ArrayList;

import se.sics.kompics.KompicsEvent;

public class AcceptMessage implements KompicsEvent,Serializable  {

	private static final long serialVersionUID = 1L;
	private final int timestamp;
	private final ArrayList<Object> value;
	private final int length;
	private final int logicalClock;
	
	public AcceptMessage(int timestamp, ArrayList<Object> value, int length, int logicalClock) {
		this.timestamp = timestamp;
		this.value = value;
		this.length = length;
		this.logicalClock = logicalClock;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	public ArrayList<Object> getValue() {
		return value;
	}
	public int getLength() {
		return length;
	}
	public int getLogicalClock() {
		return logicalClock;
	}
	
	

}
