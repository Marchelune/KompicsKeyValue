package simon.sormain.KeyValueStore.sim.app;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class AppSimuLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario AppScenario = simon.sormain.KeyValueStore.sim.app.ScenarioGenapp.app();
        AppScenario.simulate(LauncherComp.class);
    }	
}
