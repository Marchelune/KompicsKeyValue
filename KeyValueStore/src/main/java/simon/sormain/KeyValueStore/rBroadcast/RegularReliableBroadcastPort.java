package simon.sormain.KeyValueStore.rBroadcast;

import se.sics.kompics.PortType;

public class RegularReliableBroadcastPort extends PortType {

	{
		indication(Deliver.class);
		request(Broadcast.class);
	}

}
