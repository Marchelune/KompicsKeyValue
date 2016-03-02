package simon.sormain.KeyValueStore.client;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import simon.sormain.KeyValueStore.app.CASOperation;
import simon.sormain.KeyValueStore.app.GetOperation;
import simon.sormain.KeyValueStore.app.Operation;
import simon.sormain.KeyValueStore.app.OperationACK;
import simon.sormain.KeyValueStore.app.PutOperation;
import simon.sormain.KeyValueStore.app.Status;
import simon.sormain.KeyValueStore.network.TAddress;
import simon.sormain.KeyValueStore.network.TMessage;


/**
 * This client should be able to test the kv-store.
 * @author remi
 *
 */
public class Client extends ComponentDefinition {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	Positive<Network> net = requires(Network.class);
	Positive<ConsolePort> console = requires(ConsolePort.class);
	
	private TAddress self;
	private TAddress kvStore;
	private int sequenceNumber;
	private HashSet<OperationACK> replies;

	public Client(ClientInit init) {
		logger.info("Building client.");
		subscribe(handleStart, control);
		subscribe(handleConsoleLine,console);
		subscribe(handleOperationACK, net);
		
		self = init.getSelf();
		kvStore = init.getKvStore();
	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			logger.info("Client started.");
			sequenceNumber = 0;
		}
	};
	
	private ClassMatchedHandler<OperationACK, TMessage> handleOperationACK = new ClassMatchedHandler<OperationACK, TMessage>() {
		@Override
		public void handle(OperationACK content, TMessage context) {
			if(!replies.contains(content)){
				replies.add(content);
				String info = "New reply : operation " + content.getOp().getUniqueSequenceNumber() 
						+ " " + content.getOp().toString() +"\n";
				if(content.getStatus() == Status.SUCCESS){
					info = info+ "The operation is a success and the returned value is '" + content.getResult() + "'.\n";
				}else{
					info = info+ "FAIL : " + content.getStatus().details() + "\n";
				}
				trigger(new ConsoleLine(info), console);
				//or logger.error(info) ?
			}
			
		}
	};
	
	private Handler<ConsoleLine> handleConsoleLine = new Handler<ConsoleLine>() {
		@Override
		public void handle(ConsoleLine event) {
			String[] command = event.getLine().trim().split("[\\(,\\)]");
			sequenceNumber++;
			if(command[0].equals("PUT")){
				doPut(command,event);
			}else if(command[0].equals("GET")){
				doGet(command, event);
			}else if(command[0].equals("CAS")){
				doCAS(command, event);
			}else if(command[0].equals("q") || command[0].toUpperCase().equals("QUIT")){
				doShutdown();
			}else badCommand(event);
		}
	};
	
	private void badCommand(ConsoleLine event){
		String info = "Client bad command : "+ event.getLine();
		logger.error(info);
		trigger(new ConsoleLine(info), console);
	}
	
	
	private void doPut(String [] command, ConsoleLine event){
		if(command[1] == null || command[2] == null){
			badCommand(event);
		}else{
			Operation op = new PutOperation(self, sequenceNumber, Integer.parseInt(command[1]), command[2]);
			
			trigger( new TMessage(self, kvStore, Transport.TCP, op),net);
		}
	}
	
	private void doGet(String [] command, ConsoleLine event){
		if(command[1] == null){
			badCommand(event);
		}else{
			Operation op = new GetOperation(self, sequenceNumber, Integer.parseInt(command[1]));
			trigger( new TMessage(self, kvStore, Transport.TCP, op),net);
		}
	}
	
	private void doCAS(String [] command, ConsoleLine event){
		if(command[1] == null || command[2] == null || command[3] == null){
			badCommand(event);
		}else{
			Operation op = new CASOperation(self, sequenceNumber, Integer.parseInt(command[1]),command[2],command[3]);
			trigger( new TMessage(self, kvStore, Transport.TCP, op),net);
		}
	}
	
	private void doShutdown() {
		System.out.println("Shutting client down");
		System.out.close();
		System.err.close();
		Kompics.shutdown();
		System.exit(0);
	}

}
