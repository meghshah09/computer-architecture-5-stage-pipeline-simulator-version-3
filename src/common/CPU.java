package common;

import java.io.FileNotFoundException;

public class CPU {
	
	int cycleCount=0;
	CPUContext cpuContext ;
	private boolean endOfProgram;
	
	public void init(String filePath) throws FileNotFoundException {
		setEndOfProgram(false);
//		if(cpuContext== null) {
			this.cpuContext = new CPUContext(filePath);
//		}
//		else {
//			this.cpuContext.init();
//		}
	}

	int lastCycleCount=-1;
	public int getLastCycleCount() {
		return lastCycleCount;
	}

	public void setLastCycleCount(int lastCycleCount) {
		this.lastCycleCount = lastCycleCount;
	}

	public void executeCycle(int currentExecutionCycle) {
		
	//	cpuContext.getWriteBack().execute(currentExecutionCycle);
	//	cpuContext.getMemory().execute(currentExecutionCycle);
		cpuContext.getARFStage().execute(currentExecutionCycle);
		cpuContext.getMemory().execute(currentExecutionCycle);
		cpuContext.getExecution().execute(currentExecutionCycle);
		cpuContext.getMultiplicationALU2().execute(currentExecutionCycle);
		cpuContext.getMultiplicationALU().execute(currentExecutionCycle);
		cpuContext.getDivAlu4().execute(currentExecutionCycle);
		cpuContext.getDivAlu3().execute(currentExecutionCycle);
		cpuContext.getDivAlu2().execute(currentExecutionCycle);
		cpuContext.getDivAlu1().execute(currentExecutionCycle);
		cpuContext.getIssueQueue().execute(currentExecutionCycle);
		cpuContext.getLSQ().display();
		cpuContext.getDecoderRegisterFiles().execute(currentExecutionCycle);
		cpuContext.getFetch().execute(currentExecutionCycle);
		cpuContext.getARFStage().postExecution();
		cpuContext.getReorderBuffer().display();
		cpuContext.getExecution().postExecution();
		cpuContext.getMultiplicationALU2().postExecution();
		cpuContext.getDivAlu4().postExecution();
		cpuContext.getMemory().postExecution();
		System.out.println("Rename Table : ");
		if(cpuContext.getRenameTable().size()==0){
			System.out.println("\tempty");
		} else {
			for(String arf: cpuContext.getRenameTable().keySet()) {
				System.out.println("\t"+arf+" : "+cpuContext.getRenameTable().get(arf));
			}
		}
		
		if(cpuContext.getReorderBuffer().getReorderBufferQueue().size()>0 && cpuContext.getReorderBuffer().getReorderBufferQueue().get(0).getInstruction().equalsIgnoreCase("HALT")) {
			lastCycleCount = currentExecutionCycle;
			endOfProgram = true;
			return;
		}
		
		if(lastCycleCount==-1 && isLastInstruction(cpuContext)) {
			lastCycleCount = currentExecutionCycle-1;
		}
		
		
//		cpuContext.getExecution().setForwardingValues();
//		cpuContext.getMultiplicationALU2().setForwardingValues();
//		cpuContext.getDivAlu4().setForwardingValues();
//		cpuContext.getMemory().clearForwardedValues();
//		cpuContext.getWriteBack().releaseLocks();
/*		if(cpuContext.getWriteBack().getInstruction()!=null && cpuContext.getWriteBack().getInstruction().getInstruction().equalsIgnoreCase("HALT")) {
			endOfProgram = true;
		}
	
*/	}
	
	private boolean isLastInstruction(CPUContext cpuContext) {
		if(cpuContext.getFetch().getInstruction()==null 
				&& cpuContext.getDecoderRegisterFiles().getInstruction()==null 
				&& cpuContext.getExecution().getInstruction()==null 
				&& cpuContext.getMemory().getInstruction()==null 
				&&cpuContext.getARFStage().getInstruction()==null 
				&& cpuContext.getIssueQueue().getInstructionQueue().size()==0 
				&& (cpuContext.getReorderBuffer().getReorderBufferQueue().isEmpty() )) {
			return true;
		}
		else if (!cpuContext.getReorderBuffer().getReorderBufferQueue().isEmpty() 
				&&(cpuContext.getReorderBuffer().getReorderBufferQueue().get(0).getInstruction().equalsIgnoreCase("HALT") 
				&& cpuContext.getReorderBuffer().getReorderBufferMap().get(cpuContext.getReorderBuffer().getReorderBufferQueue().get(0)))) {
			return true;
		}
			
		return false;
	}
	
	
	public CPUContext getCpuContext() {
		return cpuContext;
	}

	public void setCpuContext(CPUContext cpuContext) {
		this.cpuContext = cpuContext;
	}
	public boolean isEndOfProgram() {
		return endOfProgram;
	}

	public void setEndOfProgram(boolean endOfProgram) {
		this.endOfProgram = endOfProgram;
	}

}
