package simon.sormain.KeyValueStore.sim.beb;

import java.util.HashSet;
import java.util.TreeMap;
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
import simon.sormain.KeyValueStore.app.GetOperation;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcast;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;

public class BebSender extends ComponentDefinition {
	
    private static final Logger LOG = LoggerFactory.getLogger(BebSender.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<BEBroadcastPort> bebport = requires(BEBroadcastPort.class);
    private UUID timerId;
	TAddress selfAddress;
	MapRanks mRanks;
	TreeMap<Integer, TAddress> Ranks;
	HashSet<TAddress> alladdr;
	private int seqnum;
    
    public BebSender() {
    	subscribe(handleStart, control);
    	subscribe(handleSendTimeout, timer);
    }
    
	Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            schedulePeriodicCheck();
            selfAddress = config().getValue("keyvaluestore.self.addr", TAddress.class);
            mRanks = config().getValue("keyvaluestore.self.ranks", MapRanks.class);
            Ranks = mRanks.getMap();
         // Get all addrs using Ranks
            alladdr = new HashSet<TAddress>(Ranks.values());
            seqnum = 0;
        }
    };
    
    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    Handler<SendTimeout> handleSendTimeout = new Handler<SendTimeout>() {
        @Override
        public void handle(SendTimeout event) {
        	seqnum++;
        	GetOperation op = new GetOperation(selfAddress, seqnum, 0);
        	trigger(new BEBroadcast(selfAddress, alladdr, op), bebport);
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
