package simon.sormain.KeyValueStore.tob;

import java.io.Serializable;


import se.sics.kompics.KompicsEvent;
import simon.sormain.KeyValueStore.network.TAddress;


public class TobDeliver implements KompicsEvent, Serializable {

	private static final long serialVersionUID = 1L;
	private TAddress source;

	public TobDeliver(TAddress source) {
		this.source = source;
	}
	
	public final TAddress getSource() {
		return source;
	}
}
