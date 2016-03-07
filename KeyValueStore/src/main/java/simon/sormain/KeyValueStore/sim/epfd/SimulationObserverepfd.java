package simon.sormain.KeyValueStore.sim.epfd;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
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
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.sim.eld.SimulationObserverELD.CheckTimeout;


public class SimulationObserverepfd extends ComponentDefinition {
private static final Logger LOG = LoggerFactory.getLogger(SimulationObserverepfd.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    private UUID timerId;

    public SimulationObserverepfd() {

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
        	
            if(gv.getDeadNodes().size()> 0){
            	LOG.info("EPFD : FIRST NODE DEAD");
            	LOG.info("EPFD : CORRECTNESS (Strong Completeness) {}", Completeness());
            }
            
            LOG.info("EPFD : CORRECTNESS (Eventual Strong Accuracy) {} \n", Accuracy());
            
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
    
    // Checks eventual strong accuracy
    public boolean Accuracy(){
    	TAddress addr2 = null;
    	TAddress addr3 = null;
    	TAddress addr4 = null;
    	TAddress addr5 = null;
    	try {
			addr2 = new TAddress(InetAddress.getByName("192.168.0.1"), 20000);
			addr3 = new TAddress(InetAddress.getByName("192.168.0.1"), 30000);
			addr4 = new TAddress(InetAddress.getByName("192.168.0.1"), 40000);
			addr5 = new TAddress(InetAddress.getByName("192.168.0.1"), 50000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	// The correct nodes are nodes 2,3,4,5
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    	HashSet<TAddress> suspected2 = gv.getValue("simulation.suspected2", SetTAddress.class).get();
    	HashSet<TAddress> suspected3 = gv.getValue("simulation.suspected3", SetTAddress.class).get();
    	HashSet<TAddress> suspected4 = gv.getValue("simulation.suspected4", SetTAddress.class).get();
    	HashSet<TAddress> suspected5 = gv.getValue("simulation.suspected5", SetTAddress.class).get();
    	
    	boolean b2 = !suspected2.contains(addr2) && !suspected2.contains(addr3) && !suspected2.contains(addr4) && !suspected2.contains(addr5);
    	boolean b3 = !suspected3.contains(addr2) && !suspected3.contains(addr3) && !suspected3.contains(addr4) && !suspected3.contains(addr5);
    	boolean b4 = !suspected4.contains(addr2) && !suspected4.contains(addr3) && !suspected4.contains(addr4) && !suspected4.contains(addr5);
    	boolean b5 = !suspected5.contains(addr2) && !suspected5.contains(addr3) && !suspected5.contains(addr4) && !suspected5.contains(addr5);
    	if(b2 && b3 && b4 && b5){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    // Checks strong completeness
    public boolean Completeness(){
    	TAddress addr1 = null;
    	try {
    		addr1 = new TAddress(InetAddress.getByName("192.168.0.1"), 10000);
    	} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    	HashSet<TAddress> suspected2 = gv.getValue("simulation.suspected2", SetTAddress.class).get();
    	HashSet<TAddress> suspected3 = gv.getValue("simulation.suspected3", SetTAddress.class).get();
    	HashSet<TAddress> suspected4 = gv.getValue("simulation.suspected4", SetTAddress.class).get();
    	HashSet<TAddress> suspected5 = gv.getValue("simulation.suspected5", SetTAddress.class).get();
    	// Only node 1 fails. It should be eventually detected by every correct node
    	if(suspected2.contains(addr1) && suspected3.contains(addr1) && suspected4.contains(addr1) && suspected5.contains(addr1)){
    		return true;
    	} else {
    		return false;
    	}
    }
}
