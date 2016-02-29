package simon.sormain.KeyValueStore.eld;

import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.network.TAddress;

public class Trust implements KompicsEvent {

	private final TAddress leader;

	public Trust(TAddress leader) {
		this.leader = leader;
	}

	public TAddress getLeader() {
		return leader;
	}
}
