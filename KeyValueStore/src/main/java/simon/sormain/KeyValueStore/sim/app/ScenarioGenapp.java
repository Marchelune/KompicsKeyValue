package simon.sormain.KeyValueStore.sim.app;

import static java.lang.Math.toIntExact;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation2;

import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.util.GlobalView;
import simon.sormain.KeyValueStore.converters.MapRanges;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.sim.multipaxos.OpSequence;
import simon.sormain.KeyValueStore.sim.tob.OpSet;
import simon.sormain.KeyValueStore.system.NodeParent;

public class ScenarioGenapp {
	static Operation setupOp = new Operation<SetupEvent>() {
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public void setupGlobalView(GlobalView gv) {
                	    //tob
            			gv.setValue("simulation.seqbc2", new OpSequence());
            			gv.setValue("simulation.seqbc3", new OpSequence());
                		gv.setValue("simulation.seqdelivered1", new OpSequence());
                		gv.setValue("simulation.seqdelivered2", new OpSequence());
                		gv.setValue("simulation.seqdelivered3", new OpSequence());
                		gv.setValue("simulation.seqdelivered4", new OpSequence());
                		gv.setValue("simulation.seqdelivered5", new OpSequence());
                		gv.setValue("simulation.globaldelivered", new OpSet());
                		// MultiPaxos
                		gv.setValue("simulation.seqdecided1", new OpSequence());
                		gv.setValue("simulation.seqdecided2", new OpSequence());
                		gv.setValue("simulation.seqdecided3", new OpSequence());
                		gv.setValue("simulation.seqdecided4", new OpSequence());
                		gv.setValue("simulation.seqdecided5", new OpSequence());
                		gv.setValue("simulation.proposedcommands", new OpSequence());
                		//beb
                        gv.setValue("simulation.BEBmsgs", new OpSet());
                        gv.setValue("simulation.BEBdelmsgs1", new OpSet());
                        gv.setValue("simulation.BEBdelmsgs2", new OpSet());
                        gv.setValue("simulation.BEBdelmsgs3", new OpSet());
                        gv.setValue("simulation.BEBdelmsgs4", new OpSet());
                        gv.setValue("simulation.BEBdelmsgs5", new OpSet());
                        //eld
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
    
    static Operation2 startNodeFirstRepGrpOp = new Operation2<StartNodeEvent, Long, Long>() {

        public StartNodeEvent generate(final Long self, final Long rank) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                int selfrank;
                MapRanks ranks = new MapRanks();
                MapRanges ranges = new MapRanges();
                long initialDelay = 1000;
                long deltaDelay = 500;

                {
                    try {
                    	// Ranges
                    	int[] rone = new int[2];
                    	rone[0] = 0;
                    	rone[1] = 999;
                    	HashSet<TAddress> sone = new HashSet<TAddress>();
                    	sone.add(new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                    	sone.add(new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                    	sone.add(new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
                    	ranges.put(rone, sone);
                    	int[] rtwo = new int[2];
                    	rtwo[0] = 1000;
                    	rtwo[1] = 1999;
                    	HashSet<TAddress> stwo = new HashSet<TAddress>();
                    	stwo.add(new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                    	stwo.add(new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                    	stwo.add(new TAddress(InetAddress.getByName("192.168.0.1"), 60000));
                    	ranges.put(rtwo, stwo);
                    	
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                        selfrank = toIntExact(rank);
                        ranks.put(1, new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                        ranks.put(2,new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                        ranks.put(3,new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
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
                    config.put("keyvaluestore.self.ranges", ranges);
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
    
    static Operation2 startNodeSecondRepGrpOp = new Operation2<StartNodeEvent, Long, Long>() {

        public StartNodeEvent generate(final Long self, final Long rank) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                int selfrank;
                MapRanks ranks = new MapRanks();
                MapRanges ranges = new MapRanges();
                long initialDelay = 1000;
                long deltaDelay = 500;

                {
                    try {
                    	// Ranges
                    	int[] rone = new int[2];
                    	rone[0] = 0;
                    	rone[1] = 999;
                    	HashSet<TAddress> sone = new HashSet<TAddress>();
                    	sone.add(new TAddress(InetAddress.getByName("192.168.0.1"), 10000));
                    	sone.add(new TAddress(InetAddress.getByName("192.168.0.1"), 20000));
                    	sone.add(new TAddress(InetAddress.getByName("192.168.0.1"), 30000));
                    	ranges.put(rone, sone);
                    	int[] rtwo = new int[2];
                    	rtwo[0] = 1000;
                    	rtwo[1] = 1999;
                    	HashSet<TAddress> stwo = new HashSet<TAddress>();
                    	stwo.add(new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                    	stwo.add(new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                    	stwo.add(new TAddress(InetAddress.getByName("192.168.0.1"), 60000));
                    	ranges.put(rtwo, stwo);
                    	
                        selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), toIntExact(self));
                        selfrank = toIntExact(rank);
                        ranks.put(4,new TAddress(InetAddress.getByName("192.168.0.1"), 40000));
                        ranks.put(5,new TAddress(InetAddress.getByName("192.168.0.1"), 50000));
                        ranks.put(6,new TAddress(InetAddress.getByName("192.168.0.1"), 60000));
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
                    config.put("keyvaluestore.self.ranges", ranges);
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
    
    static Operation startNodeClientOp = new Operation<StartNodeEvent>() {

        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                TAddress selfAdr;
                

                {
                    try {
                    	selfAdr = new TAddress(InetAddress.getByName("192.168.0.1"), 15000);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    return config;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return ClientHostSimu.class;
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
    
    public static SimulationScenario app() {
        SimulationScenario scen = new SimulationScenario() {
            {

                SimulationScenario.StochasticProcess setup = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, setupOp);
                    }
                };


                SimulationScenario.StochasticProcess launchNodes = new SimulationScenario.StochasticProcess() {
                    {
                    	eventInterArrivalTime(constant(1));
                        raise(1, startNodeFirstRepGrpOp, constant(10000), constant(1));
                        raise(1, startNodeFirstRepGrpOp, constant(20000), constant(2));
                        raise(1, startNodeFirstRepGrpOp, constant(30000), constant(3));
                        raise(1, startNodeSecondRepGrpOp, constant(40000), constant(4));
                        raise(1, startNodeSecondRepGrpOp, constant(50000), constant(5));
                        raise(1, startNodeSecondRepGrpOp, constant(60000), constant(6));
                    }
                };
                
                SimulationScenario.StochasticProcess launchClient = new SimulationScenario.StochasticProcess() {
                    {
                    	eventInterArrivalTime(constant(1));
                        raise(1, startNodeClientOp);
                    }  
                };



                setup.start();
                launchNodes.start();
                launchClient.startAfterTerminationOf(1000, launchNodes);
                terminateAfterTerminationOf(40000, launchClient);
            }
        };

        return scen;
    }
}
