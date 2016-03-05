package simon.sormain.KeyValueStore.sim.multipaxos;

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
import simon.sormain.KeyValueStore.network.TAddress;


public class SimulationObserverMultiPaxos extends ComponentDefinition {

private static final Logger LOG = LoggerFactory.getLogger(SimulationObserverMultiPaxos.class);
    
    Positive<Timer> timer = requires(Timer.class);
    Positive<Network> network = requires(Network.class);
    
    private UUID timerId;

    public SimulationObserverMultiPaxos() {
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
            
            OpSequence proposed = gv.getValue("simulation.proposedcommands", OpSequence.class);
            
            OpSequence decided1 = gv.getValue("simulation.seqdecided1", OpSequence.class);
            OpSequence decided2 = gv.getValue("simulation.seqdecided2", OpSequence.class);
            OpSequence decided3 = gv.getValue("simulation.seqdecided3", OpSequence.class);
            OpSequence decided4 = gv.getValue("simulation.seqdecided4", OpSequence.class);
            OpSequence decided5 = gv.getValue("simulation.seqdecided5", OpSequence.class);
            
            if(gv.getDeadNodes().size() > 0){
            	LOG.info("MultiPaxos : Node 5 is dead");
            }
            
            LOG.info("MultiPaxos : Proposed so far {}", proposed.getSequence());
            LOG.info("MultiPaxos : Node 1 : Decided so far {}:", decided1.getSequence());
            LOG.info("MultiPaxos : Node 2 : Decided so far {}:", decided2.getSequence());
            LOG.info("MultiPaxos : Node 3 : Decided so far {}:", decided3.getSequence());
            LOG.info("MultiPaxos : Node 4 : Decided so far {}:", decided4.getSequence());
            LOG.info("MultiPaxos : Node 5 : Decided so far {}: \n", decided5.getSequence());
            
            // Check that decided sequences only contain proposed commands
            boolean checkproposed1 = checkProposed(decided1);
            boolean checkproposed2 = checkProposed(decided2);
            boolean checkproposed3 = checkProposed(decided3);
            boolean checkproposed4 = checkProposed(decided4);
            boolean checkproposed5 = checkProposed(decided5);
            if(checkproposed1 && checkproposed2 && checkproposed3 && checkproposed4 && checkproposed5){
            	LOG.info("MultiPaxos : CORRECTNESS (Validity) TRUE:  All decided sequences only contain proposed commands \n");
            } else {
            	LOG.info("MultiPaxos : CORRECTNESS (Validity) FALSE:  A decided sequence contains a command that wasn't proposed \n");
            }
            
            // Check that decided sequences do not contain duplicates
            boolean checkduplicates1 = checkDuplicates(decided1);
            boolean checkduplicates2 = checkDuplicates(decided2);
            boolean checkduplicates3 = checkDuplicates(decided3);
            boolean checkduplicates4 = checkDuplicates(decided4);
            boolean checkduplicates5 = checkDuplicates(decided5);
            if(checkduplicates1 && checkduplicates2 && checkduplicates3 && checkduplicates4 && checkduplicates5){
            	LOG.info("MultiPaxos : CORRECTNESS (Validity) TRUE:  All decided sequences do not contain duplicates \n");
            } else {
            	LOG.info("MultiPaxos : CORRECTNESS (Validity) FALSE:  A decided sequence contains duplicates \n");
            }
            
            // Check that if process p decides u and process q decides v then one is a prefix of the other
            boolean p12 = prefix(decided1, decided2);
            boolean p13 = prefix(decided1, decided3);
            boolean p14 = prefix(decided1, decided4);
            boolean p15 = prefix(decided1, decided5);
            boolean p23 = prefix(decided2, decided3);
            boolean p24 = prefix(decided2, decided4);
            boolean p25 = prefix(decided2, decided5);
            boolean p34 = prefix(decided3, decided4);
            boolean p35 = prefix(decided3, decided5);
            boolean p45 = prefix(decided4, decided5);
            if(p12 && p13 && p14 && p15 && p23 && p24 && p25 && p34 && p35 && p45){
            	LOG.info("MultiPaxos : CORRECTNESS (Uniform Agreement) TRUE \n");
            } else {
            	LOG.info("MultiPaxos : CORRECTNESS (Uniform Agreement) FALSE \n");
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
    
    // Checks if a decided sequence contains only proposed commands
    public boolean checkProposed(OpSequence opSeq){
    	GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		OpSequence ProposedSeq = gv.getValue("simulation.proposedcommands", OpSequence.class);
		Iterator<Operation> itOp = opSeq.iterator();
		while(itOp.hasNext()){
			if(!ProposedSeq.contains(itOp.next())){
				return false;
			}
		}
    	return true;
    }
    
    public boolean checkDuplicates(OpSequence opSeq){
    	int size = opSeq.size();
    	for(int i = 0; i<size-1; i++){
    		for(int j = i+1; j<size; j++){
    			if(opSeq.get(i).equals(opSeq.get(j))){
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    
    public boolean prefix(OpSequence seq1, OpSequence seq2){
    	if(seq1.prefixof(seq2) || seq2.prefixof(seq1)){
    		return true;
    	} else{
    		return false;
    	}
    	
    }
}
