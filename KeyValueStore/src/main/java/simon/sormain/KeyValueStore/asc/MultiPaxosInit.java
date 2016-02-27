package simon.sormain.KeyValueStore.asc;

import java.util.Set;

import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;

public class MultiPaxosInit extends Init<MultiPaxos> {

	private final TAddress selfAddress;
	private final Set<TAddress> allAddresses;
	private final int selfRank;

	public MultiPaxosInit(TAddress selfAddress, int selfRank,Set<TAddress> allAddresses) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
		this.selfRank = selfRank;
	}
	
	public int getSelfRank() {
		return selfRank;
	}

	public TAddress getSelfAddress() {
		return selfAddress;
	}

	public Set<TAddress> getAllAddresses() {
		return allAddresses;
	}
}
