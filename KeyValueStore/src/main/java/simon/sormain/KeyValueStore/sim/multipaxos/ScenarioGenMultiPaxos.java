package simon.sormain.KeyValueStore.sim.multipaxos;

import static java.lang.Math.toIntExact;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.Operation3;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.util.GlobalView;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.sim.eld.NodeParenteld;
import simon.sormain.KeyValueStore.sim.eld.SimulationObserverELD;
import simon.sormain.KeyValueStore.sim.multipaxos.OpSequence;

public class ScenarioGenMultiPaxos {
	static Operation setupOp = new Operation<SetupEvent>() {
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public void setupGlobalView(GlobalView gv) {
                		gv.setValue("simulation.seqdecided1", new OpSequence());
                		gv.setValue("simulation.seqdecided2", new OpSequence());
                		gv.setValue("simulation.seqdecided3", new OpSequence());
                		gv.setValue("simulation.seqdecided4", new OpSequence());
                		gv.setValue("simulation.seqdecided5", new OpSequence());
                		gv.setValue("simulation.proposedcommands", new OpSequence());
                }
            };
        }
    };
    
    static Operation startObserverOp = new Operation<StartNodeEvent>() {
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                TAddress selfAdr;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("0.0.0.0"), 0);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("simulation.checktimeout", 400);
                    return config;
                }
                
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return SimulationObserverMultiPaxos.class;
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }
            };
        }
    };
    
    
    static Operation2 startNodeOp = new Operation2<StartNodeEvent, Long, Long>() {

        public StartNodeEvent generate(final Long self, final Long rank) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                int selfrank;
                MapRanks ranks = new MapRanks();

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                        selfrank = toIntExact(rank);
                        ranks.put(1, new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                        ranks.put(2,new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                        ranks.put(3,new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
                        ranks.put(4,new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                        ranks.put(5,new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("keyvaluestore.self.addr", selfAdr);
                    config.put("keyvaluestore.self.rank", selfrank);
                    config.put("keyvaluestore.self.ranks", ranks);
                    return config;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return NodeParentMultiPaxos.class;
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
                }
            };
        }
    };
    
    
    static Operation3 startNodeProposerOp = new Operation3<StartNodeEvent, Long, Long, Long>() {

        public StartNodeEvent generate(final Long self, final Long rank, final Long ratePropose) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                int selfrank;
                MapRanks ranks = new MapRanks();
                

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                        selfrank = toIntExact(rank);
                        ranks.put(1, new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                        ranks.put(2,new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                        ranks.put(3,new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
                        ranks.put(4,new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                        ranks.put(5,new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("keyvaluestore.self.addr", selfAdr);
                    config.put("keyvaluestore.self.rank", selfrank);
                    config.put("keyvaluestore.self.ranks", ranks);
                    // rate at which proposer sends proposals
                    config.put("simulation.ratePropose", ratePropose);
                    return config;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return NodeParentMultiPaxosProposer.class;
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
                }
            };
        }
    };
    
    static Operation1 killNodeOp = new Operation1<KillNodeEvent, Long>() {
        public KillNodeEvent generate(final Long self) {
            return new KillNodeEvent() {
                TAddress selfAdr;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }
                
                @Override
                public String toString() {
                    return "KillNode<" + selfAdr.toString() + ">";
                }
            };
        }
    };
    
    public static SimulationScenario multipaxos() {
        SimulationScenario scen = new SimulationScenario() {
            {

                SimulationScenario.StochasticProcess setup = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, setupOp);
                    }
                };

                SimulationScenario.StochasticProcess observer = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, startObserverOp);
                    }
                };

                SimulationScenario.StochasticProcess launchNodes = new SimulationScenario.StochasticProcess() {
                    {
                    	eventInterArrivalTime(constant(1));
                        raise(1, startNodeProposerOp, constant(10000), constant(1), constant(500));
                        raise(1, startNodeProposerOp, constant(20000), constant(2), constant(2500));
                        raise(1, startNodeOp, constant(30000), constant(3));
                        raise(1, startNodeOp, constant(40000), constant(4));
                        raise(1, startNodeOp, constant(50000), constant(5));
                    }
                };
                
                SimulationScenario.StochasticProcess killNode = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, killNodeOp, constant(50000));
                    }  
                };


                setup.start();
                observer.startAfterTerminationOf(0, setup);
                launchNodes.start();
                killNode.startAfterTerminationOf(2500,launchNodes);
                terminateAfterTerminationOf(5000, launchNodes);
            }
        };

        return scen;
    }
}
