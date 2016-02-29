package simon.sormain.KeyValueStore.asc;

import se.sics.kompics.PortType;

public class AbortableSequenceConsensusPort extends PortType {
	{
		indication(AscDecide.class);
		indication(AscAbort.class);
		request(AscPropose.class);
	}
}
