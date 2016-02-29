package simon.sormain.KeyValueStore.app;

import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;

public class StoreInit extends Init<Store>{

	private final TAddress selfAddress;

	public StoreInit(TAddress selfAddress) {
		super();
		this.selfAddress = selfAddress;
	}

	public TAddress getSelfAddress() {
		return selfAddress;
	}
	

}
