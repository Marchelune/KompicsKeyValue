package simon.sormain.KeyValueStore.sim.tob;

import java.util.HashSet;
import java.util.Iterator;

import simon.sormain.KeyValueStore.app.Operation;

public class OpSet {
	HashSet<Operation> set;
	
	public OpSet(){
		set = new HashSet<Operation>();
	}
	
	public HashSet<Operation> getSet(){
		return this.set;
	}
	
	public boolean add(Operation op){
		return set.add(op);
	}
	
	public boolean contains(Operation op){
		return set.contains(op);
	}
	
	public Iterator<Operation> iterator(){
		return set.iterator();
	}
	
	public int size(){
		return set.size();
	}
}
