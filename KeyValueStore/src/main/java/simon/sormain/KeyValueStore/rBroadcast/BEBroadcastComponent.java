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
import se.sics.kompics.simulator.util.GlobalView;
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
			logger.info("{} BestEffortBroadcast starting ...", config().getValue("keyvaluestore.self.addr", TAddress.class));
		}
	};
	
	private Handler<BEBroadcast> handleBEBroadcast = new Handler<BEBroadcast>() {
		@Override
		public void handle(BEBroadcast event) {
			for(TAddress dst : event.getDst()){
				trigger(new TMessage(event.getSrc(), dst, Transport.TCP, new BEDeliver(event.getPayload(),event.getSrc())), net);
				//using BEDeliver here to convey and encapsulate the message to be able to pattern-march it 
				msgSent(); //Simu
			}
			
		}
	};
	
	
	
	private ClassMatchedHandler<BEDeliver, TMessage> handleBEBMessage = new ClassMatchedHandler<BEDeliver, TMessage>() {
		public void handle(BEDeliver content, TMessage context) {
			msgRcv(); //Simu
			trigger(content, beb);
		}
	};
	
	private void msgSent() {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        gv.setValue("simulation.sentmsgs", gv.getValue("simulation.sentmsgs", Integer.class) + 1);
	}
	
	private void msgRcv() {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        gv.setValue("simulation.rcvmsgs", gv.getValue("simulation.rcvmsgs", Integer.class) + 1);
        TAddress selfaddr= config().getValue("keyvaluestore.self", TAddress.class);
        switch (selfaddr.getPort()) {
        case 10000 :
        	gv.setValue("simulation.rcvmsgsone", gv.getValue("simulation.rcvmsgsone", Integer.class) + 1);
        	break;
        case 20000 :
        	gv.setValue("simulation.rcvmsgstwo", gv.getValue("simulation.rcvmsgstwo", Integer.class) + 1);
        	break;
        case 30000 :
        	gv.setValue("simulation.rcvmsgsthree", gv.getValue("simulation.rcvmsgsthree", Integer.class) + 1);
        	break;
        case 40000 :
        	gv.setValue("simulation.rcvmsgsfour", gv.getValue("simulation.rcvmsgsfour", Integer.class) + 1);
        	break;
        case 50000 :
        	gv.setValue("simulation.rcvmsgsfive", gv.getValue("simulation.rcvmsgsfive", Integer.class) + 1);
        	break;
    		
        }
	}

}
