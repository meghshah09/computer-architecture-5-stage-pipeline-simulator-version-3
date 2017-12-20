package stages;

import java.util.ArrayList;
import java.util.List;

import common.CPUContext;
import common.CPUUtils;

public class WriteBack extends Stage {

	public WriteBack(CPUContext cpuContext) {
		super(cpuContext);
	}

	@Override
	public void execute(int currentExecutionCycle) {
/*
		this.currentExecutionCycle= currentExecutionCycle;

		this.setInstruction(cpuContext.getMemory().getInstruction());
		if (this.getInstruction()==null)
		{
			System.out.println("WriteBack Stage: Empty");			
			return ;
		}

		else
		{
			if(this.getInstruction().getInstruction().equalsIgnoreCase("LOAD"))
			{
				int registerDestinationPosition=CPUUtils.getRegisterPosition(this.getInstruction().getDestination()); // getting the index of the destination register which will be used to place the data of the calc memory location.
				getCpuContext().getRegisters()[registerDestinationPosition].setValue(getCpuContext().getMemory().getBufferMemoryMap().get(this.getInstruction())); // now setting the value present at memory location into destination address.
				getCpuContext().getMemory().getBufferMemoryMap().remove(this.getInstruction()); // removing map of bufferMemoryMap which holds the value present inside the memory location calculated in execution stage.
				getCpuContext().getExecution().getBufferMap().remove(this.getInstruction()); // removing map of bufferMap which holds value of register and literal to calc memory address.

			}
			else if(this.getInstruction().getInstruction().equalsIgnoreCase("STORE"))
			{
				getCpuContext().getMemory().getBufferMemoryMap().remove(this.getInstruction()); // removing map of bufferMemoryMap which holds the value present inside the memory location calculated in execution stage.
				getCpuContext().getExecution().getBufferMap().remove(this.getInstruction()); // removing map of bufferMap which holds value of register and literal to calc memory address.

			}
			// DO nothing in this below case, because no write back occurs for them.
			//			else if(this.getInstruction().getInstruction().equalsIgnoreCase("JUMP") || this.getInstruction().getInstruction().equalsIgnoreCase("HALT")) {
			//				return;
			//			}

			else if(this.getInstruction().getInstruction().equalsIgnoreCase("JUMP")) {
				System.out.println("Write Back Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString());
				return;
			}
			else if(this.getInstruction().getInstruction().equalsIgnoreCase("JAL")) {
				System.out.println("Write Back Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString());
				int registerDestinationPosition=CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
				if(registerDestinationPosition != -1) {
					getCpuContext().getRegisters()[registerDestinationPosition].setValue(this.getInstruction().getInstructionAddress()+4);

				}
				getCpuContext().getExecution().getBufferMap().remove(this.getInstruction());

				return;
			}
			else if(this.getInstruction().getInstruction().equalsIgnoreCase("HALT")) {
				System.out.println("Write Back Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString());

				return;
			}
			else if(this.getInstruction().getInstruction().equalsIgnoreCase("MUL")) {
				int registerDestinationPosition=CPUUtils.getRegisterPosition(this.getInstruction().getDestination()); // getting the index of the destination register which will be used to place the data of the calc memory location.
				if(registerDestinationPosition != -1) {
					getCpuContext().getRegisters()[registerDestinationPosition].setValue(getCpuContext().getMultiplicationALU().getMultiplicationALUBufferMap().get(this.getInstruction()));
				}
				getCpuContext().getMultiplicationALU().getMultiplicationALUBufferMap().remove(this.getInstruction()); 


			}

			else if(this.getInstruction().getInstruction().equalsIgnoreCase("DIV")) {
				int registerDestinationPosition=CPUUtils.getRegisterPosition(this.getInstruction().getDestination()); // getting the index of the destination register which will be used to place the data of the calc memory location.
				if(registerDestinationPosition != -1) {
					getCpuContext().getRegisters()[registerDestinationPosition].setValue(getCpuContext().getDivAlu1().getDivisionALUBufferMap().get(this.getInstruction()));
				}
				getCpuContext().getDivAlu1().getDivisionALUBufferMap().remove(this.getInstruction()); 


			}
		
			else {//if(this.getInstruction().getInstruction()!="LOAD" && this.getInstruction().getInstruction()!="STORE") {
				int registerDestinationPosition=CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
				if(registerDestinationPosition != -1) {
					getCpuContext().getRegisters()[registerDestinationPosition].setValue(getCpuContext().getExecution().getBufferMap().get(this.getInstruction()));

				}
				getCpuContext().getExecution().getBufferMap().remove(this.getInstruction());


			}


		}

		System.out.println("Write Back Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString());

*/	}



	public void releaseLocks() 
	{/*
		List<String> nonArithemeticInstruction = new ArrayList<String>();
		nonArithemeticInstruction.add("HALT");
		nonArithemeticInstruction.add("BNZ");
		nonArithemeticInstruction.add("BZ");
		nonArithemeticInstruction.add("JUMP");
		nonArithemeticInstruction.add("LOAD");
		nonArithemeticInstruction.add("STORE");
		nonArithemeticInstruction.add("MOVC");
		nonArithemeticInstruction.add("AND");
		nonArithemeticInstruction.add("OR");
		nonArithemeticInstruction.add("EXOR");

		if (this.getInstruction()==null || this.getInstruction().getInstruction().equalsIgnoreCase("HALT"))
		{

			return ;
		}

		if(!nonArithemeticInstruction.contains(this.getInstruction().getInstruction())) {
			
			int destinationValue = cpuContext.getMemory().getValueMap().get(instruction);
			if(destinationValue==0) {

				cpuContext.setZeroFlag(true);
				cpuContext.setZeroFlagSetBy(this.getInstruction());
				cpuContext.setZeroFlagSetByFetchSequence(this.getInstruction().getFetchSequence());
			}
			else {
				cpuContext.setZeroFlag(false);
				cpuContext.setZeroFlagSetBy(this.getInstruction());
				cpuContext.setZeroFlagSetByFetchSequence(this.getInstruction().getFetchSequence());
			}
		}
		cpuContext.getMemory().getValueMap().remove(instruction);

		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		if(destionationRegisterPosition != -1) 
		{
			if(getCpuContext().getRegisters()[destionationRegisterPosition].getLockingInstruction()==this.getInstruction()) {
				getCpuContext().getRegisters()[destionationRegisterPosition].setValid(true);
				getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(0);
				getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(null);
			}

		}
	*/}
}
