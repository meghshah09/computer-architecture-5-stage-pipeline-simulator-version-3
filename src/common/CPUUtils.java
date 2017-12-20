package common;

import java.util.ArrayList;
import java.util.List;

public class CPUUtils {
	public static void flushInstructionForBranching(CPUContext cpuContext, Instruction instr) {

		StateBackup backup = cpuContext.getStateBackupMap().get(instr);
		cpuContext.getDecoderRegisterFiles().setInstruction(null);
		cpuContext.getFetch().setInstruction(null);

		Integer bisPointer = cpuContext.getBisMap().get(instr);
		// IQ SQUASHING
		List<Instruction> issueQueueDiscardList = new ArrayList<Instruction>();
		for(Instruction instruction: cpuContext.getIssueQueue().getInstructionQueue()) {
			if(instruction.getBisMapping()==bisPointer) {
				issueQueueDiscardList.add(instruction);
			}				
		}
		for(Instruction instruction: issueQueueDiscardList) {
			cpuContext.getIssueQueue().getInstructionQueue().remove(instruction);
		}
		// LSQ SQUASHING
		List<Instruction> lsqDiscardList = new ArrayList<Instruction>();
		for(Instruction instruction: cpuContext.getLSQ().getLSQList()) {
			if(instruction.getBisMapping()==bisPointer) {
				lsqDiscardList.add(instruction);
			}
		}
		for( Instruction instruction: lsqDiscardList) {
			cpuContext.getLSQ().getLSQList().remove(instruction);
			cpuContext.getLSQ().getLSQMap().remove(instruction);
		}

		//ROB Squashing
		List<Instruction> robDiscardList = new ArrayList<Instruction>();
		for(Instruction instruction: cpuContext.getReorderBuffer().getReorderBufferQueue()) {
			if(instruction.getBisMapping()>=bisPointer) {
				robDiscardList.add(instruction);
			}
		}
		for( Instruction instruction: robDiscardList) {
			if(instruction.getPhysicalDestination() != null) {
				int physicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(instruction.getPhysicalDestination());
				cpuContext.getPhysicalRegisters()[physicalRegisterPosition].setValid(true);
				cpuContext.getPhysicalRegisters()[physicalRegisterPosition].setAllocated(false);
			}
			if(instruction.getPhyscialSource1()!=null) {
				int physicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(instruction.getPhyscialSource1());
				cpuContext.getPhysicalRegisters()[physicalRegisterPosition].getLastReferredBy().remove(instruction);
				if(!cpuContext.getRenameTable().get(instruction.getSource1()).equals(instruction.getPhyscialSource1()) && !cpuContext.getReorderBuffer().getReorderBufferQueue().contains(cpuContext.getPhysicalRegisters()[physicalRegisterPosition].getLastReferredBy().get(0))) {
					cpuContext.getPhysicalRegisters()[physicalRegisterPosition].setValid(true);
					cpuContext.getPhysicalRegisters()[physicalRegisterPosition].setAllocated(false);

				}
			}

			if(instruction.getPhyscialSource2()!=null) {
				int physicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(instruction.getPhyscialSource1());
				cpuContext.getPhysicalRegisters()[physicalRegisterPosition].getLastReferredBy().remove(instruction);
				if(!cpuContext.getRenameTable().get(instruction.getSource1()).equals(instruction.getPhyscialSource1()) && !cpuContext.getReorderBuffer().getReorderBufferQueue().contains(cpuContext.getPhysicalRegisters()[physicalRegisterPosition].getLastReferredBy().get(0))) {
					cpuContext.getPhysicalRegisters()[physicalRegisterPosition].setValid(true);
					cpuContext.getPhysicalRegisters()[physicalRegisterPosition].setAllocated(false);

				}
			}
			
			
			
			cpuContext.getReorderBuffer().getReorderBufferQueue().remove(instruction);
			cpuContext.getReorderBuffer().getReorderBufferMap().remove(instruction);
		}


		// RenameTABLE SQUASHING
		cpuContext.setRenameTable(backup.getRenameTable());

	}

	public static int getLatestBisIndex(CPUContext cpuContext) {
		int maxBisIndex=-1;
		for(Instruction instr: cpuContext.getBisMap().keySet()) {
			int index = cpuContext.getBisMap().get(instr);
			if(index>maxBisIndex)
				maxBisIndex=index;
		}
		return maxBisIndex;
	}

	public static boolean isBranchedInstruction(Instruction instr) {

		List <String> branchedInstructions = new ArrayList<String>();
		branchedInstructions.add("JUMP");
		branchedInstructions.add("JAL");
		branchedInstructions.add("BZ");
		branchedInstructions.add("BNZ");

		if(instr!=null &&  branchedInstructions.contains(instr.getInstruction()))
			return true;
		else 
			return false;

	}
	public static int getRegisterPosition(String registerName) {

		List<String> registers = new ArrayList<String>();
		registers.add("R0");
		registers.add("R1");
		registers.add("R2");
		registers.add("R3");
		registers.add("R4");
		registers.add("R5");
		registers.add("R6");
		registers.add("R7");
		registers.add("R8");
		registers.add("R9");
		registers.add("R10");
		registers.add("R11");
		registers.add("R12");
		registers.add("R13");
		registers.add("R14");
		registers.add("R15");	

		registerName=registerName.trim();
		registerName= registerName.toUpperCase();


		return registers.indexOf(registerName); // if not found will return -1 ( Literal)

	}


	public static int getPhysicalRegisterPosition(String physicalRegisterName) {

		List<String> physicalRegisters = new ArrayList<String>();
		physicalRegisters.add("P0");
		physicalRegisters.add("P1");
		physicalRegisters.add("P2");
		physicalRegisters.add("P3");
		physicalRegisters.add("P4");
		physicalRegisters.add("P5");
		physicalRegisters.add("P6");
		physicalRegisters.add("P7");
		physicalRegisters.add("P8");
		physicalRegisters.add("P9");
		physicalRegisters.add("P10");
		physicalRegisters.add("P11");
		physicalRegisters.add("P12");
		physicalRegisters.add("P13");
		physicalRegisters.add("P14");
		physicalRegisters.add("P15");	
		physicalRegisters.add("P16");
		physicalRegisters.add("P17");
		physicalRegisters.add("P18");
		physicalRegisters.add("P19");
		physicalRegisters.add("P20");
		physicalRegisters.add("P21");
		physicalRegisters.add("P22");
		physicalRegisters.add("P23");
		physicalRegisters.add("P24");
		physicalRegisters.add("P25");
		physicalRegisters.add("P26");
		physicalRegisters.add("P27");
		physicalRegisters.add("P28");
		physicalRegisters.add("P29");
		physicalRegisters.add("P30");
		physicalRegisters.add("P31");

		if(physicalRegisterName == null)
			return -1;
		physicalRegisterName=physicalRegisterName.trim();
		physicalRegisterName= physicalRegisterName.toUpperCase();


		return physicalRegisters.indexOf(physicalRegisterName); // if not found will return -1 ( Literal)

	}

	public static boolean isInstructionStalled(Instruction instruction, CPUContext cpuContext) {
		//TODO Implementation for HALT, STORE , JUMP and branch instr.
		if(instruction.getPhyscialSource1() != null && isPhysicalSourceLocked(cpuContext, instruction.getPhyscialSource1())){
			return true;
		}
		else if(instruction.getInstruction().equalsIgnoreCase("JUMP") && instruction.getPhysicalDestination() != null 
				&& isPhysicalSourceLocked(cpuContext, instruction.getPhysicalDestination())) {
			return true;
		}
		else if(instruction.getPhyscialSource2() != null && isPhysicalSourceLocked(cpuContext, instruction.getPhyscialSource2())){
			return true;
		}
		return false;

	}



	/*getting free PRF*/
	public static int getFreePRF(CPUContext cpuContext){
		for(int i =0; i<32;i++){

			if(cpuContext.getPhysicalRegisters()[i].isAllocated() == false)
				return i;
		}
		return -1;
	}
	/*release PRF*/
	public static void releasePRF(CPUContext cpuContext, String source){

		int index = CPUUtils.getPhysicalRegisterPosition(source);

		cpuContext.getPhysicalRegisters()[index].setAllocated(false);
		return;
	}
	/*get value of physical register */
	public static int getPhysicalRegisterOperandvalue(CPUContext cpuContext, String source, String regVal){

		Integer operand;
		/*          operand = cpuContext.getExecution().getPhysicalForwardingBufferMap().get(source);
            if(operand != null)
            	return operand;            
		 */          int registerPosition = CPUUtils.getPhysicalRegisterPosition(source);

		 if (registerPosition ==-1){
			 operand = Integer.parseInt(regVal);
		 }
		 else{
			 operand = cpuContext.getPhysicalRegisters()[registerPosition].getValue();
		 }
		 return operand;
	}

	public static int getOperandValue(CPUContext cpuContext, String source) {
		int operand;
		int registerPosition = CPUUtils.getRegisterPosition(source);

		if(registerPosition== -1) {
			operand=Integer.parseInt(source);

		}
		else {
			Integer forwardedRegVal = cpuContext.getDecoderRegisterFiles().getForwardedMap().get(source);
			if(forwardedRegVal == null) {

				operand=cpuContext.getRegisters()[registerPosition].getValue();

			}
			else {

				operand=forwardedRegVal;

			}
		}
		return operand;

	}

	public static Integer getForwardedValue(CPUContext cpuContext, String register) {
		Integer value= null;
		if (register == null)
			return null;
		if(register.equalsIgnoreCase(cpuContext.getExecution().getForwardingRegister())) {
			value= cpuContext.getExecution().getForwardingRegisterValue();
		}
		else if(register.equalsIgnoreCase(cpuContext.getMultiplicationALU2().getForwardingRegister())) {
			value=cpuContext.getMultiplicationALU2().getForwardingRegisterValue();
		}
		else if(register.equalsIgnoreCase(cpuContext.getDivAlu4().getForwardingRegister())) {
			value= cpuContext.getDivAlu4().getForwardingRegisterValue();
		}
		return value;
	}


	public static boolean isPhysicalSourceLocked(CPUContext cpuContext, String source) {
		if(source==null || source.length()==0 ) {
			return false;
		}
		/*		Integer forwardedRegisterValue = cpuContext.getExecution().getPhysicalForwardingBufferMap().get(source);
		if (forwardedRegisterValue != null)
			return false;
		 */		
		int sourceRegisterPosition1 = CPUUtils.getPhysicalRegisterPosition(source);
		if(sourceRegisterPosition1 != -1 ) {
			PRF sourcePhysicalregister1 = cpuContext.getPhysicalRegisters()[sourceRegisterPosition1];
			if(!sourcePhysicalregister1.isValid()) {
				return true;
			}
		}
		return false;

	}


	public static boolean isSourceLocked(CPUContext cpuContext, String source, int currentExecutionCycle) {
		if(source==null || source.length()==0 ) {
			return false;
		}

		int sourceRegisterPosition1 = CPUUtils.getRegisterPosition(source);
		if(sourceRegisterPosition1 != -1 ) {
			Register register = cpuContext.getRegisters()[sourceRegisterPosition1];
			if(!register.isValid() && register.getLockedInCycle()!= currentExecutionCycle && cpuContext.getDecoderRegisterFiles().getForwardedMap().get(source)==null) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDestinationLocked(CPUContext cpuContext, String destination, int currentExecutionCycle) {
		if(destination==null || destination.length()==0 ) {
			return false;
		}

		int sourceRegisterPosition1 = CPUUtils.getRegisterPosition(destination);
		if(sourceRegisterPosition1 != -1 ) {
			Register register = cpuContext.getRegisters()[sourceRegisterPosition1];
			if(!register.isValid() && register.getLockedInCycle()!= currentExecutionCycle ) {
				return true;	

			}
		}
		return false;
	}

	/*private Boolean getForwardedZeroFlag(CPUContext cpuContext) {

		Instruction lastArithemeticInstruction = cpuContext.getDecoderRegisterFiles().getLastArithemeticInstruction();
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
		return null;
	}*/




}
