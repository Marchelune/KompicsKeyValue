package simon.sormain.KeyValueStore.epfd;

import java.util.Set;

import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;


public class EpfdInit extends Init<Epfd> {

	private final TAddress selfAddress;
	private final Set<TAddress> allAddresses;
	private final long initialDelay;
	private final long deltaDelay;

	public EpfdInit(TAddress selfAddress, Set<TAddress> allAddresses, long initialDelay, long deltaDelay) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
		this.initialDelay = initialDelay;
		this.deltaDelay = deltaDelay;
	}

	public TAddress getSelfAddress() {
		return selfAddress;
	}

	public Set<TAddress> getAllAddresses() {
		return allAddresses;
	}

	public final long getInitialDelay() {
		return initialDelay;
	}
	
	public final long getDeltaDelay() {
		return deltaDelay;
	}
}
