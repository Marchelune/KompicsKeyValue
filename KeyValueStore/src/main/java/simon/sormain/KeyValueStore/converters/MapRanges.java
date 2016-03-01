package simon.sormain.KeyValueStore.converters;



import java.util.HashMap;
import java.util.Set;

import simon.sormain.KeyValueStore.network.TAddress;

public class MapRanges {
	private HashMap<int[], Set<TAddress>> ranges;
	
	public MapRanges() {
		ranges = new HashMap<int[], Set<TAddress>>();
	}
	
	public int size(){
		return ranges.size();
	}
	
	public HashMap<int[], Set<TAddress>> getMap() {
		return this.ranges;
	}
	
	public void put(int[] range, Set<TAddress> setAddr){
		ranges.put(range, setAddr);
	}
		
}
