package simon.sormain.KeyValueStore.sim.beb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastComponent;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;

public class BEBSimuSender extends ComponentDefinition {

	
    private static final Logger LOG = LoggerFactory.getLogger(BEBSimuSender.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    
    public BEBSimuSender() {

    	//create and connect all components except timer and network
        Component beb = create(BEBroadcastComponent.class, Init.NONE);
        Component sender = create(BebSender.class, Init.NONE);
    	
        //connect required internal components to network and timer
        connect(beb.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(sender.getNegative(BEBroadcastPort.class), beb.getPositive(BEBroadcastPort.class), Channel.TWO_WAY); 
        connect(sender.getNegative(Timer.class), timer, Channel.TWO_WAY);
    }
    
    
  
}