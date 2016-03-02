package simon.sormain.KeyValueStore.sim.multipaxos;

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
            LOG.info("MultiPaxos : Node 1 : Decided so far :", gv.getValue("simulation.seqdecided1", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 2 : Decided so far :", gv.getValue("simulation.seqdecided2", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 3 : Decided so far :", gv.getValue("simulation.seqdecided3", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 4 : Decided so far :", gv.getValue("simulation.seqdecided4", OpSequence.class).getSequence());
            LOG.info("MultiPaxos : Node 5 : Decided so far :", gv.getValue("simulation.seqdecided1", OpSequence.class).getSequence());
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
