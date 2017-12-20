package stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CPUContext;
import common.CPUUtils;
import common.Instruction;
import common.Register;
import common.RenameTable;

public class DecodeRegisterFiles extends Stage {

	private Instruction lastArithemeticInstruction= null;
	private int lastArithmeticInstrictionFetchSequence=0;	
	private Map<String,Integer> forwardedMap = new HashMap<String,Integer>();

	public Instruction getLastArithemeticInstruction() {
		return lastArithemeticInstruction;
	}

	public void setLastArithemeticInstruction(Instruction lastArithemeticInstruction) {
		this.lastArithemeticInstruction = lastArithemeticInstruction;
	}

	public DecodeRegisterFiles(CPUContext cpuContext) {
		super(cpuContext);
	}

	@Override
	public void execute(int currentExecutionCycle) {
		
		
		
		
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
		
		this.currentExecutionCycle= currentExecutionCycle;		

		this.fetchForwardedValuesIntoDRFStage();

		/*	if(this.isInstructionStalled(currentExecutionCycle) || this.isInstructionStalledAtNextStage()) {
			System.out.println("Decode RF Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString()+" Stalled");
			return;

		}
		 */
		if(this.isInstructionStalled()) {
			System.out.println("Decode RF Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString()+" Stalled");
			return;

		}
		

		this.setInstruction(cpuContext.getFetch().getInstruction());
		forwardedMap.clear();
		if (this.getInstruction()==null) {
			System.out.println("Decode RF Stage: Empty");
			return;

		}
		
		//		cpuContext.getReorderBuffer().getReorderBufferQueue().add(instruction);
		//		cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction, false);
		//cpuContext.getIssueQueue().addToIssueQueue(instruction);
		//TODO need to do for LSQ later

		List<String> instructionWithoutDestination = new ArrayList<String>();
		instructionWithoutDestination.add("STORE");
		instructionWithoutDestination.add("HALT");
		instructionWithoutDestination.add("JUMP");
		instructionWithoutDestination.add("BZ");
		instructionWithoutDestination.add("BNZ");

		List<String> instructionWithoutRegister = new ArrayList<String>(); // number of elements
		instructionWithoutRegister.add("HALT");
		instructionWithoutRegister.add("BZ");
		instructionWithoutRegister.add("BNZ");

		
		String [] strArray = null;
		strArray = instruction.getInstructionString().split("[,\\s\\#]+");
		instruction.setInstruction(strArray[0]);


		if(strArray.length>1)
		{
			instruction.setDestination(strArray[1]);

		}
		else
		{
			instruction.setDestination(null); // if Halt instr comes, then in that case no destination will be present.
		}


		if(strArray.length>2)
		{
			instruction.setSource1(strArray[2]);

		}
		else
		{
			instruction.setSource1(null); // if BNZ instr comes, than in that case only one register will be needed.
		}

		if(strArray.length>3) {
			instruction.setSource2(strArray[3]);
		}
		else
		{
			instruction.setSource2(null);	// if MOVC R1 #10 type instruction comes than it will not take source2.
		}
		
		if(!nonArithemeticInstruction.contains(instruction.getInstruction())){
			this.lastArithemeticInstruction = this.instruction;
			
		}

		
		if (instructionWithoutDestination.contains(instruction.getInstruction())) {

			// TODO, Need to implement for other instruction.	
			
			
			
			if(!instructionWithoutRegister.contains(instruction.getInstruction())) {

				String destinationPhysicalRegister = cpuContext.getRenameTable().get(instruction.getDestination());

				this.instruction.setPhysicalDestination(destinationPhysicalRegister);
				//this.instruction.formPhysicalRegisterInstruction();
				
				if(destinationPhysicalRegister != null) {
					instruction.setPhysicalDestination(destinationPhysicalRegister);
					int destinationPhysicalRegisterPostion = CPUUtils.getPhysicalRegisterPosition(destinationPhysicalRegister);
					cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPostion].getLastReferredBy().add(0, instruction);
					cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPostion].setReadyToFree(false);
				}
				


				String sourcePhysicalRegister1 = cpuContext.getRenameTable().get(instruction.getSource1());


				if(sourcePhysicalRegister1 != null) {
					instruction.setPhyscialSource1(sourcePhysicalRegister1);
					int sourcePhysicalRegister1Postion = CPUUtils.getPhysicalRegisterPosition(sourcePhysicalRegister1);
					cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Postion].getLastReferredBy().add(0, instruction);
					cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Postion].setReadyToFree(false);
				}

				if(instruction.getInstruction().equalsIgnoreCase("STORE")) {
					String sourcePhysicalRegister2 = cpuContext.getRenameTable().get(instruction.getSource2());
					if(sourcePhysicalRegister2 != null) {
						instruction.setPhyscialSource2(sourcePhysicalRegister2);

						int sourcePhysicalRegister2Postion = CPUUtils.getPhysicalRegisterPosition(sourcePhysicalRegister2);
						cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Postion].getLastReferredBy().add(0, instruction);
						cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Postion].setReadyToFree(false);
						
					}
				}
				this.instruction.formPhysicalRegisterInstruction();
			}
		}
		else {

			String sourcePhysicalRegister1 = cpuContext.getRenameTable().get(instruction.getSource1());


			if(sourcePhysicalRegister1 != null) {
				instruction.setPhyscialSource1(sourcePhysicalRegister1);
				int sourcePhysicalRegister1Postion = CPUUtils.getPhysicalRegisterPosition(sourcePhysicalRegister1);
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Postion].getLastReferredBy().add(0, instruction);
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister1Postion].setReadyToFree(false);
			}

			String sourcePhysicalRegister2 = cpuContext.getRenameTable().get(instruction.getSource2());
			if(sourcePhysicalRegister2 != null) {
				instruction.setPhyscialSource2(sourcePhysicalRegister2);

				int sourcePhysicalRegister2Postion = CPUUtils.getPhysicalRegisterPosition(sourcePhysicalRegister2);
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Postion].getLastReferredBy().add(0, instruction);
				cpuContext.getPhysicalRegisters()[sourcePhysicalRegister2Postion].setReadyToFree(false);
			}

			int destinationPhysicalRegisterPosition = CPUUtils.getFreePRF(cpuContext);
			instruction.setPhysicalDestination(cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].getName());
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(false);
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].getLastReferredBy().add(0, instruction);
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setReadyToFree(false);
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setAllocated(true);

			String oldPhysicalRegister = cpuContext.getRenameTable().get(instruction.getDestination());
			if(oldPhysicalRegister != null)
			{
				int oldPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(oldPhysicalRegister);
				if(cpuContext.getPhysicalRegisters()[oldPhysicalRegisterPosition].isReadyToFree()) {
					cpuContext.getPhysicalRegisters()[oldPhysicalRegisterPosition].setAllocated(false);
				}

			}
			cpuContext.getRenameTable().put(instruction.getDestination(), cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].getName());

			this.instruction.formPhysicalRegisterInstruction();

		}

		System.out.println("Decode RF Stage: "+instruction.getInstructionNumber()+" "+instruction.getInstructionString());



		/*if(this.getInstruction().getInstruction().equalsIgnoreCase("HALT")) {
			halt();
		}*/

		if(this.getInstruction().getInstruction().equalsIgnoreCase("BZ") || this.getInstruction().getInstruction().equalsIgnoreCase("BNZ")) {

			this.getInstruction().setLastArithmeticInstruction(this.lastArithemeticInstruction);
			this.instruction.formPhysicalRegisterInstruction();
		}

		if(!nonArithemeticInstruction.contains(this.instruction.getInstruction())) {
			this.lastArithemeticInstruction= this.instruction;
			this.lastArithmeticInstrictionFetchSequence = this.instruction.getFetchSequence();
		}

	}

	private void fetchForwardedValuesIntoDRFStage() {

		List<String> skipList = new ArrayList<String>();
		skipList.add("HALT");
		//		skipList.add("BZ");
		//		skipList.add("BNZ");


		if(this.getInstruction()==null || this.getInstruction().getInstruction()==null || skipList.contains(this.getInstruction().getInstruction()) ) {
			return;
		}

		if(this.getInstruction().getInstruction().equalsIgnoreCase("STORE") || this.getInstruction().getInstruction().equalsIgnoreCase("JUMP")) {
			String register = this.getInstruction().getDestination();
			Integer value = CPUUtils.getForwardedValue(cpuContext, register);
			if(forwardedMap.get(register)==null)
				forwardedMap.put(register, value);

		}
		else if(this.getInstruction().getInstruction().equalsIgnoreCase("BZ") || this.getInstruction().getInstruction().equalsIgnoreCase("BNZ")) {
			String register = this.getLastArithemeticInstruction().getDestination();
			Integer value = CPUUtils.getForwardedValue(cpuContext, register);
			if(forwardedMap.get(register)==null)
				forwardedMap.put(register, value);

		}
		else {
			String register1 = this.getInstruction().getSource1();
			Integer value = CPUUtils.getForwardedValue(cpuContext, register1);
			if(forwardedMap.get(register1)==null)
				forwardedMap.put(register1, value);

			String register2 = this.getInstruction().getSource2();
			Integer value2 = CPUUtils.getForwardedValue(cpuContext, register2);
			if(forwardedMap.get(register2)==null)
				forwardedMap.put(register2, value2);

		}
	}


	/*public void halt() {

		getCpuContext().setProgramCounter(-1);

	}*/


	public boolean isInstructionStalled() {
		//Instruction instr = this.instruction;
		if(cpuContext.getReorderBuffer().getReorderBufferQueue().size()>=32) {
			return true;
		}
		else if(CPUUtils.getFreePRF(cpuContext) == -1){
			return true;
		}
		else if(cpuContext.getIssueQueue().getInstructionQueue().size() >=16) {
			return true;
		}
		else if(CPUUtils.isBranchedInstruction(this.instruction) && cpuContext.getBisMap().size()>=8)
			return true;
		return false;
	}


	public boolean isInstructionStalled(int currentExecutionCycle) { // due to registers

		this.fetchForwardedValuesIntoDRFStage();

		if (this.getInstruction()==null)
		{

			return false;
		}

		if(this.getInstruction().getInstruction().equalsIgnoreCase("STORE") || this.getInstruction().getInstruction().equalsIgnoreCase("JUMP")) {

			if(CPUUtils.isSourceLocked(this.cpuContext, this.getInstruction().getDestination(), currentExecutionCycle ) ||
					CPUUtils.isSourceLocked(this.cpuContext, this.getInstruction().getSource1(), currentExecutionCycle )) {

				return true;

			}
			else 
				return false;
		}

		if(CPUUtils.isSourceLocked(this.cpuContext, this.getInstruction().getSource1(), currentExecutionCycle )
				|| CPUUtils.isSourceLocked(this.cpuContext, this.getInstruction().getSource2(),currentExecutionCycle )
				|| CPUUtils.isDestinationLocked(this.cpuContext, this.getInstruction().getDestination(), currentExecutionCycle)) 
		{

			return true;		
		}

		if(this.getInstruction().getInstruction().equalsIgnoreCase("BZ") || this.getInstruction().getInstruction().equalsIgnoreCase("BNZ")) {
			if(! ( (getCpuContext().getZeroFlagSetBy() == this.lastArithemeticInstruction) && (getCpuContext().getZeroFlagSetByFetchSequence()==lastArithmeticInstrictionFetchSequence) ) 
					&& getForwardedZeroFlag()==null) {
				return true;
			}
		}
		return false;

	}
	public boolean isInstructionStalledAtNextStage() { // due to stalling at MUL/EXE stage.
		if (this.getInstruction()==null)
		{

			return false;
		}


		if(!this.getInstruction().getInstruction().equalsIgnoreCase("DIV") && !this.getInstruction().getInstruction().equalsIgnoreCase("MUL")
				&& this.getInstruction() != cpuContext.getExecution().getInstruction()) {
			return true;
		}
		else if(this.getInstruction().getInstruction().equalsIgnoreCase("MUL") && this.getInstruction() != cpuContext.getMultiplicationALU().getInstruction()) {
			return true;
		}
		else if(this.getInstruction().getInstruction().equalsIgnoreCase("HALT") && (cpuContext.getMultiplicationALU2().getInstruction()!=null || cpuContext.getExecution().getInstruction()!=null)) {
			return true;
		}

		return false;

	}

	private Boolean getForwardedZeroFlag() {
		if(lastArithemeticInstruction ==null)
			return null;
		if(lastArithemeticInstruction.equals(cpuContext.getExecution().getForwardingRegisterInstruction())) {
			return cpuContext.getExecution().getForwardingRegisterValue() ==0;
		} 
		if(lastArithemeticInstruction.equals(cpuContext.getMultiplicationALU2().getForwardingRegisterInstruction())) {
			return cpuContext.getMultiplicationALU2().getForwardingRegisterValue() ==0;
		}
		if(lastArithemeticInstruction.equals(cpuContext.getDivAlu4().getForwardingRegisterInstruction())) {
			return cpuContext.getDivAlu4().getForwardingRegisterValue() ==0;
		}
		Integer forwardedValue= this.getForwardedMap().get(lastArithemeticInstruction.getDestination());
		if(forwardedValue != null) {
			return forwardedValue == 0;
		}
		return null;

	}

	public Map<String, Integer> getForwardedMap() {
		return forwardedMap;
	}

	public void setForwardedMap(Map<String, Integer> forwardedMap) {
		this.forwardedMap = forwardedMap;
	}

}
