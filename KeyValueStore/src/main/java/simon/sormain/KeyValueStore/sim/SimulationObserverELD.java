package simon.sormain.KeyValueStore.sim;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.sim.SimulationObserverBEB.CheckTimeout;

public class SimulationObserverELD extends ComponentDefinition {

private static final Logger LOG = LoggerFactory.getLogger(SimulationObserverBEB.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    
    private UUID timerId;

    public SimulationObserverELD() {

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
            GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
            if(gv.getDeadNodes().size() == 0){
            	if(gv.getValue("simulation.leader1", TAddress.class).equals(gv.getValue("simulation.leader2", TAddress.class)) &&
            		gv.getValue("simulation.leader2", TAddress.class).equals(gv.getValue("simulation.leader3", TAddress.class)) &&
            		gv.getValue("simulation.leader3", TAddress.class).equals(gv.getValue("simulation.leader4", TAddress.class)) &&
            		gv.getValue("simulation.leader4", TAddress.class).equals(gv.getValue("simulation.leader5", TAddress.class))) {
            	
            		LOG.info("ELD : TRUE, all nodes trust the same leader, {} .", gv.getValue("simulation.leader1", TAddress.class));
            	} else {
            		LOG.info("ELD: FALSE, every node does not trust the same leader.");
            	}
            } else {
            
            	if(gv.getDeadNodes().size() > 0 && gv.getValue("simulation.leader2", TAddress.class).equals(gv.getValue("simulation.leader3", TAddress.class)) &&
            			gv.getValue("simulation.leader3", TAddress.class).equals(gv.getValue("simulation.leader4", TAddress.class)) &&
            			gv.getValue("simulation.leader4", TAddress.class).equals(gv.getValue("simulation.leader5", TAddress.class))) {
            		LOG.info("ELD : FIRST NODE DEAD, TRUE, all nodes trust the same leader, {} .", gv.getValue("simulation.leader2", TAddress.class));
            	} else {
            		LOG.info("ELD : FIRST NODE DEAD, FALSE, every node does not trust the same leader.");
            	}
            }
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
}
