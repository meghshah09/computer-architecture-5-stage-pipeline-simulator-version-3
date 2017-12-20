package stages;

import common.CPUContext;
import common.Instruction;

public abstract class Stage {
	protected Instruction instruction;
	protected CPUContext cpuContext;

	protected int currentExecutionCycle;

	public int getCurrentExecutionCycle() {
		return currentExecutionCycle;
	}



	public void setCurrentExecutionCycle(int currentExecutionCycle) {
		this.currentExecutionCycle = currentExecutionCycle;
	}



	public abstract void execute(int currentExecutionCycle);



	public Stage(CPUContext cpuContext) {
		this.setCpuContext(cpuContext);
	}



	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}



	public CPUContext getCpuContext() {
		return cpuContext;
	}



	public void setCpuContext(CPUContext cpuContext) {
		this.cpuContext = cpuContext;
	}


}

