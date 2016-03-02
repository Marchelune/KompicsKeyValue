package simon.sormain.KeyValueStore.sim.multipaxos;

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
import simon.sormain.KeyValueStore.app.GetOperation;
import simon.sormain.KeyValueStore.asc.AbortableSequenceConsensusPort;
import simon.sormain.KeyValueStore.asc.AscPropose;
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcast;
import simon.sormain.KeyValueStore.sim.beb.BebSender.SendTimeout;
import simon.sormain.KeyValueStore.tob.Tob;

//Simu component
public class SendProposeMPaxosComponent extends ComponentDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(Tob.class);
	
	Positive<Timer> timer = requires(Timer.class);
	private Positive<AbortableSequenceConsensusPort> asc = requires(AbortableSequenceConsensusPort.class);
	private UUID timerId;
	private int seqnum;
	TAddress selfaddr;
	
	public SendProposeMPaxosComponent() {
    	subscribe(handleStart, control);
    	subscribe(handleSendTimeout, timer);

    }
	
	Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            schedulePeriodicCheck();
        	seqnum = 0;
        	selfaddr = config().getValue("keyvaluestore.self.addr", TAddress.class);
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
        	// The value of the key nor the type of the operation matter here
        	GetOperation op = new GetOperation(selfaddr, seqnum, 0);
        	trigger(new AscPropose(op), asc);
        }
    };
    
    private void schedulePeriodicCheck() {
        long period = config().getValue("simulation.ratePropose", Long.class);
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
