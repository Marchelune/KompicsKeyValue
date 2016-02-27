package simon.sormain.KeyValueStore.tob;

import se.sics.kompics.PortType;

public class TotalOrderBroadcastPort extends PortType {
	{
		indication(TobDeliver.class);
		request(TobBroadcast.class);
	}
}
