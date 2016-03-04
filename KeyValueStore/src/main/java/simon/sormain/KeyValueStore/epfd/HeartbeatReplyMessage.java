package simon.sormain.KeyValueStore.epfd;

import java.io.Serializable;

import se.sics.kompics.KompicsEvent;


public class HeartbeatReplyMessage implements KompicsEvent, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -993674169735184260L;
	private final int seqnum;
	
	 public HeartbeatReplyMessage(int seq) {
		 this.seqnum = seq;
	 }

	 public int getSeqnum(){
		 return this.seqnum;
	 }
}
