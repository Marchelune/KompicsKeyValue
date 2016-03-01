package simon.sormain.KeyValueStore.converters;

import java.util.*;
import simon.sormain.KeyValueStore.network.*;

public class SetTAddress {
	private HashSet<TAddress> set;
	
	public SetTAddress() {
		set = new HashSet<TAddress>();
	}
	
	public int size(){
		return set.size();
	}
	
	public boolean add(TAddress address){
		return set.add(address);
	}
	
	public HashSet<TAddress> get(){
		return set;
	}
}
