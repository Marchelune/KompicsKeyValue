package simon.sormain.KeyValueStore.sim;


import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;


public class fiveNodesLauncher {
    public static void main(String[] args) {
        long seed = 12345;
        SimulationScenario.setSeed(seed);
        SimulationScenario fiveNodesScenario = simon.sormain.KeyValueStore.sim.ScenarioGen.fiveNodes();
        fiveNodesScenario.simulate(LauncherComp.class);
    }
}
