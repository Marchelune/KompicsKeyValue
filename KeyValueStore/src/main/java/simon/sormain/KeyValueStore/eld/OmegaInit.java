package simon.sormain.KeyValueStore.eld;


import java.util.TreeMap;

import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;

public class OmegaInit extends Init<Omega> {


	private final TreeMap<Integer, TAddress> allAddresses;

	public OmegaInit(TreeMap<Integer, TAddress> allAddresses) {
		super();
		this.allAddresses = allAddresses;
	}

	public TreeMap<Integer, TAddress> getAllAddresses() {
		return allAddresses;
	}
	

	
}
