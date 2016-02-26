package simon.sormain.KeyValueStore.rBroadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;

/*
 * \brief implements a best effort broadcast algorithm.
 */
public class BEBroadcastComponent extends ComponentDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(BEBroadcastComponent.class);
	private Positive<Network> net = requires(Network.class);
	private Negative<BEBroadcastPort> beb = provides(BEBroadcastPort.class);
	

	public BEBroadcastComponent() {
		subscribe(handleStart, control);
		subscribe(handleBEBroadcast, beb);
		subscribe(handleBEBMessage, net);
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("BestEffortBroadcast starting ...");
		}
	};
	
	private Handler<BEBroadcast> handleBEBroadcast = new Handler<BEBroadcast>() {
		@Override
		public void handle(BEBroadcast event) {
			for(TAddress dst : event.getDst()){
				trigger(new TMessage(event.getSrc(), dst, Transport.TCP, new BEDeliver(event.getPayload(),event.getSrc())), net);
				//using BEDeliver here to convey and encapsulate the message to be able to pattern-march it 
			}
			
		}
	};
	
	private ClassMatchedHandler<BEDeliver, TMessage> handleBEBMessage = new ClassMatchedHandler<BEDeliver, TMessage>() {
		public void handle(BEDeliver content, TMessage context) {
			trigger(content, beb);
		}
	};

}
