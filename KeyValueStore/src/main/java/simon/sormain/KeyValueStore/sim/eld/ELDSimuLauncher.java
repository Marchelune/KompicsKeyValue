package simon.sormain.KeyValueStore.sim.eld;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ELDSimuLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario eldScenario = simon.sormain.KeyValueStore.sim.eld.ScenarioGeneld.eld();
        eldScenario.simulate(LauncherComp.class);
    }
}
