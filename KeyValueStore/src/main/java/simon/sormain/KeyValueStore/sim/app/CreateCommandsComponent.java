package simon.sormain.KeyValueStore.sim.app;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.client.ConsoleLine;
import simon.sormain.KeyValueStore.client.ConsolePort;


public class CreateCommandsComponent extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(CreateCommandsComponent.class);
	Positive<Timer> timer = requires(Timer.class);
	private Negative<ConsolePort> console = provides(ConsolePort.class);
	private UUID timerId;
	private int seqnum;
	long period;
	String typeOp;
	
	
	public CreateCommandsComponent(CreateCommandsInit init) {
    	subscribe(handleStart, control);
    	subscribe(handleSendTimeout, timer);
    	period = init.getPeriod();
    	typeOp = init.getTypeOp();
	}
	
	Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            schedulePeriodicCheck();
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
        	int res;
        	if (seqnum % 2 == 0) {
        		  // even
        		res = seqnum;
        		} else {
        		  // odd
        			res = seqnum + 1000;
        		}
        	String op = null;
        	String op2 = null;
        	if(typeOp.equals("PUT")){
        		op = typeOp + "(" + Integer.toString(res) +",kvstore"+Integer.toString(res)+")";
        	} else if(typeOp.equals("GET")) {
        		op = typeOp + "(" + Integer.toString(res) + ")";
        	} else if(typeOp.equals("CAS")) {
        		op =typeOp + "(" + Integer.toString(res) +",kvstore"+Integer.toString(res)+",CAS"+Integer.toString(res)+")";
        		op2 = typeOp + "(" + Integer.toString(res) + ")";
        	} 
        	trigger(new ConsoleLine(op) , console);
        	if(typeOp.equals("CAS")){
        		//trigger(new ConsoleLine(op2) , console);
        	}
        }
    };
    
    private void schedulePeriodicCheck() {
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
