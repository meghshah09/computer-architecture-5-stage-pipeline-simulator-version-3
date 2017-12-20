package stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;
import common.Register;
import common.RegisterBufferWrapper;

public class Memory extends Stage {

	public Memory(CPUContext cpuContext) {
		super(cpuContext);

	}

	private Map<Instruction,Integer> valueMap = new HashMap<Instruction,Integer>();

	public Map<Instruction, Integer> getValueMap() {
		return valueMap;
	}


	public void setValueMap(Map<Instruction, Integer> valueMap) {
		this.valueMap = valueMap;
	}

	private Map<Instruction,Integer> bufferMemoryMap = new HashMap<Instruction,Integer>(); 


	public Map<Instruction,Integer> getBufferMemoryMap() {
		return bufferMemoryMap;
	}


	public void setBufferMemoryMap(Map<Instruction,Integer> bufferMemoryMap) {
		this.bufferMemoryMap = bufferMemoryMap;
	}

	/*private void updateZeroFlag() {
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



		if(!nonArithemeticInstruction.contains(this.getInstruction().getInstruction())) {

			int destinationValue=CPUUtils.getOperandValue(cpuContext, this.getInstruction().getDestination());
			if(destinationValue==0) {

				cpuContext.setZeroFlag(true);
				cpuContext.setZeroFlagSetBy(this.getInstruction());

			}
			else {
				cpuContext.setZeroFlag(false);
				cpuContext.setZeroFlagSetBy(this.getInstruction());

			}

		}

	}*/

	@Override
	public void execute(int currentExecutionCycle) {


		this.currentExecutionCycle= currentExecutionCycle;

		if(getCpuContext().getDivAlu4().getInstruction() != null) 
		{
			this.setInstruction(cpuContext.getDivAlu4().getInstruction());
			valueMap.put(cpuContext.getDivAlu4().getInstruction(), cpuContext.getDivAlu1().getDivisionALUBufferMap().get(instruction));
		}
		else if(getCpuContext().getMultiplicationALU2().getInstruction() != null) 
		{
			this.setInstruction(cpuContext.getMultiplicationALU2().getInstruction());
			valueMap.put(cpuContext.getMultiplicationALU2().getInstruction(), cpuContext.getMultiplicationALU().getMultiplicationALUBufferMap().get(instruction));

		}
		else {
			this.setInstruction(cpuContext.getExecution().getInstruction());
			valueMap.put(cpuContext.getExecution().getInstruction(), cpuContext.getExecution().getBufferMap().get(instruction));
		}
		if (this.getInstruction() == null)
		{
			System.out.println("Memory Stage: Empty");			
			return;
		}
		else {
			if (this.getInstruction().getInstruction().contentEquals("LOAD")) {

				Integer memAddress= getCpuContext().getExecution().getBufferMap().get(this.getInstruction());
				Integer memValue= getCpuContext().getMemoryMap().get(memAddress);
				if (memValue==null) {
					memValue=0;
				}

				bufferMemoryMap.put(this.getInstruction(),memValue);
			}
			else if (this.getInstruction().getInstruction().equalsIgnoreCase("STORE"))
			{
				int registerDestinationPosition=CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
				int registerDestinationValue= getCpuContext().getRegisters()[registerDestinationPosition].getValue();
				getCpuContext().getMemoryMap().put(getCpuContext().getExecution().getBufferMap().get(this.getInstruction()), registerDestinationValue);

			}
			else if (this.getInstruction().getInstruction().equalsIgnoreCase("BNZ")) {
				boolean flagVal = false;
				if (valueMap.get(this.getInstruction().getLastArithmeticInstruction()) ==null) {
					flagVal = getCpuContext().isZeroFlag();
					
				}
				else if (valueMap.get(this.getInstruction().getLastArithmeticInstruction()) ==0) {
					flagVal =true;
				}
				else {
					flagVal =false;
				}
				
				if(!flagVal) {

					getCpuContext().setProgramCounter(getCpuContext().getExecution().getBufferMap().get(this.getInstruction()));
					getCpuContext().getFetch().setInstruction(null);
					getCpuContext().getDecoderRegisterFiles().setInstruction(null);
					getCpuContext().getFetch().setBranched(true);
				}

			}
			else if (this.getInstruction().getInstruction().equalsIgnoreCase("BZ")) {
				
				boolean flagVal = false;
				if (valueMap.get(this.getInstruction().getLastArithmeticInstruction()) ==null) {
					flagVal = getCpuContext().isZeroFlag();
					
				}
				else if (valueMap.get(this.getInstruction().getLastArithmeticInstruction()) ==0) {
					flagVal =true;
				}
				else {
					flagVal =false;
				}
				
				
				if(flagVal) {

					getCpuContext().setProgramCounter(getCpuContext().getExecution().getBufferMap().get(this.getInstruction()));
					getCpuContext().getFetch().setInstruction(null);
					getCpuContext().getDecoderRegisterFiles().setInstruction(null);
					getCpuContext().getFetch().setBranched(true);
				}
			}
			else if (this.getInstruction().getInstruction().equalsIgnoreCase("JUMP") || this.getInstruction().getInstruction().equalsIgnoreCase("JAL")) {

				getCpuContext().setProgramCounter(getCpuContext().getExecution().getBufferMap().get(this.getInstruction()));
				getCpuContext().getFetch().setInstruction(null);
				getCpuContext().getDecoderRegisterFiles().setInstruction(null);
				getCpuContext().getFetch().setBranched(true);

			}

			else if(!this.getInstruction().getInstruction().equalsIgnoreCase("HALT")) {
				int value=0;
				if(this.getInstruction().getInstruction().equals("MUL")) {

					value=getCpuContext().getMultiplicationALU().getMultiplicationALUBufferMap().get(this.getInstruction());


				}

				else if (this.getInstruction().getInstruction().equals("DIV")) {

					value=getCpuContext().getDivAlu1().getDivisionALUBufferMap().get(this.getInstruction());

				}


				else {
					value=getCpuContext().getExecution().getBufferMap().get(this.getInstruction());

				}

			}
		}
		System.out.println("Memory Stage: " +instruction.getInstructionNumber()+" "+ instruction.getInstructionString());

		//updateZeroFlag();
	}


	public void clearForwardedValues(){
		if(this.getInstruction()!=null && this.getInstruction().equals(cpuContext.getExecution().getForwardingRegisterInstruction())) {
			Execution forwardingStage = cpuContext.getExecution();
			forwardingStage.setForwardingRegister(null);
			forwardingStage.setForwardingRegisterInstruction(null);
			forwardingStage.setForwardingRegisterValue(0);
		}
		else if(this.getInstruction()!=null && this.getInstruction().equals(cpuContext.getMultiplicationALU2().getForwardingRegisterInstruction())) {
			MultiplicationALU2 forwardingStage = cpuContext.getMultiplicationALU2();
			forwardingStage.setForwardingRegister(null);
			forwardingStage.setForwardingRegisterInstruction(null);
			forwardingStage.setForwardingRegisterValue(0);
		}
		else if(this.getInstruction()!=null && this.getInstruction().equals(cpuContext.getDivAlu4().getForwardingRegisterInstruction())) {
			DivALU4 forwardingStage = cpuContext.getDivAlu4();
			forwardingStage.setForwardingRegister(null);
			forwardingStage.setForwardingRegisterInstruction(null);
			forwardingStage.setForwardingRegisterValue(0);
		}
	}
	
}
