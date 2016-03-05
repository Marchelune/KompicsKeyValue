package simon.sormain.KeyValueStore.sim.beb;

import java.util.Iterator;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.sim.multipaxos.OpSequence;
import simon.sormain.KeyValueStore.sim.tob.OpSet;


public class SimulationObserverBEB extends ComponentDefinition {

	
    private static final Logger LOG = LoggerFactory.getLogger(SimulationObserverBEB.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    
    private UUID timerId;

    public SimulationObserverBEB() {

        subscribe(handleStart, control);
        subscribe(handleCheck, timer);
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

    Handler<CheckTimeout> handleCheck = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {
            
            LOG.info("BEB : CORRECTNESS (Best-Effort Validity) {} \n", checkValidity());

  
            
        }
    };

    
    private void schedulePeriodicCheck() {
        long period = config().getValue("simulation.checktimeout", Long.class);
        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(period, period);
        CheckTimeout timeout = new CheckTimeout(spt);
        spt.setTimeoutEvent(timeout);
        trigger(spt, timer);
        timerId = timeout.getTimeoutId();
    }

    public static class CheckTimeout extends Timeout {

        public CheckTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }
    
    public boolean checkValidity(){
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);

    	OpSet bebmsgs = gv.getValue("simulation.BEBmsgs", OpSet.class);
    	OpSet delivered1 = gv.getValue("simulation.BEBdelmsgs1", OpSet.class);
    	OpSet delivered2 = gv.getValue("simulation.BEBdelmsgs2", OpSet.class);
    	OpSet delivered3 = gv.getValue("simulation.BEBdelmsgs3", OpSet.class);
    	OpSet delivered4 = gv.getValue("simulation.BEBdelmsgs4", OpSet.class);
    	OpSet delivered5 = gv.getValue("simulation.BEBdelmsgs5", OpSet.class);
    	
    	Iterator<Operation> itbeb = bebmsgs.iterator();
    	
    	while(itbeb.hasNext()){
    		Operation nextop = itbeb.next();
    		if(!delivered1.contains(nextop) || !delivered2.contains(nextop) || 
    				!delivered3.contains(nextop) ||!delivered4.contains(nextop) ||!delivered5.contains(nextop)){
    			return false;
    		}
    	}
    	return true;
    }
}
