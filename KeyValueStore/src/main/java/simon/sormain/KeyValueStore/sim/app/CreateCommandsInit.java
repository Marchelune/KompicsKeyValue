package simon.sormain.KeyValueStore.sim.app;

import se.sics.kompics.Init;

public class CreateCommandsInit extends Init<CreateCommandsComponent> {
	
	String typeOp;
	long period;
	boolean CAS = false;
	
	public CreateCommandsInit(String typeOp, long period){
		super();
		this.typeOp = typeOp;
		this.period = period;
	}
	
	public CreateCommandsInit(String typeOp, long period, boolean CAS){
		super();
		this.typeOp = typeOp;
		this.period = period;
		this.CAS = CAS;
	}
	
	public String getTypeOp(){
		return this.typeOp;
	}
	
	public long getPeriod(){
		return this.period;
	}
	
	public boolean getCAS(){
		return this.CAS;
	}

}
