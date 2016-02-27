package simon.sormain.KeyValueStore.asc;

/**
 * This class encapsulates a pair of accepted value and the round number sent by 
 * the acceptor. Mainly used for the readList in multipaxos component.
 * @author remi
 *
 */
public class ProposedPair {

	private final int AcceptedValueRound;
	private final Object acceptedValue;
	
	/**
	 * 
	 * @param acceptedValueRound Round number in which the value is accepted
	 * @param acceptedValue Accepted value
	 */
	public ProposedPair(int acceptedValueRound, Object acceptedValue) {
		super();
		AcceptedValueRound = acceptedValueRound;
		this.acceptedValue = acceptedValue;
	}

	public int getAcceptedValueRound() {
		return AcceptedValueRound;
	}

	public Object getAcceptedValue() {
		return acceptedValue;
	}
	
	

}
