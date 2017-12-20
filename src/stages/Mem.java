package stages;

import java.util.HashMap;
import java.util.Map;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;

public class Mem extends Stage {

	public Mem(CPUContext cpuContext) {
		super(cpuContext);
		// TODO Auto-generated constructor stub
	}
	private Integer storeRegisterBackup=null;
	private int cycleCount=0;
	public int getCycleCount() {
		return cycleCount;
	}

	private Integer memAddress =null;
	public void setCycleCount(int cycleCount) {
		this.cycleCount = cycleCount;
	}
	private Map<Instruction,Integer> memStageBufferMap = new HashMap<Instruction,Integer>(); 


	@Override
	public void execute(int currentExecutionCycle) {
		// TODO Auto-generated method stub
		if(cycleCount == 3 || this.instruction == null) {
			cycleCount=0;
		}

		cycleCount++;

		if(cycleCount == 1) {
			this.instruction = null;
			if(cpuContext.getLSQ().getLSQList().size()>0) {
				Instruction instr = cpuContext.getLSQ().getLSQList().get(0);
				if(("LOAD".equalsIgnoreCase(instr.getInstruction()) && cpuContext.getLSQ().getLSQMap().get(instr)!=null)||(cpuContext.getLSQ().getLSQMap().get(instr)!=null && cpuContext.getReorderBuffer().getReorderBufferQueue().get(0).equals(instr) )) {
					if((instr.getInstruction().equalsIgnoreCase("STORE") && !CPUUtils.isPhysicalSourceLocked(cpuContext, instr.getPhysicalDestination())  ) 
							|| instr.getInstruction().equalsIgnoreCase("LOAD") ) {
						this.instruction=instr;
						memAddress = cpuContext.getLSQ().getLSQMap().get(instruction);
						cpuContext.getLSQ().getLSQList().remove(instr);
						cpuContext.getLSQ().getLSQMap().remove(instr);
						
						if(instr.getInstruction().equalsIgnoreCase("STORE")) {
							if(cpuContext.getARFStage().getInstruction()== null) {
/*								cpuContext.getReorderBuffer().getReorderBufferMap().remove(instr);
								cpuContext.getReorderBuffer().getReorderBufferQueue().remove(instr);
*/								cpuContext.getReorderBuffer().getReorderBufferMap().put(instr, true);
							}else {
								cpuContext.getReorderBuffer().getReorderBufferMap().put(instr, true);
							}
							storeRegisterBackup = CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instr.getPhysicalDestination(), instr.getDestination());
						}
					}
				}
			}
		}
		if(cycleCount == 3) {
			if (this.getInstruction().getInstruction().contentEquals("LOAD")) {

				//Integer memAddress= memAddress;
				Integer memValue= getCpuContext().getMemoryMap().get(memAddress);
				if (memValue==null) {
					memValue=0;
				}

				memStageBufferMap.put(instruction, memValue);	

			}
			else if (this.getInstruction().getInstruction().equalsIgnoreCase("STORE"))
			{
				//Integer memAddress= getCpuContext().getLSQ().getLSQMap().get(instruction);

				/*		int physicalRegisterDestinationPosition=CPUUtils.getPhysicalRegisterPosition(this.getInstruction().getPhysicalDestination());
				int physicalRegisterDestinationValue= getCpuContext().getPhysicalRegisters()[physicalRegisterDestinationPosition].getValue();
				 */		getCpuContext().getMemoryMap().put(memAddress, storeRegisterBackup);

			}

		}

		if(this.instruction== null) {
			System.out.println("Memory Stage: Empty");
		}
		else if(this.getInstruction().getInstructionString().equalsIgnoreCase("NOP")){
			System.out.println("Memory Stage: " +instruction.getInstructionNumber()+" "+instruction.getInstructionString());
		}
		else {
			System.out.println("Memory Stage: " +instruction.getInstructionNumber()+" "+instruction.getPhysicalRegisterInstruction());
		}
	}


	public void postExecution() {

		if(cycleCount<3)
			return;
		if(this.instruction== null || this.instruction.getInstructionString().equalsIgnoreCase("NOP"))
			return;
		else if (this.getInstruction().getInstruction().equalsIgnoreCase("STORE")) {
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
			return;
		}
		else {
			int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(this.instruction.getPhysicalDestination());
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValue(this.getMemStageBufferMap().get(this.instruction));
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(true);
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
			return;
			//	cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
			//	cpuContext.getExecution().getPhysicalForwardingBufferMap().put(this.getInstruction().getPhysicalDestination(), cpuContext.getMultiplicationALU().getMultiplicationALUBufferMap().get(this.instruction));

		}
	}



	public Map<Instruction,Integer> getMemStageBufferMap() {
		return memStageBufferMap;
	}


	public void setMemStageBufferMap(Map<Instruction,Integer> memStageBufferMap) {
		this.memStageBufferMap = memStageBufferMap;
	}
}
