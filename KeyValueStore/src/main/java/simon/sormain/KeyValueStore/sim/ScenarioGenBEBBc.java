package simon.sormain.KeyValueStore.sim;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

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
import simon.sormain.KeyValueStore.converters.SetTAddress;
import simon.sormain.KeyValueStore.network.*;
import simon.sormain.KeyValueStore.system.*;
import static java.lang.Math.toIntExact;

public class ScenarioGenBEBBc {

    static Operation setupOp = new Operation<SetupEvent>() {
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public void setupGlobalView(GlobalView gv) {
                    gv.setValue("simulation.sentmsgs", 0);
                    gv.setValue("simulation.rcvmsgs", 0);
                    gv.setValue("simulation.rcvmsgsone", 0);
                    gv.setValue("simulation.rcvmsgstwo", 0);
                    gv.setValue("simulation.rcvmsgsthree", 0);
                    gv.setValue("simulation.rcvmsgsfour", 0);
                    gv.setValue("simulation.rcvmsgsfive", 0);
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
                    config.put("simulation.checktimeout", 10);
                    return config;
                }
                
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return SimulationObserverBEB.class;
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }
            };
        }
    };
    
    
    static Operation1 startSenderOp = new Operation1<StartNodeEvent, Long>() {

        public StartNodeEvent generate(final Long self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                SetTAddress allAddr = new SetTAddress();
                long initialDelay = 1000;
                long deltaDelay = 500;
                long senderDelay = 2;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("keyvaluestore.self", selfAdr);
                    config.put("keyvaluestore.epfd.allAddr", allAddr);
                    config.put("keyvaluestore.epfd.initDelay", initialDelay);
                    config.put("keyvaluestore.epfd.deltaDelay", deltaDelay);
                    config.put("simulation.sendertimeout", senderDelay);
                    return config;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return BEBSimuSender.class;
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

    static Operation1 startNodeOp = new Operation1<StartNodeEvent, Long>() {

        public StartNodeEvent generate(final Long self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                SetTAddress allAddr = new SetTAddress();
                long initialDelay = 1000;
                long deltaDelay = 500;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                        allAddr.add(new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("keyvaluestore.self", selfAdr);
                    config.put("keyvaluestore.epfd.allAddr", allAddr);
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
    

    

    public static SimulationScenario BebBc() {
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

                SimulationScenario.StochasticProcess launchSender = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, startSenderOp, constant(10000));
                    }
                };
                SimulationScenario.StochasticProcess launchNodes = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
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
                launchSender.startAfterTerminationOf(1000,launchNodes);
                killNode.startAfterTerminationOf(500, launchSender);
                terminateAfterTerminationOf(100, killNode);
            }
        };

        return scen;
    }

}
