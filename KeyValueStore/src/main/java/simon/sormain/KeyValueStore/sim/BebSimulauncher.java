package simon.sormain.KeyValueStore.sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class BebSimulauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario BebScenario = simon.sormain.KeyValueStore.sim.ScenarioGenBEBBc.BebBc();
        BebScenario.simulate(LauncherComp.class);
    }
}
