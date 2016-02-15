package simon.sormain.KeyValueStore.sim;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.Distribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.util.GlobalView;
import simon.sormain.KeyValueStore.network.TAddress;
//import se.sics.test.sim.SimulationObserver;
import simon.sormain.KeyValueStore.system.NodeParent;


public class ScenarioGen {
	/*
    static Operation setupOp = new Operation<SetupEvent>() {
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public void setupGlobalView(GlobalView gv) {
                    gv.setValue("simulation.pongs", 0);
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
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("pingpong.simulation.checktimeout", 2000);
                    return config;
                }
                
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return SimulationObserver.class;
                }

                @Override
                public Init getComponentInit() {
                    return new SimulationObserver.Init(100, 2);
                }
            };
        }
    };
*/
    static Operation1 startNodeOp = new Operation1<StartNodeEvent, Long>() {

        public StartNodeEvent generate(final Long self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                TAddress addr1;
                TAddress addr2;
                TAddress addr3;
                TAddress addr4;
                TAddress addr5;
                long initialDelay = 1000;
                long deltaDelay = 500;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0." + self), 10000);
                        addr1 = new TAddress(InetAddress.getByName("192.168.0.1"), 10000);
                        addr2 = new TAddress(InetAddress.getByName("192.168.0.2"), 10000);
                        addr3 = new TAddress(InetAddress.getByName("192.168.0.3"), 10000);
                        addr4 = new TAddress(InetAddress.getByName("192.168.0.4"), 10000);
                        addr5 = new TAddress(InetAddress.getByName("192.168.0.5"), 10000);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("keyvaluestore.self", selfAdr);
                    config.put("keyvaluestore.epfd.allAddr.addr1", addr1);
                    config.put("keyvaluestore.epfd.allAddr.addr2", addr2);
                    config.put("keyvaluestore.epfd.allAddr.addr3", addr3);
                    config.put("keyvaluestore.epfd.allAddr.addr4", addr4);
                    config.put("keyvaluestore.epfd.allAddr.addr5", addr5);
                    config.put("keyvaluestore.epfd.initDelay", initialDelay);
                    config.put("keyvaluestore.epfd.deltaDelay", deltaDelay);
                    return config;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return NodeParent.class;
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
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0." + self), 10000);
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

    

    public static SimulationScenario fiveNodes() {
        SimulationScenario scen = new SimulationScenario() {
            {
            	/*
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
*/
                SimulationScenario.StochasticProcess launchNodes = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startNodeOp, constant(1));
                        raise(1, startNodeOp, constant(2));
                        raise(1, startNodeOp, constant(3));
                        raise(1, startNodeOp, constant(4));
                        raise(1, startNodeOp, constant(5));
                    }
                };
                
                SimulationScenario.StochasticProcess killNode = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, killNodeOp, constant(1));
                    }  
                };


                //setup.start();
                //observer.startAfterTerminationOf(0, setup);
                launchNodes.start();
                killNode.startAfterTerminationOf(10000, launchNodes);
                terminateAfterTerminationOf(10000, killNode);
            }
        };

        return scen;
    }
}
