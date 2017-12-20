package stages;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;
import common.RegisterBufferWrapper;

public class MultiplicationALU2 extends Stage{

	public MultiplicationALU2(CPUContext cpuContext) {
		super(cpuContext);
	
	}
	
	private String forwardingRegister="";
	private int forwardingRegisterValue=0;
	private Instruction forwardingRegisterInstruction= null;

	
	public void setForwardingValues() {
		Instruction currentInstruction = this.getInstruction();
		if(currentInstruction ==null) {
			return;
		}
		setForwardingRegister(currentInstruction.getDestination());
		setForwardingRegisterValue(cpuContext.getMultiplicationALU().getMultiplicationALUBufferMap().get(currentInstruction));
		setForwardingRegisterInstruction(currentInstruction);
	}

	public void postExecution() {
		if(this.instruction== null)
			return;
		int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(this.instruction.getPhysicalDestination());
		cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValue(cpuContext.getMultiplicationALU().getMultiplicationALUBufferMap().get(this.instruction));
		cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(true);
		cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
		cpuContext.getExecution().getBufferMap().put(instruction, cpuContext.getMultiplicationALU().getMultiplicationALUBufferMap().get(this.instruction));
	//	cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
	//	cpuContext.getExecution().getPhysicalForwardingBufferMap().put(this.getInstruction().getPhysicalDestination(), cpuContext.getMultiplicationALU().getMultiplicationALUBufferMap().get(this.instruction));
	
	}

	
	
	@Override
	public void execute(int currentExecutionCycle) {
		
		this.currentExecutionCycle= currentExecutionCycle;
/*		if (this.getInstruction()!=null && this.getInstruction()!=cpuContext.getMemory().getInstruction() ) {
			System.out.println("Multiplication Stage2: " +instruction.getInstructionNumber()+" "+ instruction.getInstructionString()+" Stalled");
			return;

		}
*/
		
		this.setInstruction(cpuContext.getMultiplicationALU().getInstruction());
		if(this.getInstruction()== null) 
		{
			System.out.println("MUL2 Stage: Empty");
		return;	
		}
	
		System.out.println("MUL2 Stage: " +instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction());

	}


	public String getForwardingRegister() {
		return forwardingRegister;
	}


	public void setForwardingRegister(String forwardingRegister) {
		this.forwardingRegister = forwardingRegister;
	}


	public int getForwardingRegisterValue() {
		return forwardingRegisterValue;
	}


	public void setForwardingRegisterValue(int forwardingRegisterValue) {
		this.forwardingRegisterValue = forwardingRegisterValue;
	}


	public Instruction getForwardingRegisterInstruction() {
		return forwardingRegisterInstruction;
	}


	public void setForwardingRegisterInstruction(Instruction forwardingRegisterInstruction) {
		this.forwardingRegisterInstruction = forwardingRegisterInstruction;
	}
	
	


}
