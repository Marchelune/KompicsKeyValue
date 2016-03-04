package simon.sormain.KeyValueStore.sim.app;

import se.sics.kompics.Init;

public class CreateCommandsInit extends Init<CreateCommandsComponent> {
	
	String typeOp;
	long period;
	
	public CreateCommandsInit(String typeOp, long period){
		super();
		this.typeOp = typeOp;
		this.period = period;
	}
	
	public String getTypeOp(){
		return this.typeOp;
	}
	
	public long getPeriod(){
		return this.period;
	}

}
