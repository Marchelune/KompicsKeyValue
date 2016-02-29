package simon.sormain.KeyValueStore.app;

import se.sics.kompics.PortType;

public class RouterPort extends PortType {

	{
		indication(Operation.class);
	}

}
