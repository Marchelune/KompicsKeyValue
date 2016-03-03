package simon.sormain.KeyValueStore.sim.tob;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class TOBSimuLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario TOBScenario = simon.sormain.KeyValueStore.sim.tob.ScenarioGentob.tob();
        TOBScenario.simulate(LauncherComp.class);
    }
}
