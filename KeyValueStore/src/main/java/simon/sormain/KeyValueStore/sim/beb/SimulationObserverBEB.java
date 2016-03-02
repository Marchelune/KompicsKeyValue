package simon.sormain.KeyValueStore.sim.beb;

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
            GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
            if(gv.getDeadNodes().size() > 0) {
                LOG.info("The sender is dead");
            }
            LOG.info("{} messages have been sent by the sender", gv.getValue("simulation.sentmsgs", Integer.class));
            LOG.info(" TOTAL : {} messages have been received", gv.getValue("simulation.rcvmsgs", Integer.class));
            LOG.info(" NODE ONE : {} messages have been received", gv.getValue("simulation.rcvmsgsone", Integer.class));
            LOG.info(" NODE TWO : {} messages have been received", gv.getValue("simulation.rcvmsgstwo", Integer.class));
            LOG.info(" NODE THREE : {} messages have been received", gv.getValue("simulation.rcvmsgsthree", Integer.class));
            LOG.info(" NODE FOUR : {} messages have been received", gv.getValue("simulation.rcvmsgsfour", Integer.class));
            LOG.info(" NODE FIVE : {} messages have been received", gv.getValue("simulation.rcvmsgsfive", Integer.class));
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
