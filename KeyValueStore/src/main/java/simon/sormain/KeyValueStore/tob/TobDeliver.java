package simon.sormain.KeyValueStore.tob;

import java.io.Serializable;


import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.network.TAddress;

/**
 * This class is kind of dummy but needed to differentiate operations sent on network port.
 * @author remi
 *
 */
public class TobDeliver implements Serializable, PatternExtractor<Class, Operation> {

	private static final long serialVersionUID = 1L;
	
	private final Operation op;
	
	public TobDeliver(Operation op) {
		this.op = op;
	}
	
	public Operation getOp() {
		return op;
	}

	public Class extractPattern() {
		return op.getClass();
	}

	public Operation extractValue() {
		return op;
	}

	
}
