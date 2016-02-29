package simon.sormain.KeyValueStore.tob;

import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.app.Operation;

public class TobBroadcast implements KompicsEvent {

	private final Operation op;

	public TobBroadcast(Operation op) {
		this.op = op;
	}

	public Operation getOp() {
		return op;
	}


}
