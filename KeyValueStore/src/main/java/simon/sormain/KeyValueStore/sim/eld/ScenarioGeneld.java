package simon.sormain.KeyValueStore.sim.eld;

import static java.lang.Math.toIntExact;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.util.GlobalView;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.network.TAddress;

public class ScenarioGeneld {

	 static Operation setupOp = new Operation<SetupEvent>() {
	        public SetupEvent generate() {
	            return new SetupEvent() {
	                @Override
	                public void setupGlobalView(GlobalView gv) {
	                	try{
	                		gv.setValue("simulation.leader1", new TAddress(InetAddress.getByName("0.0.0.0"), 0));
	                		gv.setValue("simulation.leader2", new TAddress(InetAddress.getByName("0.0.0.0"), 0));
	                		gv.setValue("simulation.leader3", new TAddress(InetAddress.getByName("0.0.0.0"), 0));
	                		gv.setValue("simulation.leader4", new TAddress(InetAddress.getByName("0.0.0.0"), 0));
	                		gv.setValue("simulation.leader5", new TAddress(InetAddress.getByName("0.0.0.0"), 0));
	                	} catch (UnknownHostException ex) {
	                		throw new RuntimeException(ex);
	                	}
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
	                    config.put("simulation.checktimeout", 100);
	                    return config;
	                }
	                
	                @Override
	                public Address getNodeAddress() {
	                    return selfAdr;
	                }

	                @Override
	                public Class getComponentDefinition() {
	                    return SimulationObserverELD.class;
	                }

	                @Override
	                public Init getComponentInit() {
	                    return Init.NONE;
	                }
	            };
	        }
	    };
	    
	    
	    static Operation1 startNodeOp = new Operation1<StartNodeEvent, Long>() {

	        public StartNodeEvent generate(final Long self) {
	            return new StartNodeEvent() {
	                TAddress selfAdr;
	                MapRanks ranks = new MapRanks();
	                long initialDelay = 1000;
	                long deltaDelay = 500;

	                {
	                    try {
	                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
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
	                    config.put("keyvaluestore.self.ranks", ranks);
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
	                    return NodeParenteld.class;
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
	    
	    public static SimulationScenario eld() {
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
	                        raise(1, startNodeOp, constant(10000));
	                        raise(1, startNodeOp, constant(20000));
	                        raise(1, startNodeOp, constant(30000));
	                        raise(1, startNodeOp, constant(40000));
	                        raise(1, startNodeOp, constant(50000));
	                    }
	                };
	                
	                SimulationScenario.StochasticProcess killNode = new SimulationScenario.StochasticProcess() {
	                    {
	                        raise(1, killNodeOp, constant(10000));
	                    }  
	                };


	                setup.start();
	                observer.startAfterTerminationOf(0, setup);
	                launchNodes.start();
	                killNode.startAfterTerminationOf(5000, launchNodes);
	                terminateAfterTerminationOf(5000, killNode);
	            }
	        };

	        return scen;
	    }
}
