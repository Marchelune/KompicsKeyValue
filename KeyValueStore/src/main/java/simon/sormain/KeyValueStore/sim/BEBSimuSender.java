package simon.sormain.KeyValueStore.sim;

import java.util.HashSet;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.epfd.Epfd;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcast;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastComponent;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;

public class BEBSimuSender extends ComponentDefinition {

	
    private static final Logger LOG = LoggerFactory.getLogger(BEBSimuSender.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    
    public BEBSimuSender() {

    	//create and connect all components except timer and network
        Component epfd = create(Epfd.class, Init.NONE); 
        Component beb = create(BEBroadcastComponent.class, Init.NONE);
        Component sender = create(BebSender.class, Init.NONE);
    	
        //connect required internal components to network and timer
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(beb.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(sender.getNegative(BEBroadcastPort.class), beb.getPositive(BEBroadcastPort.class), Channel.TWO_WAY); // ????
        connect(sender.getNegative(Timer.class), timer, Channel.TWO_WAY);
    }
    
    
  
}
