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
import simon.sormain.KeyValueStore.tob.TobDeliver;

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
		subscribe(handleClientPutOperation, net);
		subscribe(handleClientGetOperation, net);
		subscribe(handleClientCASOperation, net);
		subscribe(handleBEDPutOperation, beb);
		subscribe(handleBEDGetOperation, beb);
		subscribe(handleBEDCASOperation, beb);
		
		allRanges = init.getAllRanges();
		self = init.getSelf();
	}
	
	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("{} Router started.", self);
			cache = new HashMap<Integer, Set<TAddress>>();
		}
	};
	

	private ClassMatchedHandler<PutOperation, TMessage> handleClientPutOperation = new ClassMatchedHandler<PutOperation, TMessage>() {
		@Override
		public void handle(PutOperation content, TMessage context) {
			logger.info("{}: Got {} from {}. BEB broadcasting it. \n", self, content.toString(), context.getSource());
			Set<TAddress> dst = correspondingRG(content.getKey());
			trigger(new BEBroadcast(self, dst , content), beb);
		}
	};
	
	private ClassMatchedHandler<GetOperation, TMessage> handleClientGetOperation = new ClassMatchedHandler<GetOperation, TMessage>() {
		@Override
		public void handle(GetOperation content, TMessage context) {
			logger.info("{}: Got {} from {}. BEB broadcasting it. \n", self, content.toString(), context.getSource());
			Set<TAddress> dst = correspondingRG(content.getKey());
			trigger(new BEBroadcast(self, dst , content), beb);
		}
	};
	
	private ClassMatchedHandler<CASOperation, TMessage> handleClientCASOperation = new ClassMatchedHandler<CASOperation, TMessage>() {
		@Override
		public void handle(CASOperation content, TMessage context) {
			logger.info("{}: Got {} from {}. BEB broadcasting it. \n", self, content.toString(), context.getSource());
			Set<TAddress> dst = correspondingRG(content.getKey());
			trigger(new BEBroadcast(self, dst , content), beb);
		}
	};
	
	private ClassMatchedHandler<PutOperation, BEDeliver> handleBEDPutOperation = new ClassMatchedHandler<PutOperation, BEDeliver>() {
		@Override
		public void handle(PutOperation content, BEDeliver context) {
			logger.info("{}: Got {} from {}. Delivering it to the store. \n", self, content.toString(), context.getSrc());
			trigger(content, routy);
			
		}
	};
	
	private ClassMatchedHandler<GetOperation, BEDeliver> handleBEDGetOperation = new ClassMatchedHandler<GetOperation, BEDeliver>() {
		@Override
		public void handle(GetOperation content, BEDeliver context) {
			logger.info("{}: Got {} from {}. Delivering it to the store. \n", self, content.toString(), context.getSrc());
			trigger(content, routy);
			
		}
	};
	
	private ClassMatchedHandler<CASOperation, BEDeliver> handleBEDCASOperation = new ClassMatchedHandler<CASOperation, BEDeliver>() {
		@Override
		public void handle(CASOperation content, BEDeliver context) {
			logger.info("{}: Got {} from {}. Delivering it to the store. \n", self, content.toString(), context.getSrc());
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
			    	logger.debug("RANGE" + Integer.toString(range[1])); //test
			    	result = entry.getValue();
			    	break;
			    }
			}
		}
		return result;
	}

}
