package simon.sormain.KeyValueStore.app;

import simon.sormain.KeyValueStore.network.TAddress;

public class PutOperation extends Operation {
	
	private static final long serialVersionUID = 8826523849729195116L;
	private final String value;

	public PutOperation(TAddress client, int uniqueSequenceNumber, int key, String value) {
		super(client, uniqueSequenceNumber, key);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "PUT ("+ Integer.toString(this.getKey()) + "," + value + ")";
	}

}
