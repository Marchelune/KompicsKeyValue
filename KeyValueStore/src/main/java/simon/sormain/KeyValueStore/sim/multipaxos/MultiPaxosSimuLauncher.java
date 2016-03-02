package simon.sormain.KeyValueStore.sim.multipaxos;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class MultiPaxosSimuLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario MultiPaxosScenario = simon.sormain.KeyValueStore.sim.multipaxos.ScenarioGenMultiPaxos.multipaxos();
        MultiPaxosScenario.simulate(LauncherComp.class);
    }
}
