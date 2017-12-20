package stages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;

public class DivALU1 extends Stage {

	public DivALU1(CPUContext cpuContext) {
		super(cpuContext);
		// TODO Auto-generated constructor stub
	}

	private Map<Instruction,Integer> divisionALUBufferMap = new HashMap<Instruction,Integer>(); 

	public Map<Instruction,Integer> getDivisionALUBufferMap() {
		return divisionALUBufferMap;
	}

	public void setDivisionALUBufferMap(Map<Instruction,Integer> divisionALUBufferMap) {
		this.divisionALUBufferMap = divisionALUBufferMap;
	}




	@Override
	public void execute(int currentExecutionCycle) {

		this.currentExecutionCycle= currentExecutionCycle;
		List<Instruction> issueQueueLocal = cpuContext.getIssueQueue().getInstructionQueue();
		this.instruction= null;
		
		for(Instruction instr: issueQueueLocal) {
		
			if(instr == null || !( instr.getInstruction().equalsIgnoreCase("DIV"))){
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
			System.out.println("DIV1 Stage: Empty");
			return;
		}
		divide();
		issueQueueLocal.remove(this.instruction);
		
		System.out.println("DIV1 Stage: "+instruction.getInstructionNumber()+" "+instruction.getPhysicalRegisterInstruction());


			
//		if(getCpuContext().getDecoderRegisterFiles().isInstructionStalled(this.currentExecutionCycle)) {
//			this.setInstruction(null);
//		}
/*		else if (cpuContext.getDecoderRegisterFiles().getInstruction()!=null 
				&& cpuContext.getDecoderRegisterFiles().getInstruction().getInstruction().equalsIgnoreCase("DIV"))
		{	
			this.setInstruction(cpuContext.getDecoderRegisterFiles().getInstruction());
			divide();
		} 
		else if (cpuContext.getDecoderRegisterFiles().getInstruction()!=null
				&& cpuContext.getDecoderRegisterFiles().getInstruction().getInstruction().equalsIgnoreCase("HALT"))
		{	
			this.setInstruction(cpuContext.getDecoderRegisterFiles().getInstruction());
			halt();
		} 
		else
		{
			this.setInstruction(null);	
		}

		if (this.getInstruction()== null)
		{
			System.out.println("DivALU Stage1: Empty");
		}
		else {
			System.out.println("DivALU Stage1:" +instruction.getInstructionNumber()+" "+ instruction.getInstructionString());

		}
*/

	}



/*	public void halt() {
		cpuContext.getDecoderRegisterFiles().setInstruction(null);
		cpuContext.getFetch().setInstruction(null);
		getCpuContext().setProgramCounter(-1);
	}
*/
	public void divide() {

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

		int result= operand1 / operand2 ;

		divisionALUBufferMap.put(this.getInstruction(), result);
		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		int source1RegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getSource1());
		int source2RegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getSource2());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
	}



}
