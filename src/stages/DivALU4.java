package stages;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;

public class DivALU4 extends Stage{

	public DivALU4(CPUContext cpuContext) {
		super(cpuContext);
	}

	private String forwardingRegister="";
	private int forwardingRegisterValue=0;
	private Instruction forwardingRegisterInstruction= null;

	
	public void setForwardingValues() {
		Instruction currentInstruction = this.getInstruction();
		if(currentInstruction ==null || currentInstruction.getInstruction().equalsIgnoreCase("HALT")) {
			return;
		}
		setForwardingRegister(currentInstruction.getDestination());
		setForwardingRegisterValue(cpuContext.getDivAlu1().getDivisionALUBufferMap().get(currentInstruction));
		setForwardingRegisterInstruction(currentInstruction);
	}
	


	
	public void postExecution() {
		if(this.instruction== null)
			return;
		int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(this.instruction.getPhysicalDestination());
		cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValue(cpuContext.getDivAlu1().getDivisionALUBufferMap().get(this.instruction));
		cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(true);
		cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
		cpuContext.getExecution().getBufferMap().put(instruction, cpuContext.getDivAlu1().getDivisionALUBufferMap().get(this.instruction));
//s		cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
//		cpuContext.getExecution().getPhysicalForwardingBufferMap().put(this.getInstruction().getPhysicalDestination(), cpuContext.getDivAlu1().getDivisionALUBufferMap().get(this.instruction));
	
	}

	
	
	
	@Override
	

public void execute(int currentExecutionCycle) {
		
		this.currentExecutionCycle= currentExecutionCycle;
		
		this.setInstruction(cpuContext.getDivAlu3().getInstruction());
		if(this.getInstruction()== null) 
		{
			System.out.println("DIV4 Stage: Empty");
		return;	
		}
	
		System.out.println("DIV4 Stage: " +instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction());

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
