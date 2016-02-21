package simon.sormain.KeyValueStore.rBroadcast;

import se.sics.kompics.PortType;

public class BEBroadcastPort extends PortType {

	{
		indication(BEDeliver.class);
		request(BEBroadcast.class);
	}

}
