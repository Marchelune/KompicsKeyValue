package simon.sormain.KeyValueStore.client;



import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;

public class ClientInit extends Init<Client> {


	private final TAddress self;
	private final TAddress kvStore; //TAddress of any node of the store
	public ClientInit(TAddress self, TAddress kvStore) {
		super();
		this.self = self;
		this.kvStore = kvStore;
	}
	public TAddress getSelf() {
		return self;
	}
	public TAddress getKvStore() {
		return kvStore;
	}
	
	

	
}
