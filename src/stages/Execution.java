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

public class Execution extends Stage {

	public Execution(CPUContext cpuContext) {
		super(cpuContext);
	}

	private String forwardingRegister="";
	private int forwardingRegisterValue=0;
	private Instruction forwardingRegisterInstruction= null;
	// to store temporary result of execution stage.

	private Map<Instruction,Integer> bufferMap = new HashMap<Instruction,Integer>(); 


	public Map<Instruction, Integer> getBufferMap() {
		return bufferMap;
	}


	public void setBufferMap(Map<Instruction, Integer> bufferMap) {
		this.bufferMap = bufferMap;
	}

	public void setForwardingValues() {
		Instruction currentInstruction = this.getInstruction();

		if(currentInstruction==null || currentInstruction.getInstruction().equalsIgnoreCase("HALT") 
				|| currentInstruction.getInstruction().equalsIgnoreCase("STORE") 
				|| currentInstruction.getInstruction().equalsIgnoreCase("JUMP") 
				|| currentInstruction.getInstruction().equalsIgnoreCase("LOAD"))
		{
			return;

		}

		forwardingRegister= currentInstruction.getDestination();
		forwardingRegisterValue= this.bufferMap.get(currentInstruction);
		forwardingRegisterInstruction = currentInstruction;
	}


	private Map<String,Integer> physicalForwardingBufferMap = new HashMap<String,Integer>(); 


	public void postExecution() {
		
		List<String> instructionWithoutDestination = new ArrayList<String>();
		instructionWithoutDestination.add("STORE");
		instructionWithoutDestination.add("HALT");
		instructionWithoutDestination.add("JUMP");
		instructionWithoutDestination.add("BZ");
		instructionWithoutDestination.add("BNZ");

		
		if(this.instruction== null)
			return;

		if(instruction.getInstruction().equalsIgnoreCase("STORE")) {
			cpuContext.getLSQ().getLSQMap().put(instruction, bufferMap.get(this.instruction));
		}		
		else if(instruction.getInstruction().equalsIgnoreCase("LOAD")) {
			cpuContext.getLSQ().getLSQMap().put(instruction, bufferMap.get(this.instruction));
			int instructionIndex = cpuContext.getLSQ().getLSQList().indexOf(instruction); 
			// CHeck for forwarding
			if(instructionIndex >0) {
				Instruction previousInstruction = cpuContext.getLSQ().getLSQList().get(instructionIndex-1);
				if(previousInstruction.getInstruction().equalsIgnoreCase("STORE") 
						&& cpuContext.getLSQ().getLSQMap().get(previousInstruction) !=null
						&& cpuContext.getLSQ().getLSQMap().get(instruction).equals(cpuContext.getLSQ().getLSQMap().get(previousInstruction))) {

					int previousDestinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(previousInstruction.getPhysicalDestination());
					int forwardedValue = cpuContext.getPhysicalRegisters()[previousDestinationPhysicalRegisterPosition].getValue();

					int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(this.instruction.getPhysicalDestination());
					cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValue(forwardedValue);
					cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(true);
					cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
					cpuContext.getLSQ().getLSQList().remove(instruction);
					return;
				}
			}
			/*BYPASS SCENARIO*/
			boolean isBypass=true;
			int bypassPosition = instructionIndex;
			for (int index=instructionIndex-1;index>=0;index--) {
				Instruction instr = cpuContext.getLSQ().getLSQList().get(index);
				if(instr.getInstruction().equalsIgnoreCase("STORE")) {
					if(cpuContext.getLSQ().getLSQMap().get(instr)== null) {
						
						isBypass=false;
						break;
						
					}
					else if(cpuContext.getLSQ().getLSQMap().get(instruction).equals(cpuContext.getLSQ().getLSQMap().get(instr))) {
						
						isBypass=false;
						break;
						
					}
				}
				else {
					break;
				}
				bypassPosition--;
			}

			if(isBypass && bypassPosition >=0) {
				cpuContext.getLSQ().getLSQList().remove(instruction);
				cpuContext.getLSQ().getLSQList().add(bypassPosition,instruction);
			}
		}
		
		else if(this.getInstruction().getInstruction().equalsIgnoreCase("JUMP")) {
			cpuContext.setProgramCounter(bufferMap.get(instruction));
			cpuContext.getFetch().setBranched(true);
			CPUUtils.flushInstructionForBranching(cpuContext, this.instruction);
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
		}
		
		else if(this.getInstruction().getInstruction().equalsIgnoreCase("BZ")) {
			if(this.getBufferMap().get(instruction.getLastArithmeticInstruction())==0) {
				cpuContext.setProgramCounter(bufferMap.get(instruction));
				cpuContext.getFetch().setBranched(true);
				CPUUtils.flushInstructionForBranching(cpuContext, this.instruction);

			}
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
		}
		else if( this.getInstruction().getInstruction().equalsIgnoreCase("BNZ")) {
			if(this.getBufferMap().get(instruction.getLastArithmeticInstruction())!=null && this.getBufferMap().get(instruction.getLastArithmeticInstruction())!=0) {
				cpuContext.setProgramCounter(bufferMap.get(instruction));
				cpuContext.getFetch().setBranched(true);
				CPUUtils.flushInstructionForBranching(cpuContext, this.instruction);
			}
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
		}
		
		else if(this.getInstruction().getInstruction().equalsIgnoreCase("JAL")) {
			cpuContext.setProgramCounter(bufferMap.get(instruction));
			cpuContext.getFetch().setBranched(true);
			CPUUtils.flushInstructionForBranching(cpuContext, this.instruction);
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
			
			int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(this.instruction.getPhysicalDestination());
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValue(this.getInstruction().getInstructionAddress()+4);
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(true);
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
			
		}

		else {
			int destinationPhysicalRegisterPosition = CPUUtils.getPhysicalRegisterPosition(this.instruction.getPhysicalDestination());
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValue(bufferMap.get(this.instruction));
			cpuContext.getPhysicalRegisters()[destinationPhysicalRegisterPosition].setValid(true);
			cpuContext.getReorderBuffer().getReorderBufferMap().put(instruction,true);
			if( !instructionWithoutDestination.contains(instruction.getInstruction())) {
				cpuContext.getInstructionValueMap().put(instruction, this.bufferMap.get(instruction));
				
			}
			//		physicalForwardingBufferMap.put(this.getInstruction().getPhysicalDestination(), bufferMap.get(this.instruction));

		}
	}

	@Override
	public void execute(int currentExecutionCycle) {

		this.currentExecutionCycle= currentExecutionCycle;
		List<Instruction> issueQueueLocal = cpuContext.getIssueQueue().getInstructionQueue();
		this.instruction= null;
		for(Instruction instr: issueQueueLocal) {

			if(instr!=null && ( instr.getInstruction().equalsIgnoreCase("MUL") || instr.getInstruction().equalsIgnoreCase("DIV") 
					||instr.getInstruction().equalsIgnoreCase("HALT"))) 
			{
				this.setInstruction(null);
			}
			else if(CPUUtils.isInstructionStalled(instr, cpuContext)) {
				this.setInstruction(null);
			}
			else if (( instr.getInstruction().equalsIgnoreCase("BZ") ||  instr.getInstruction().equalsIgnoreCase("BNZ")) && this.getBufferMap().get(instr.getLastArithmeticInstruction())==null)  {
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
			System.out.println("IntFU Stage: Empty");
			return;
		}

		issueQueueLocal.remove(this.instruction);

		System.out.println("IntFU Stage: "+instruction.getInstructionNumber()+" " + instruction.getPhysicalRegisterInstruction());


 		switch(this.getInstruction().getInstruction()) {

		case "MOVC": movc();
		break;
		// Dont need to implement, proffesor withdraws this requirement.
		//		case "ADDC": movc();
		//		break;
		//		case "MOV": mov();
		//		break;
		case "ADD": add();
		break;
		case "SUB": substract();
		break;
		case "LOAD": load();
		break;
		case "STORE": store();
		break;
		case "AND": and();
		break;
		case "OR": or();
		break;
		case "EXOR": exOR();
		break;
		case "BZ": 	bz();
		break;
		case "BNZ":		bnz();
		break;
		case "JUMP": jump();
		break;
		case "JAL":	jal();
		break;
		default:
			System.out.println("Invalid instruction");
			break;
		}

	}

	private void jal() {

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

		int newPCValue=operand1+operand2;
		this.getBufferMap().put(instruction, newPCValue);
		/*int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
*/
	}


	public void add() {
		//TODO FOR ALL ALU.

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
		int result= operand1 + operand2 ;

		bufferMap.put(this.getInstruction(), result);
		/*		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */	}

	public void substract() {


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


		int result= operand1 - operand2 ;

		bufferMap.put(this.getInstruction(), result);
		/*		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */	}

	public void movc() {

		int operand1;
		if(instruction.getPhyscialSource1() != null ) {
			operand1=CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource1(), instruction.getSource1());
		}
		else {
			operand1=CPUUtils.getOperandValue(this.cpuContext, this.getInstruction().getSource1());
		}
		bufferMap.put(instruction, operand1);

		//		
		//		int registerPosition = CPUUtils.getRegisterPosition(this.getInstruction().getSource1());
		//		if(registerPosition == -1) {
		//			bufferMap.put(this.getInstruction(),Integer.parseInt(this.getInstruction().getSource1()));
		//		}
		//		else
		//		{
		//
		//			bufferMap.put(this.getInstruction(),getCpuContext().getRegisters()[registerPosition].getValue());
		//
		//		}
		/*		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */	}

	public void and() {


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


		int result= operand1 & operand2;

		bufferMap.put(this.getInstruction(), result);
		/*		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */
	}

	public void or() {

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


		int result= operand1 | operand2;

		bufferMap.put(this.getInstruction(), result);
		/*		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */	}
	public void exOR() {

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


		int result= operand1 ^ operand2;
		bufferMap.put(this.getInstruction(), result);
		/*
		bufferMap.put(this.getInstruction(), result);
		int destionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getDestination());
		getCpuContext().getRegisters()[destionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[destionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */	}


	public void load() {

		int physicalRegisterValue_1 = CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource1(), instruction.getSource1()); //CPUUtils.getPhysicalRegisterPosition(this.getInstruction().getPhyscialSource1());
		int physicalRegisterValue_2 = CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource2(), instruction.getSource2());
		bufferMap.put(instruction, (physicalRegisterValue_1+physicalRegisterValue_2));
		/*int physicalRegisterPosition_1 = CPUUtils.getPhysicalRegisterPosition(this.getInstruction().getPhyscialSource1());
		int physicalRegisterPosition_2 = CPUUtils.getPhysicalRegisterPosition(this.getInstruction().getPhyscialSource2());

		if (physicalRegisterPosition_1 != -1 && physicalRegisterPosition_2 == -1) {
			bufferMap.put(this.getInstruction(), ((getCpuContext().getPhysicalRegisters()[physicalRegisterPosition_1].getValue())
					+ (Integer.parseInt(this.getInstruction().getSource2()))));

		} else if (physicalRegisterPosition_1 == -1 && physicalRegisterPosition_2 != -1) {
			bufferMap.put(this.getInstruction(), ((Integer.parseInt(this.getInstruction().getSource1()))
					+ (getCpuContext().getPhysicalRegisters()[physicalRegisterPosition_2].getValue())));

		}*/
		/*		int physicalDestionationRegisterPosition = CPUUtils.getRegisterPosition(this.getInstruction().getPhysicalDestination());
		getCpuContext().getRegisters()[physicalDestionationRegisterPosition].setValid(false);
		getCpuContext().getRegisters()[physicalDestionationRegisterPosition].setLockedInCycle(this.getCurrentExecutionCycle());
		getCpuContext().getRegisters()[physicalDestionationRegisterPosition].setLockingInstruction(this.getInstruction());
		 */	}

	public void store() {
		int physicalRegisterValue_1 = CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource1(), instruction.getSource1()); //CPUUtils.getPhysicalRegisterPosition(this.getInstruction().getPhyscialSource1());
		int physicalRegisterValue_2 = CPUUtils.getPhysicalRegisterOperandvalue(cpuContext, instruction.getPhyscialSource2(), instruction.getSource2());

		/*if (physicalRegisterPosition_1 != -1 && physicalRegisterPosition_2 == -1) {
			bufferMap.put(this.getInstruction(), ((getCpuContext().getPhysicalRegisters()[physicalRegisterPosition_1].getValue())
					+ (Integer.parseInt(this.getInstruction().getSource2()))));

		} else if (physicalRegisterPosition_1 == -1 && physicalRegisterPosition_2 != -1) {
			bufferMap.put(this.getInstruction(), ((Integer.parseInt(this.getInstruction().getSource1()))
					+ (getCpuContext().getPhysicalRegisters()[physicalRegisterPosition_2].getValue())));

		}*/
		bufferMap.put(instruction, (physicalRegisterValue_1+physicalRegisterValue_2));
	}

	public void jump() {

		int operandValue1= CPUUtils.getPhysicalRegisterOperandvalue(this.cpuContext, this.getInstruction().getPhysicalDestination(), this.getInstruction().getDestination()); // This is not destination, it is source 1 only. 
		int operandValue2= CPUUtils.getPhysicalRegisterOperandvalue(this.cpuContext, this.getInstruction().getPhyscialSource1(), this.getInstruction().getSource1()); // This is not source1, it is source 2.
		int newPCValue=operandValue1+operandValue2;
		this.getBufferMap().put(instruction, newPCValue);


	}
	public void bnz() {

		int bzLoop= CPUUtils.getOperandValue(this.cpuContext,this.getInstruction().getDestination());
		int newPCValue= instruction.getInstructionAddress() + bzLoop;
		this.getBufferMap().put(instruction, newPCValue);



	}
	public void bz() {


		int bzLoop= CPUUtils.getOperandValue(this.cpuContext,this.getInstruction().getDestination());
		int newPCValue= instruction.getInstructionAddress() + bzLoop;
		this.getBufferMap().put(instruction, newPCValue);

	}


	public String getForwardingRegister() {
		return forwardingRegister;
	}


	public void setForwardingRegister(String forwardingRegister) {
		this.forwardingRegister = forwardingRegister;
	}


	public int getForwardingRegisterValue() {
		return forwardingRegisterValue;
	}


	public void setForwardingRegisterValue(int forwardingRegisterValue) {
		this.forwardingRegisterValue = forwardingRegisterValue;
	}


	public Instruction getForwardingRegisterInstruction() {
		return forwardingRegisterInstruction;
	}


	public void setForwardingRegisterInstruction(Instruction forwardingRegisterInstruction) {
		this.forwardingRegisterInstruction = forwardingRegisterInstruction;
	}


	public Map<String,Integer> getPhysicalForwardingBufferMap() {
		return physicalForwardingBufferMap;
	}


	public void setPhysicalForwardingBufferMap(Map<String,Integer> physicalForwardingBufferMap) {
		this.physicalForwardingBufferMap = physicalForwardingBufferMap;
	}

}
