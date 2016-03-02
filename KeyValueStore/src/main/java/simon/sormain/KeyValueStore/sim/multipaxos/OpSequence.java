package simon.sormain.KeyValueStore.sim.multipaxos;

import java.util.ArrayList;
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
}
