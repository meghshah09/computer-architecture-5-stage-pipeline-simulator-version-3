package stages;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;

public class ARFStage extends Stage {

	public ARFStage(CPUContext cpuContext) {
		super(cpuContext);
		// TODO Auto-generated constructor stub
	}

	int numberOfCommittedInstructions=0;
	
	public void postExecution() {
		if(this.instruction == null)
		return;
		
		cpuContext.getReorderBuffer().getReorderBufferQueue().remove(0);
		cpuContext.getReorderBuffer().getReorderBufferMap().remove(this.instruction);
		
	}
	
	@Override
	public void execute(int currentExecutionCycle) {
		// TODO Auto-generated method stub
		this.instruction=null;
		if(cpuContext.getReorderBuffer().getReorderBufferQueue().size()==0){
			System.out.println("Commit: Empty");
			//postExecution();
			return;
		}
		Instruction instr = cpuContext.getReorderBuffer().getReorderBufferQueue().get(0);

		if(cpuContext.getReorderBuffer().getReorderBufferMap().get(instr) != null 
				&& (cpuContext.getReorderBuffer().getReorderBufferMap().get(instr))
				) 
		{
			this.instruction= instr;
			numberOfCommittedInstructions++;
/*			cpuContext.getReorderBuffer().getReorderBufferQueue().remove();
			cpuContext.getReorderBuffer().getReorderBufferMap().remove(instr);
*/		}
		else if(instr != null && (instr.getInstruction().equalsIgnoreCase("STORE") 
				&& 	instr.equals(cpuContext.getLSQ().getLSQList().get(0)) 
				&&  (cpuContext.getMemory().getCycleCount()==3 || cpuContext.getMemory().getInstruction() == null)
				&& cpuContext.getLSQ().getLSQMap().get(instr) != null 
				&& !CPUUtils.isPhysicalSourceLocked(cpuContext, instr.getPhysicalDestination())
				))
		{ 
			this.instruction = instr;
			numberOfCommittedInstructions++;
		}
		else
			this.instruction= null;
		if(this.instruction == null) {
			System.out.println("Commit: Empty");
			//postExecution();
			return;	

		}
		if(this.instruction.getInstruction().equalsIgnoreCase("HALT")) {
			//postExecution();
			
			return;
		}
		
		System.out.println("Commit: " +instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction());
		
		if(this.instruction.getInstruction().equalsIgnoreCase("BZ") || this.instruction.getInstruction().equalsIgnoreCase("BNZ")) {
			return;
		}
		
		String destinationPhysicalRegister = this.instruction.getPhysicalDestination();
		int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(destinationPhysicalRegister);

		
		
		if(this.instruction.getInstruction().equalsIgnoreCase("STORE")) {
			// Wont do anything for Store. Because store is not having destination.	
		}		
		
		else{
			int destinationPhysicalRegisterValue = cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].getValue();
			int destinationRegisterPosition = CPUUtils.getRegisterPosition(this.instruction.getDestination());
			cpuContext.getRegisters()[destinationRegisterPosition].setValue(destinationPhysicalRegisterValue);
		}

		if(!cpuContext.getRenameTable().get(this.instruction.getDestination()).equalsIgnoreCase(destinationPhysicalRegister)){
			if(cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].getLastReferredBy().get(0).equals(this.instruction)) {
				cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setReadyToFree(true);
				cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setAllocated(false);

			}
		}
		else {
			if(cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].getLastReferredBy().get(0).equals(this.instruction)) {
				cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setReadyToFree(true);

			}

		}

		String sourcePhysicalRegister1 = this.instruction.getPhysicalDestination();
		int sourcePhysicalRegister1Position = CPUUtils.getPhysicalRegisterPosition(sourcePhysicalRegister1);
		String sourcePhysicalRegister2 = this.instruction.getPhysicalDestination();
		int sourcePhysicalRegister2Position = CPUUtils.getPhysicalRegisterPosition(sourcePhysicalRegister2);


		if(!cpuContext.getRenameTable().get(this.instruction.getDestination()).equalsIgnoreCase(sourcePhysicalRegister1)){
			if(cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Position].getLastReferredBy().get(0).equals(this.instruction)) {
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Position].setReadyToFree(true);
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Position].setAllocated(false);

			}
		}
		else {
			if(cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Position].getLastReferredBy().get(0).equals(this.instruction)) {
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Position].setReadyToFree(true);

			}

		}

		// For source2, checking if new instance is created in rename table, if yes then then if condition will be executed. otherwise we are
		if(!cpuContext.getRenameTable().get(this.instruction.getDestination()).equalsIgnoreCase(sourcePhysicalRegister2)){
			if(cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Position].getLastReferredBy().get(0).equals(this.instruction)) {
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Position].setReadyToFree(true);
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Position].setAllocated(false); // now any other register can use this physical register.

			}
		}
		else {
			if(cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Position].getLastReferredBy().get(0).equals(this.instruction)) {
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Position].setReadyToFree(true); 

			}

		}		

		//postExecution();

	}

	public int getNumberOfCommittedInstructions() {
		return numberOfCommittedInstructions;
	}

	public void setNumberOfCommittedInstructions(int numberOfCommittedInstructions) {
		this.numberOfCommittedInstructions = numberOfCommittedInstructions;
	}

}