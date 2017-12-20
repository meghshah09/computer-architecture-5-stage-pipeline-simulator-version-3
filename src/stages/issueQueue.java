package stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;
import common.StateBackup;

public class issueQueue extends Stage{

	public issueQueue(CPUContext cpuContext) {
		super(cpuContext);
	}
	private Map<Instruction,Instruction> lastArithmeticInstructionMap = new HashMap<Instruction,Instruction>();
	
	private final List<Instruction> instructionQueue = new ArrayList<Instruction>();
	
	public boolean addToIssueQueue(Instruction instruction) {
		
		int size=instructionQueue.size();
		if(size <16) {
			instructionQueue.add(instruction);
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public void removeFromIssueQueue(Instruction instruction) {
		
		instructionQueue.remove(instruction);
	}
	
	/*	LSQ extension 
	private int lsqIndex;
	
	public int getLSQIndex(){
		return lsqIndex;
	}

	public void setLSQIndex(int v){
		this.lsqIndex = v;
	}
*/
	@Override
	public void execute(int currentExecutionCycle) {
		// TODO Auto-generated method stub
		if(cpuContext.getDecoderRegisterFiles().getInstruction() !=null 
				&& cpuContext.getDecoderRegisterFiles().getInstruction().getInstruction().equalsIgnoreCase("HALT")) {
			cpuContext.getDecoderRegisterFiles().getInstruction().setPhysicalRegisterInstruction("HALT");
			cpuContext.getDecoderRegisterFiles().getInstruction().setBisMapping(CPUUtils.getLatestBisIndex(cpuContext));
			cpuContext.getReorderBuffer().getReorderBufferQueue().add(cpuContext.getDecoderRegisterFiles().getInstruction());
			cpuContext.getReorderBuffer().getReorderBufferMap().put(cpuContext.getDecoderRegisterFiles().getInstruction(), true);
			cpuContext.getDecoderRegisterFiles().setInstruction(null);
			cpuContext.getFetch().setInstruction(null);
			cpuContext.getFetch().setEndOfProgram(true);
			if(instructionQueue.size() == 0) {
			System.out.println("Issue Queue: Empty");
			}
			else {
				System.out.println("Issue Queue:");
			}
			for(Instruction instruction: instructionQueue) {
		//		System.out.println("TEST"+instructionQueue.size());
				System.out.println("\t"+ instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction());

			}
			return;
		}
		
		
		if(!cpuContext.getDecoderRegisterFiles().isInstructionStalled() && cpuContext.getDecoderRegisterFiles().getInstruction() !=null) {
			
			Instruction instr = cpuContext.getDecoderRegisterFiles().getInstruction();
			instr.setBisMapping(CPUUtils.getLatestBisIndex(cpuContext));
			if(CPUUtils.isBranchedInstruction(instr)) {
				try {
					cpuContext.getStateBackupMap().put(instr, new StateBackup(cpuContext));
					cpuContext.addToBisMap(instr);
					lastArithmeticInstructionMap.put(instr, cpuContext.getDecoderRegisterFiles().getLastArithemeticInstruction());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			instructionQueue.add(instr);
			cpuContext.getReorderBuffer().getReorderBufferQueue().add(instr);
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instr, false);
			if(instr.getInstruction().equalsIgnoreCase("LOAD") || instr.getInstruction().equalsIgnoreCase("STORE") ) {
				
				cpuContext.getLSQ().getLSQList().add(instr);
				cpuContext.getLSQ().getLSQMap().put(instr, null);
			}
			
		}
		if(instructionQueue.size() == 0) {
		System.out.println("Issue Queue: Empty");
		}
		else {
			System.out.println("Issue Queue:");
		}
		for(Instruction instruction: instructionQueue) {
	//		System.out.println("TEST"+instructionQueue.size());
			System.out.println("\t"+ instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction());

		}
		
	}
	
	public List<Instruction> getInstructionQueue() {
		return instructionQueue;
	}

	public Map<Instruction,Instruction> getLastArithmeticInstructionMap() {
		return lastArithmeticInstructionMap;
	}

	public void setLastArithmeticInstructionMap(Map<Instruction,Instruction> lastArithmeticInstructionMap) {
		this.lastArithmeticInstructionMap = lastArithmeticInstructionMap;
	}

}
