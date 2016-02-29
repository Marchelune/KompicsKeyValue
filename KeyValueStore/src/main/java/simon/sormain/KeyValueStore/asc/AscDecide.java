package simon.sormain.KeyValueStore.asc;


import se.sics.kompics.KompicsEvent;

public class AscDecide implements KompicsEvent  {

	private final Object value;
	
	public AscDecide(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}
