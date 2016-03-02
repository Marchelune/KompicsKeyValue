package simon.sormain.KeyValueStore.sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ELDSimuLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario eldScenario = simon.sormain.KeyValueStore.sim.ScenarioGeneld.eld();
        eldScenario.simulate(LauncherComp.class);
    }
}
