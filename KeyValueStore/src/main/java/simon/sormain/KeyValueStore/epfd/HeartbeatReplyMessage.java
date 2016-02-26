package simon.sormain.KeyValueStore.epfd;

import se.sics.kompics.KompicsEvent;


public class HeartbeatReplyMessage implements KompicsEvent {

	private final int seqnum;
	
	 public HeartbeatReplyMessage(int seq) {
		 this.seqnum = seq;
	 }

	 public int getSeqnum(){
		 return this.seqnum;
	 }
}
