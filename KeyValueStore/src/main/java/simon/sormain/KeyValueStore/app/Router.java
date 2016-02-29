package simon.sormain.KeyValueStore.app;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcast;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;
import simon.sormain.KeyValueStore.rBroadcast.BEDeliver;

/**
 * This component is in charge of redirecting a client request (net port) to the right RG.
 * It must be provided an init with the different ranges and corresponding addresses for each RG.
 * @author remi
 *
 */
public class Router extends ComponentDefinition{
	
	private static final Logger logger = LoggerFactory.getLogger(Router.class);
	
	private Positive<BEBroadcastPort> beb = requires(BEBroadcastPort.class);
	private Positive<Network> net = requires(Network.class);
	private Negative<RouterPort> routy = provides(RouterPort.class); 
	

	private HashMap<int[], Set<TAddress>> allRanges;
	private HashMap<Integer, Set<TAddress>> cache; //This is a beautiful optimization.
	private TAddress self;
	
	public Router(RouterInit init) {
		subscribe(handleStart, control);
		subscribe(handleClientOperation,net);
		subscribe(handleBEDOperation, beb);
		
		allRanges = init.getAllRanges();
		self = init.getSelf();
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("Router started.");
			cache = new HashMap<Integer, Set<TAddress>>();
		}
	};
	
	private ClassMatchedHandler<Operation, TMessage> handleClientOperation = new ClassMatchedHandler<Operation, TMessage>() {
		@Override
		public void handle(Operation content, TMessage context) {
			Set<TAddress> dst = correspondingRG(content.getKey());
			trigger(new BEBroadcast(self, dst , content), beb);
		}
	};
	
	private ClassMatchedHandler<Operation, BEDeliver> handleBEDOperation = new ClassMatchedHandler<Operation, BEDeliver>() {
		@Override
		public void handle(Operation content, BEDeliver context) {
			trigger(content, routy);
			
		}
	};

	/**
	 * 
	 * @param key
	 * @return the corresponding set of addresses of the replication group. 
	 */
	private Set<TAddress> correspondingRG(int key){
		Set<TAddress> result = cache.get(key);
		if(result == null){
			for (Entry<int[], Set<TAddress>> entry : allRanges.entrySet()) {
			    int[] range = entry.getKey();
			    if(key<range[1]){
			    	result = entry.getValue();
			    	break;
			    }
			}
		}
		return result;
	}

}
