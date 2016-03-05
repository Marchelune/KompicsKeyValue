package simon.sormain.KeyValueStore.sim.eld;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

public class SimulationObserverELD extends ComponentDefinition {

private static final Logger LOG = LoggerFactory.getLogger(SimulationObserverELD.class);
    
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
        	TAddress leader1 = gv.getValue("simulation.leader1", TAddress.class);
        	TAddress leader2 = gv.getValue("simulation.leader2", TAddress.class);
        	TAddress leader3 = gv.getValue("simulation.leader3", TAddress.class);
        	TAddress leader4 = gv.getValue("simulation.leader4", TAddress.class);
        	TAddress leader5 = gv.getValue("simulation.leader5", TAddress.class);

            if(gv.getDeadNodes().size() == 0){
            	if(leader1.equals(leader2) && leader2.equals(leader3) &&
            		leader3.equals(leader4) && leader4.equals(leader5)) {
            	
            		LOG.info("ELD : TRUE, all correct nodes trust the same correct leader, {} .\n", gv.getValue("simulation.leader1", TAddress.class));
            	} else {
            		LOG.info("ELD : FALSE, every correct node does not trust the same correct leader.\n");
            	}
            } else {
            	LOG.info("ELD : FIRST NODE DEAD");
            	if( leader2.equals(leader3) && leader3.equals(leader4) && leader4.equals(leader5) && trustCorrectLeader()) {
            		LOG.info("ELD : TRUE, all correct nodes trust the same correct leader, {} .\n", gv.getValue("simulation.leader2", TAddress.class));
            	} else {
            		LOG.info("ELD : FALSE \n");
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
    
    public boolean trustCorrectLeader(){
    	TAddress addr1 = null;
    	try {
			addr1 = new TAddress(InetAddress.getByName("192.168.0.1"), 10000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    	TAddress leader2 = gv.getValue("simulation.leader2", TAddress.class);
    	TAddress leader3 = gv.getValue("simulation.leader3", TAddress.class);
    	TAddress leader4 = gv.getValue("simulation.leader4", TAddress.class);
    	TAddress leader5 = gv.getValue("simulation.leader5", TAddress.class);
    	if(leader2.equals(addr1) || leader3.equals(addr1) || leader4.equals(addr1) || leader5.equals(addr1)){
    		return false;
    	} else {
    		return true;
    	}
    }
}
