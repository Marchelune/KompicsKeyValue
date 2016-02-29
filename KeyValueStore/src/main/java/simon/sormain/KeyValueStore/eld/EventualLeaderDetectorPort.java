package simon.sormain.KeyValueStore.eld;

import se.sics.kompics.PortType;

public class EventualLeaderDetectorPort extends PortType {
	{
		indication(Trust.class);
	}
}
