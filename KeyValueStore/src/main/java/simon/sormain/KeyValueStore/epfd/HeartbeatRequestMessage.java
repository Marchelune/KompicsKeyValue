package simon.sormain.KeyValueStore.epfd;

import java.io.Serializable;

import se.sics.kompics.KompicsEvent;


public class HeartbeatRequestMessage implements KompicsEvent, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6424896729441080386L;
	private final int seqnum;
	
	 public HeartbeatRequestMessage(int seq) {
		 this.seqnum = seq;
	 }
	 
	 public int getSeqnum(){
		 return this.seqnum;
	 }
}
