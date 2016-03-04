package simon.sormain.KeyValueStore.app;

import java.io.Serializable;
import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.network.TAddress;

/**
 * Class that should encapsulate all kind of operations on our kv-store.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author remi
 *
 */
public abstract class Operation implements KompicsEvent, Serializable, Comparable<Operation> {

	private static final long serialVersionUID = 1L;
	private final TAddress client;
	private final int uniqueSequenceNumber;
	private final int key;
	
	
	
	public Operation(TAddress client, int uniqueSequenceNumber, int key) {
		super();
		this.client = client;
		this.uniqueSequenceNumber = uniqueSequenceNumber;
		this.key = key;
	}
	
	public TAddress getAddress() {
		return client;
	}
	
	public int getKey() {
		return key;
	}

	public TAddress getClient() {
		return client;
	}
	public int getUniqueSequenceNumber() {
		return uniqueSequenceNumber;
	}

	public int compareTo(Operation arg0) {
		if (arg0.getClient().equals(client)){
			if(uniqueSequenceNumber == arg0.getUniqueSequenceNumber()) return 0;
			if(uniqueSequenceNumber < arg0.getUniqueSequenceNumber()) return -1;
			return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Operation){
			Operation arg0 = (Operation) obj;
			if (arg0.getClient().equals(client) &&
				uniqueSequenceNumber == arg0.getUniqueSequenceNumber()) return true;
		}
		return false;
	}
	
	@Override
    public int hashCode() {
        return uniqueSequenceNumber*key + client.hashCode();
    }

	@Override
	public abstract String toString();
	

}
