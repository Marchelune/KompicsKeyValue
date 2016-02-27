package simon.sormain.KeyValueStore.sim;

import java.util.HashSet;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.network.SetTAddress;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcast;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;

public class BebSender extends ComponentDefinition {
	
    private static final Logger LOG = LoggerFactory.getLogger(BebSender.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<BEBroadcastPort> bebport = requires(BEBroadcastPort.class);
    private UUID timerId;
    
    public BebSender() {
    	subscribe(handleStart, control);
    	subscribe(handleSendTimeout, timer);
    }
    
	Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            schedulePeriodicCheck();
        }
    };
    
    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    Handler<SendTimeout> handleSendTimeout = new Handler<SendTimeout>() {
        @Override
        public void handle(SendTimeout event) {
        	TAddress selfaddr = config().getValue("keyvaluestore.self", TAddress.class);
        	HashSet<TAddress> alladdr = config().getValue("keyvaluestore.epfd.allAddr", SetTAddress.class).get();
        	trigger(new BEBroadcast(selfaddr,alladdr, null), bebport);
        }
    };
    
    
    private void schedulePeriodicCheck() {
        long period = config().getValue("simulation.sendertimeout", Long.class);
        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(period, period);
        SendTimeout timeout = new SendTimeout(spt);
        spt.setTimeoutEvent(timeout);
        trigger(spt, timer);
        timerId = timeout.getTimeoutId();
    }

    public static class SendTimeout extends Timeout {

        public SendTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }
}
