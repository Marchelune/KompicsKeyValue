package simon.sormain.KeyValueStore.sim.app;

import se.sics.kompics.Init;

public class ClientHostSimuInit extends Init<ClientHostSimu> {
	
	boolean CAS;
	
	public ClientHostSimuInit(boolean CAS){
		super();
		this.CAS = CAS;
	}
	
	public boolean getCAS(){
		return this.CAS;
	}
}
