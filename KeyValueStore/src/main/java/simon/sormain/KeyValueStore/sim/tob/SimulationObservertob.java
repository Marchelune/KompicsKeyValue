package simon.sormain.KeyValueStore.sim.tob;

import java.util.Iterator;
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
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.sim.multipaxos.OpSequence;

public class SimulationObservertob extends ComponentDefinition{
	
	private static final Logger LOG = LoggerFactory.getLogger(SimulationObservertob.class);
	
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    
    private UUID timerId;

    public SimulationObservertob() {
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
            if(gv.getDeadNodes().size() > 0){
            	LOG.info("TOB : Nodes 1 and 4 are dead");
            }
            LOG.info("TOB : Node 2 : Broadcast so far {}", gv.getValue("simulation.seqbc2", OpSequence.class).getSequence());
            LOG.info("TOB : Node 3 : Broadcast so far {} \n", gv.getValue("simulation.seqbc3", OpSequence.class).getSequence());
            LOG.info("TOB : Node 1 : Delivered so far {}:", gv.getValue("simulation.seqdelivered1", OpSequence.class).getSequence());
            LOG.info("TOB : Node 2 : Delivered so far {}:", gv.getValue("simulation.seqdelivered2", OpSequence.class).getSequence());
            LOG.info("TOB : Node 3 : Delivered so far {}:", gv.getValue("simulation.seqdelivered3", OpSequence.class).getSequence());
            LOG.info("TOB : Node 4 : Delivered so far {}:", gv.getValue("simulation.seqdelivered4", OpSequence.class).getSequence());
            LOG.info("TOB : Node 5 : Delivered so far {}: \n", gv.getValue("simulation.seqdelivered5", OpSequence.class).getSequence());
            LOG.info("TOB : Globally delivered so far {}: \n", gv.getValue("simulation.globaldelivered", OpSet.class).getSet());

            LOG.info("TOB : CORRECTNESS (Validity) {} \n", checkValidity());
            LOG.info("TOB : CORRECTNESS (Uniform Agreement) {} \n", checkUnifAgreement());
            LOG.info("TOB : CORRECTNESS (Uniform Total Order) {} \n", checkTotalOrder());
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
    	// The correct senders are node 2 and 3
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    	OpSequence delivered2 = gv.getValue("simulation.seqdelivered2", OpSequence.class);
    	OpSequence delivered3 = gv.getValue("simulation.seqdelivered3", OpSequence.class);
    	OpSequence bc2 = gv.getValue("simulation.seqbc2", OpSequence.class);
    	OpSequence bc3 = gv.getValue("simulation.seqbc3", OpSequence.class);
    	
    	Iterator<Operation> it2 = bc2.iterator();
    	Iterator<Operation> it3 = bc3.iterator();
    	
    	while(it2.hasNext()){
    		if(!delivered2.contains(it2.next())){
    			return false;
    		}
    	}
    	
    	while(it3.hasNext()){
    		if(!delivered3.contains(it3.next())){
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public boolean checkUnifAgreement(){
    	// The correct nodes are nodes 2, 3 and 5
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    	OpSet GlobalDelivered = gv.getValue("simulation.globaldelivered", OpSet.class);
    	
    	OpSequence delivered2 = gv.getValue("simulation.seqdelivered2", OpSequence.class);
    	OpSequence delivered3 = gv.getValue("simulation.seqdelivered3", OpSequence.class);
    	OpSequence delivered5 = gv.getValue("simulation.seqdelivered5", OpSequence.class);
    	
    	Iterator<Operation> itgd = GlobalDelivered.iterator();
    	while(itgd.hasNext()){
    		Operation nextop = itgd.next();
    		if(!delivered2.contains(nextop) || !delivered3.contains(nextop) || !delivered5.contains(nextop)){
    			return false;
    		}
    	}
    	return true;
    }
    
    public boolean checkTotalOrder(){
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    	OpSequence delivered1 = gv.getValue("simulation.seqdelivered1", OpSequence.class);
    	OpSequence delivered2 = gv.getValue("simulation.seqdelivered2", OpSequence.class);
    	OpSequence delivered3 = gv.getValue("simulation.seqdelivered3", OpSequence.class);
    	OpSequence delivered4 = gv.getValue("simulation.seqdelivered4", OpSequence.class);
    	OpSequence delivered5 = gv.getValue("simulation.seqdelivered5", OpSequence.class);
    	
    	boolean so12 = sameOrder(delivered1, delivered2);
    	boolean so13 = sameOrder(delivered1, delivered3);
    	boolean so14 = sameOrder(delivered1, delivered4);
    	boolean so15 = sameOrder(delivered1, delivered5);
    	boolean so23 = sameOrder(delivered2, delivered3);
    	boolean so24 = sameOrder(delivered2, delivered4);
    	boolean so25 = sameOrder(delivered2, delivered5);
    	boolean so34 = sameOrder(delivered3, delivered4);
    	boolean so35 = sameOrder(delivered3, delivered5);
    	boolean so45 = sameOrder(delivered4, delivered5);
    	
    	if(so12 && so13 && so14 && so15 && so23 && so24 && so25 && so34 && so35 && so45){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean sameOrder(OpSequence seq1, OpSequence seq2){
    	if(seq1.size() <= seq2.size()){
    		for(int i = 0; i < seq1.size(); i++){
    			if(!seq1.get(i).equals(seq2.get(i))){
    				return false;
    			}
    		}
    	} else {
    		for(int i = 0; i < seq2.size(); i++){
    			if(!seq1.get(i).equals(seq2.get(i))){
    				return false;
    			}
    		}
    	}
    	return true;
    }
}
