package simon.sormain.KeyValueStore.app;

import java.io.Serializable;

import se.sics.kompics.KompicsEvent;


/**
 * Class to inform the client of the success (or fail) of its operation on the kv store.
 * If the operation succeed, the result is the result of the read operation
 * (a write return the written value)
 * @author remi
 *
 */
public class OperationACK implements Serializable, KompicsEvent {

	private static final long serialVersionUID = 3842299044043747136L;
	private final Operation op;
	private final Status status;
	private final String result;
	
	public OperationACK(Operation op, Status status, String result) {
		super();
		this.op = op;
		this.status = status;
		this.result = result;
	}

	public Operation getOp() {
		return op;
	}

	public Status getStatus() {
		return status;
	}

	public String getResult() {
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
 		boolean retValue = false;
    	if (obj instanceof OperationACK) {
			OperationACK e  = (OperationACK)obj;
			retValue =  op.equals(e.getOp());
		}
		return retValue;
	}
	
	@Override
    public int hashCode() {
        return 1+op.hashCode();
    }
	

}
