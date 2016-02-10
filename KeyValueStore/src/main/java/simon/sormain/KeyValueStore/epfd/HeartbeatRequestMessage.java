package simon.sormain.KeyValueStore.epfd;

import se.sics.kompics.KompicsEvent;


public class HeartbeatRequestMessage implements KompicsEvent {

	private static final long serialVersionUID = 3830255588022005317L;
	
	private final int seqnum;
	
	 public HeartbeatRequestMessage(int seq) {
		 this.seqnum = seq;
	 }
	 
	 public int getSeqnum(){
		 return this.seqnum;
	 }
}
