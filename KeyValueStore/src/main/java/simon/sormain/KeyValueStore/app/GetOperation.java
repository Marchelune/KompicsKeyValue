package simon.sormain.KeyValueStore.app;

import simon.sormain.KeyValueStore.network.TAddress;

public class GetOperation extends Operation {

	private static final long serialVersionUID = -1276766845995422408L;

	public GetOperation(TAddress client, int uniqueSequenceNumber, int key) {
		super(client, uniqueSequenceNumber, key);
	}

	@Override
	public String toString() {
		return "GET ("+ Integer.toString(this.getKey())  + ")";
	}

}
