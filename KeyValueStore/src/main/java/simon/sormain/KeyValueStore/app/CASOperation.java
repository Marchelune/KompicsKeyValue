package simon.sormain.KeyValueStore.app;

import simon.sormain.KeyValueStore.network.TAddress;

public class CASOperation extends Operation {

	private static final long serialVersionUID = 5055353619480517679L;
	private final String referenceValue;
	private final String newValue;

	public CASOperation(TAddress client, int uniqueSequenceNumber, int key, String referenceValue, String newValue) {
		super(client, uniqueSequenceNumber, key);
		this.referenceValue = referenceValue;
		this.newValue = newValue;
	}

	public String getReferenceValue() {
		return referenceValue;
	}

	public String getNewValue() {
		return newValue;
	}

	@Override
	public String toString() {
		return "CAS ("+ Integer.toString(this.getKey()) + "," + referenceValue + "," + newValue + ")";
	}

}
