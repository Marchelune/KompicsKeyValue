package simon.sormain.KeyValueStore.tob;

import java.util.Set;
import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;

public class TobInit extends Init<Tob> {

	private final TAddress selfAddress;
	private final Set<TAddress> allAddresses;

	public TobInit(TAddress selfAddress, Set<TAddress> allAddresses) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
	}
	
	public TAddress getSelfAddress() {
		return selfAddress;
	}
	
	public Set<TAddress> getAllAddresses() {
		return allAddresses;
	}
}
