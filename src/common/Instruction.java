package common;

public class Instruction implements Cloneable{
	
	String instructionNumber;	
	String instruction;
	String source1;
	String physicalSource1;
	String source2;
	String physicalSource2;
	String destination;
	String physicalDestination;
	String instructionString;  
	int instructionAddress=0;
	Instruction lastArithmeticInstruction;
	int fetchSequence;
	String physicalRegisterInstruction;
	int bisMapping=-1;
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();
	}
	
	public int getBisMapping() {
		return bisMapping;
	}

	public void setBisMapping(int bisMapping) {
		this.bisMapping = bisMapping;
	}

	public String getPhysicalRegisterInstruction() {
		return physicalRegisterInstruction;
	}

	public void setPhysicalRegisterInstruction(String physicalRegisterInstruction) {
		this.physicalRegisterInstruction = physicalRegisterInstruction;
	}

	public void formPhysicalRegisterInstruction() {
		if(physicalSource2 != null || source2 !=null) {
			if(physicalSource2!=null) {
			physicalRegisterInstruction = instruction + " " + physicalDestination + " " + physicalSource1 + " " + physicalSource2;
		}
			else {
				physicalRegisterInstruction = instruction + " " + physicalDestination + " " + physicalSource1 + " " + source2;
			}
		}
		else if(physicalSource1 !=null || source1 !=null ) {
			if(physicalSource1!=null) {
			physicalRegisterInstruction = instruction + " " + physicalDestination + " " + physicalSource1;
			}
			else {
				physicalRegisterInstruction = instruction + " " + physicalDestination + " " + source1;
			}
		}
		else if(physicalDestination !=null || destination!=null) {
			if(physicalSource1!=null) {
			physicalRegisterInstruction = instruction + " " + physicalDestination;
			}
			else {
				physicalRegisterInstruction = instruction + " " + destination;
			}
		}
		else {
			physicalRegisterInstruction = instruction;
			
		}
		
		if(instruction.equalsIgnoreCase("BZ") || (instruction.equals("BNZ"))){
			physicalRegisterInstruction = instructionString;
		}
	
	}
	
	public String getPhysicalSource1() {
		return physicalSource1;
	}

	public void setPhysicalSource1(String physicalSource1) {
		this.physicalSource1 = physicalSource1;
	}

	public String getPhysicalSource2() {
		return physicalSource2;
	}

	public void setPhysicalSource2(String physicalSource2) {
		this.physicalSource2 = physicalSource2;
	}

	public String getPhyscialSource1() {
		return physicalSource1;
	}

	public void setPhyscialSource1(String physcialSource1) {
		this.physicalSource1 = physcialSource1;
	}

	public String getPhyscialSource2() {
		return physicalSource2;
	}

	public void setPhyscialSource2(String physcialSource2) {
		this.physicalSource2 = physcialSource2;
	}

	public String getPhysicalDestination() {
		return physicalDestination;
	}

	public void setPhysicalDestination(String physicalDestination) {
		this.physicalDestination = physicalDestination;
	}

	
	
	
	public Instruction(String instructionNumber, String instr, int instructionAddress)
	{	
		this.instructionNumber=instructionNumber;
		instructionString=instr;
		this.instructionAddress=instructionAddress;
	}

	public Instruction getLastArithmeticInstruction() {
		return lastArithmeticInstruction;
	}

	public void setLastArithmeticInstruction(Instruction lastArithmeticInstruction) {
		this.lastArithmeticInstruction = lastArithmeticInstruction;
	}
	
	public String getInstructionNumber() {
		return instructionNumber;
	}

	public void setInstructionNumber(String instructionNumber) {
		this.instructionNumber = instructionNumber;
	}

	
	public int getInstructionAddress() {
		return instructionAddress;
	}

	public void setInstructionAddress(int instructionAddress) {
		this.instructionAddress = instructionAddress;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getSource1() {
		return source1;
	}

	public void setSource1(String source1) {
		this.source1 = source1;
	}

	public String getSource2() {
		return source2;
	}

	public void setSource2(String source2) {
		this.source2 = source2;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getInstructionString() {
		return instructionString;
	}

	public void setInstructionString(String instructionString) {
		this.instructionString = instructionString;
	}
	
	public int getFetchSequence() {
		return fetchSequence;
	}

	public void setFetchSequence(int fetchSequence) {
		this.fetchSequence = fetchSequence;
	}
}
