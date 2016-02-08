package simon.sormain.KeyValueStore.epfd;

import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;

public class HeartbeatReplyMessage extends TMessage {

	public HeartbeatReplyMessage(TAddress src, TAddress dst) {
		super(src, dst);
	}

	private static final long serialVersionUID = -7678165393077733049L;

	

}
