package stages;

import common.CPUContext;

public class DivALU2 extends Stage {

	public DivALU2(CPUContext cpuContext) {
		super(cpuContext);
		// TODO Auto-generated constructor stub
	}

	@Override
public void execute(int currentExecutionCycle) {
		
		this.currentExecutionCycle= currentExecutionCycle;
		
		this.setInstruction(cpuContext.getDivAlu1().getInstruction());
		if(this.getInstruction()== null) 
		{
			System.out.println("DIV2 Stage: Empty");
		return;	
		}
	
		System.out.println("DIV2 Stage: " +instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction());

	}

}
