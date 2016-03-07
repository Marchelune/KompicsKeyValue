package simon.sormain.KeyValueStore.sim.epfd;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class EpfdSimuLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario epfdScenario = simon.sormain.KeyValueStore.sim.epfd.ScenarioGenepfd.epfd();
        epfdScenario.simulate(LauncherComp.class);
    }
}
