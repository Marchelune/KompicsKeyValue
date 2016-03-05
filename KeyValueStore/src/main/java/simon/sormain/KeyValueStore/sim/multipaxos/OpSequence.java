package simon.sormain.KeyValueStore.sim.multipaxos;

import java.util.ArrayList;
import java.util.Iterator;

import simon.sormain.KeyValueStore.app.Operation;

public class OpSequence {
	ArrayList<Operation> Sequence;
	
	public OpSequence(){
		Sequence = new ArrayList<Operation>();
	}
	
	public boolean add(Operation o){
		return Sequence.add(o);
	}
	
	public int size(){
		return Sequence.size();
	}
	
	public ArrayList<Operation> getSequence(){
		return this.Sequence;
	}
	
	public boolean contains(Operation op){
		return Sequence.contains(op);
	}
	
	public Iterator<Operation> iterator(){
		return Sequence.iterator();
	}
	
	public Operation get(int index){
		return Sequence.get(index);
	}
	
	// returns true is this is a prefix of opSeq
	public boolean prefixof(OpSequence opSeq){
		if(this.size() <= opSeq.size()){
			for(int i = 0; i<this.size(); i++){
				if(!this.get(i).equals(opSeq.get(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
