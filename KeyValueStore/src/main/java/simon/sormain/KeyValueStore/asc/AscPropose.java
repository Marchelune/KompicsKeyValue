package simon.sormain.KeyValueStore.asc;


import se.sics.kompics.KompicsEvent;

public class AscPropose implements KompicsEvent {
	
	private final Object value;
	
	public AscPropose(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}
