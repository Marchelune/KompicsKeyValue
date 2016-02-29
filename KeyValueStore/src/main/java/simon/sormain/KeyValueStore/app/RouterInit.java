package simon.sormain.KeyValueStore.app;


import java.util.HashMap;
import java.util.Set;

import se.sics.kompics.Init;
import simon.sormain.KeyValueStore.network.TAddress;

public class RouterInit extends Init<Router>{


	private final HashMap<int[], Set<TAddress>> allRanges;
	private final TAddress self;
	
	/**
	 * 
	 * @param allRanges each range should be an array with inclusive lower and exclusive higher limit [a;b[ in the two first cells
	 * @param self
	 */
	public RouterInit(HashMap<int[], Set<TAddress>> allRanges, TAddress self) {
		super();
		this.allRanges = allRanges;
		this.self = self;
	}
	
	public HashMap<int[], Set<TAddress>> getAllRanges() {
		return allRanges;
	}
	public TAddress getSelf() {
		return self;
	}
	
}
