package simon.sormain.KeyValueStore.sim.app;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class AppSimuLauncherCASNOK {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario AppScenario = simon.sormain.KeyValueStore.sim.app.ScenarioGenapp.appCASNOK();
        AppScenario.simulate(LauncherComp.class);
    }
}
