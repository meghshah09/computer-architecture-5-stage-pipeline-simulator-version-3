package stages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;

public class MultiplicationALU extends Stage {

	public MultiplicationALU(CPUContext cpuContext) {
		super(cpuContext);

	}


	
	private Map<Instruction,Integer> multiplicationALUBufferMap = new HashMap<Instruction,Integer>(); 

	public Map<Instruction, Integer> getMultiplicationALUBufferMap() {
		return multiplicationALUBufferMap;
	}



	@Override
	public void execute(int currentExecutionCycle) {

		

		this.currentExecutionCycle= currentExecutionCycle;
		List<Instruction> issueQueueLocal = cpuContext.getIssueQueue().getInstructionQueue();
		this.instruction= null;
		
		for(Instruction instr: issueQueueLocal) {
		
			if(instr == null || !( instr.getInstruction().equalsIgnoreCase("MUL"))){
				this.setInstruction(null);
				
			}
			else if(CPUUtils.isInstructionStalled(instr, cpuContext)) {
				this.setInstruction(null);
			}
			else{	
				this.setInstruction(instr);
				
			}
			if(this.instruction!=null) {
				break;
			}
			
		}
				
		if (this.getInstruction() == null)
		{
			System.out.println("MUL1 Stage: Empty");
			return;
		}
		multiply();
		issueQueueLocal.remove(this.instruction);
		
		System.out.println("MUL1 Stage: "+instruction.getInstructionNumber()+" "+instruction.getPhysicalRegisterInstruction());
/*		this.currentExecutionCycle= currentExecutionCycle;

		if (this.getInstruction()!=null && this.getInstruction()!=cpuContext.getMultiplicationALU2().getInstruction() ) {
			System.out.println("Multiplication Stage1: " +instruction.getInstructionNumber()+" "+ instruction.getInstructionString()+"Stalled");
			return;

		}


			if(getCpuContext().getDecoderRegisterFiles().isInstructionStalled(this.currentExecutionCycle)) {
				this.setInstruction(null);
			}
			else if (cpuContext.getDecoderRegisterFiles().getInstruction()!=null && cpuContext.getDecoderRegisterFiles().getInstruction().getInstruction().equalsIgnoreCase("MUL"))
			{	
				this.setInstruction(cpuContext.getDecoderRegisterFiles().getInstruction());
				multiply();
			} 
			else
			{
			 this.setInstruction(null);	
			}
			
			if (this.getInstruction()== null)
			{
				System.out.println("MultiplicationALU Stage: Empty");
			}
			else {
				System.out.println("MultiplicationALU Stage: " +instruction.getInstructionNumber()+" "+ instruction.getInstructionString());

			}

*/
	}

	public void multiply() {

		int operand1, operand2;
		if(instruction.getPhyscialSource1() != null ) {
				operand1=CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource1(), instruction.getSource1());
			}
			else {
			operand1=CPUUtils.getOperandValue(this.cpuContext, this.getInstruction().getSource1());
			}
		
			if(instruction.getPhyscialSource2() != null ) {
				operand2=CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource2(), instruction.getSource2());
			}
			else {
				operand2=CPUUtils.getOperandValue(this.cpuContext, this.getInstruction().getSource2());
			}
		
		int result= operand1 * operand2 ;
		
		multiplicationALUBufferMap.put(this.getInstruction(), result);
		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());		
	}
}
