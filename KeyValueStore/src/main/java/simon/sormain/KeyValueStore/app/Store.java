package simon.sormain.KeyValueStore.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.TreeMap;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;
import simon.sormain.KeyValueStore.rBroadcast.BEBroadcastPort;
import simon.sormain.KeyValueStore.tob.TobBroadcast;
import simon.sormain.KeyValueStore.tob.TobDeliver;
import simon.sormain.KeyValueStore.tob.TotalOrderBroadcastPort;


/**
 * The highest layer in our system which stores actual (key,value)s. 
 * @author remi
 *
 */
public class Store extends ComponentDefinition {
	
private static final Logger logger = LoggerFactory.getLogger(Store.class);
	
	private Positive<RouterPort> routy = requires(RouterPort.class);
	private Positive<TotalOrderBroadcastPort> tob = requires(TotalOrderBroadcastPort.class);
	private Positive<Network> net = requires(Network.class);
	
	private TreeMap<Integer, String> storedValues; //Let's say we'll store string.
	private final TAddress self;
	
	public Store(StoreInit init) {
		subscribe(handleStart, control);
		subscribe(handleRoutedOperation,routy);
		subscribe(handlePut,tob);
		subscribe(handleGet,tob);
		subscribe(handleCAS,tob);
	
		
		self = init.getSelfAddress();
	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			storedValues = new TreeMap<Integer, String>();
		}
	};
	
	private Handler<Operation> handleRoutedOperation = new Handler<Operation>() {
		@Override
		public void handle(Operation event) {
			// So maybe here we should check if this is really the good store for the key, but I assume the
			// router component does its job properly, I paid him well.
			trigger(new TobBroadcast(event), tob);
		}
	};
	
	private ClassMatchedHandler<PutOperation, TobDeliver> handlePut = new ClassMatchedHandler<PutOperation, TobDeliver>() {	
		@Override
		public void handle(PutOperation content, TobDeliver context) {
			logger.info("Performing " + content.toString());
			storedValues.put(content.getKey(), content.getValue());
			trigger(new TMessage(self, content.getClient(), Transport.TCP, 
					new OperationACK(content, Status.SUCCESS, content.getValue())), net);
			//logger.info("sending ACK for PUT to {} ",content.getClient());
		}
	};
	
	private ClassMatchedHandler<GetOperation, TobDeliver> handleGet = new ClassMatchedHandler<GetOperation, TobDeliver>() {
		@Override
		public void handle(GetOperation content, TobDeliver context) {
			logger.info("Performing " + content.toString());
			String result = storedValues.get(content.getKey());
			if(result != null){
				trigger(new TMessage(self, content.getClient(), Transport.TCP, 
						new OperationACK(content, Status.SUCCESS, result)), net);
				//logger.info("sending ACK for GET to {} ",content.getClient());
			}else{
				logger.error(content.toString() + " FAILED !");
				trigger(new TMessage(self, content.getClient(), Transport.TCP, 
						new OperationACK(content, Status.NOTFOUND, result)), net);
			}
			
		}
	};
	
	private ClassMatchedHandler<CASOperation, TobDeliver> handleCAS = new ClassMatchedHandler<CASOperation, TobDeliver>() {
		@Override
		public void handle(CASOperation content, TobDeliver context) {
			logger.info("Performing " + content.toString());
			String result = storedValues.get(content.getKey());
			if(result != null){
				if(result.equals(content.getReferenceValue())){
					storedValues.put(content.getKey(), content.getNewValue());
					trigger(new TMessage(self, content.getClient(), Transport.TCP, 
							new OperationACK(content, Status.SUCCESS, content.getNewValue())), net);
				}else{
					logger.error(content.toString() + " FAILED !");
					trigger(new TMessage(self, content.getClient(), Transport.TCP, 
							new OperationACK(content, Status.CASFAILED, result)), net);
				}
			}else{
				logger.error(content.toString() + " FAILED !");
				trigger(new TMessage(self, content.getClient(), Transport.TCP, 
						new OperationACK(content, Status.NOTFOUND, result)), net);
			}
		}
	};


}
