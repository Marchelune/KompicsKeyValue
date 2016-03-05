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
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.sim.tob.OpSet;

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
				msgSent((Operation) event.getPayload()); //Simu
			}
			
		}
	};
	
	
	
	private ClassMatchedHandler<BEDeliver, TMessage> handleBEBMessage = new ClassMatchedHandler<BEDeliver, TMessage>() {
		public void handle(BEDeliver content, TMessage context) {
			msgRcv((Operation) content.getPayload()); //Simu
			trigger(content, beb);
		}
	};
	
	private void msgSent(Operation op) {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        OpSet BebOps = gv.getValue("simulation.BEBmsgs", OpSet.class);
        BebOps.add(op);
        gv.setValue("simulation.BEBmsgs", BebOps);
	}
	
	private void msgRcv(Operation op) {
        GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
        
        TAddress selfaddr= config().getValue("keyvaluestore.self.addr", TAddress.class);
        OpSet BebOps;
        switch (selfaddr.getPort()) {
        case 10000 :
            BebOps = gv.getValue("simulation.BEBdelmsgs1", OpSet.class);
            BebOps.add(op);
            gv.setValue("simulation.BEBdelmsgs1", BebOps);
        	break;
        case 20000 :
            BebOps = gv.getValue("simulation.BEBdelmsgs2", OpSet.class);
            BebOps.add(op);
            gv.setValue("simulation.BEBdelmsgs2", BebOps);
        	break;
        case 30000 :
            BebOps = gv.getValue("simulation.BEBdelmsgs3", OpSet.class);
            BebOps.add(op);
            gv.setValue("simulation.BEBdelmsgs3", BebOps);
        	break;
        case 40000 :
            BebOps = gv.getValue("simulation.BEBdelmsgs4", OpSet.class);
            BebOps.add(op);
            gv.setValue("simulation.BEBdelmsgs4", BebOps);
        	break;
        case 50000 :
            BebOps = gv.getValue("simulation.BEBdelmsgs5", OpSet.class);
            BebOps.add(op);
            gv.setValue("simulation.BEBdelmsgs5", BebOps);
        	break;
    		
        }
	}

}
