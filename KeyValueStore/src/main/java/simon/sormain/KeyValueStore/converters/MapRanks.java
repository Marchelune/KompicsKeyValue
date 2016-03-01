package simon.sormain.KeyValueStore.converters;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import simon.sormain.KeyValueStore.network.TAddress;

public class MapRanks {

	private TreeMap<Integer, TAddress> ranks;
	
	public MapRanks() {
		ranks = new TreeMap<Integer, TAddress>();
	}

	public int size(){
		return ranks.size();
	}
	
	public TreeMap<Integer, TAddress> getMap() {
		return this.ranks;
	}
	
	public void put(Integer rank, TAddress Addr){
		ranks.put(rank, Addr);
	}
}
