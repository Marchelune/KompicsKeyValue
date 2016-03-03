package simon.sormain.KeyValueStore.sim.tob;

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
            LOG.info("TOB : Node 1 : Delivered so far {}:", gv.getValue("simulation.seqdelivered1", OpSequence.class).getSequence());
            LOG.info("TOB : Node 2 : Delivered so far {}:", gv.getValue("simulation.seqdelivered2", OpSequence.class).getSequence());
            LOG.info("TOB : Node 3 : Delivered so far {}:", gv.getValue("simulation.seqdelivered3", OpSequence.class).getSequence());
            LOG.info("TOB : Node 4 : Delivered so far {}:", gv.getValue("simulation.seqdelivered4", OpSequence.class).getSequence());
            LOG.info("TOB : Node 5 : Delivered so far {}: \n", gv.getValue("simulation.seqdelivered5", OpSequence.class).getSequence());
            /*
            LOG.info("MultiPaxos : Node 1 : Decided so far {}:", gv.getValue("simulation.seqdecided1", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 2 : Decided so far {}:", gv.getValue("simulation.seqdecided2", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 3 : Decided so far {}:", gv.getValue("simulation.seqdecided3", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 4 : Decided so far {}:", gv.getValue("simulation.seqdecided4", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 5 : Decided so far {}: \n", gv.getValue("simulation.seqdecided5", OpSequence.class).getSequence());
            */
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
