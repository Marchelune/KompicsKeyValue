package simon.sormain.KeyValueStore.epfd;

import se.sics.kompics.KompicsEvent;


public class HeartbeatReplyMessage implements KompicsEvent {

	private static final long serialVersionUID = -7678165393077733049L;

	private final int seqnum;
	
	 public HeartbeatReplyMessage(int seq) {
		 this.seqnum = seq;
	 }

	 public int getSeqnum(){
		 return this.seqnum;
	 }
}
