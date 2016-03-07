package simon.sormain.KeyValueStore.sim.multipaxos;

import java.util.HashSet;
import java.util.TreeMap;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import simon.sormain.KeyValueStore.asc.AbortableSequenceConsensusPort;
import simon.sormain.KeyValueStore.asc.MultiPaxos;
import simon.sormain.KeyValueStore.asc.MultiPaxosInit;
import simon.sormain.KeyValueStore.converters.MapRanks;
import simon.sormain.KeyValueStore.network.TAddress;

public class NodeParentMultiPaxosProposer extends ComponentDefinition{

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    
    public NodeParentMultiPaxosProposer(){
    	TAddress selfAddress = config().getValue("keyvaluestore.self.addr", TAddress.class);
    	int selfRank = config().getValue("keyvaluestore.self.rank", Integer.class);
    	MapRanks mRanks = config().getValue("keyvaluestore.self.ranks", MapRanks.class);
		TreeMap<Integer, TAddress> Ranks = mRanks.getMap();
		
		// Get all addrs using Ranks
		HashSet<TAddress> alladdr = new HashSet<TAddress>(Ranks.values());
		
		//create and connect all components except timer and network
		Component asc = create(MultiPaxos.class, new MultiPaxosInit(selfAddress, selfRank, alladdr)); //TODO rank
		Component proposer = create(SendProposeMPaxosComponent.class, Init.NONE);
		
		//connect required internal components to network and timer
		connect(asc.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(proposer.getNegative(AbortableSequenceConsensusPort.class), asc.getPositive(AbortableSequenceConsensusPort.class), Channel.TWO_WAY); 
        connect(proposer.getNegative(Timer.class), timer, Channel.TWO_WAY);
    }
}