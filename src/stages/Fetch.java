package stages;

import common.CPUContext;
import common.Instruction;

public class Fetch extends Stage{
	
	private boolean endOfProgram=false;
	private boolean isBranched=false;

	int fetchSequence=1;
	
	public Fetch(CPUContext cpuContext) {
		super(cpuContext);
	}

	@Override
	public void execute(int currentExecutionCycle) {
		
		this.currentExecutionCycle= currentExecutionCycle;
		
		if( isFetchInstructionStalled()) {
			System.out.println("Fetch Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString()+" Stalled");
			return ;
		}
		else if (endOfProgram && !isBranched) {
			System.out.println("Fetch Stage: Empty");
			return;
		}
		int programCounter= cpuContext.getProgramCounter();
		
		if( programCounter ==-1 || cpuContext.getInstructionMap().get(programCounter)==null) {
			endOfProgram = true;
			this.setInstruction(null);
			System.out.println("Fetch Stage: Empty");
			programCounter+=4;
			cpuContext.setProgramCounter(programCounter);
			return;
			
		}
		else {
			endOfProgram=false;
			isBranched=false;
		}
		try {
			setInstruction((Instruction)(cpuContext.getInstructionMap().get(programCounter).clone()));
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		instruction.setFetchSequence(fetchSequence);
		fetchSequence++;
		programCounter+=4;
		cpuContext.setProgramCounter(programCounter);
		System.out.println("Fetch Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString());
	}

	public boolean isEndOfProgram() {
		return endOfProgram;
	}

	public void setEndOfProgram(boolean endOfProgram) {
		this.endOfProgram = endOfProgram;
	}
	
	public boolean isFetchInstructionStalled() {
		if(this.getInstruction()== null) {
			return false;
		}
		Instruction instructionInDecodeStage = getCpuContext().getDecoderRegisterFiles().getInstruction();
		if(instructionInDecodeStage != this.getInstruction()) {
			return true;
		}
		return false;
		
	}

	public boolean isBranched() {
		return isBranched;
	}

	public void setBranched(boolean isBranched) {
		this.isBranched = isBranched;
	}
	
}
