package common;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import stages.*;


public class CPUContext {

	private Register[] registers = new Register[16];
	private PRF[] physicalRegisters = new PRF[32];
	
	private Map <String,String> renameTable = new HashMap<String,String>();
	

	private Map <Instruction,Integer> InstructionValueMap = new HashMap<Instruction,Integer>();
	public Map <Instruction,Integer> getInstructionValueMap() {
		return InstructionValueMap;
	}

	public void setInstructionValueMap(Map <Instruction,Integer> instructionValueMap) {
		InstructionValueMap = instructionValueMap;
	}
	
	private Map <Instruction,Integer> bisMap= new HashMap<Instruction,Integer>(); 
	public boolean addToBisMap(Instruction instr) {
		if(bisMap.size()==8)
			return false;
		bisMap.put(instr, bisIndexCounter);
		bisIndexCounter++;
		return true;
	}
	private int bisIndexCounter=0;
	
	
	
	
	public int getBisIndexCounter() {
		return bisIndexCounter;
	}

	public void setBisIndexCounter(int bisIndexCounter) {
		this.bisIndexCounter = bisIndexCounter;
	}

	private Map <Instruction,StateBackup> stateBackupMap = new HashMap<Instruction,StateBackup>();
	
	public Map<Instruction, StateBackup> getStateBackupMap() {
		return stateBackupMap;
	}

	public void setStateBackupMap(Map<Instruction, StateBackup> stateBackupMap) {
		this.stateBackupMap = stateBackupMap;
	}

	private ReorderBuffers reorderBuffer;
	
	public ReorderBuffers getReorderBuffer() {
		return reorderBuffer;
	}

	public void setReorderBuffer(ReorderBuffers reorderBuffer) {
		this.reorderBuffer = reorderBuffer;
	}

	public Map<String, String> getRenameTable() {
		return renameTable;
	}

	public void setRenameTable(Map<String, String> renameTable) {
		this.renameTable = renameTable;
	}

	private issueQueue issueQueue;

	public issueQueue getIssueQueue() {
		return issueQueue;
	}

	public void setIssueQueue(issueQueue issueQueue) {
		this.issueQueue = issueQueue;
	}

	private Map <Integer, Integer> memoryMap = new HashMap<Integer,Integer>();
	private int programCounter=4000;
	private boolean zeroFlag= false ;
	private Instruction zeroFlagSetBy= null;
	private int zeroFlagSetByFetchSequence;
	private final Fetch fetch ;
	private final DecodeRegisterFiles decoderRegisterFiles;
	private final Execution execution;
	private final Mem memory;
	private final WriteBack writeBack;
	private final MultiplicationALU multiplicationALU;
	private final MultiplicationALU2 multiplicationALU2;
	private final DivALU1 divAlu1;
	private final DivALU2 divAlu2;
	private final DivALU3 divAlu3;
	private final DivALU4 divAlu4;
	private final ARFStage ARFStage;
	private final LSQ LSQ;
	public MultiplicationALU2 getMultiplicationALU2() {
		return multiplicationALU2;
	}
	
	public void init() {
		for (int i=0;i<16;i++) 
		{
			registers[i].setValue(0);
			registers[i].setValid(true);
			registers[i].setLockingInstruction(null);
			registers[i].setLockedInCycle(0);
			
		}
		
		this.programCounter=4000;
		zeroFlag=false;
		zeroFlagSetBy= null;
		memoryMap.keySet().removeAll(memoryMap.keySet());
		this.getFetch().setEndOfProgram(false);
		this.getFetch().setBranched(false);
		this.getDecoderRegisterFiles().setLastArithemeticInstruction(null);
		Map<Instruction, Integer> executionBufferMap = this.getExecution().getBufferMap();
		executionBufferMap.keySet().removeAll(executionBufferMap.keySet());
		Map<Instruction, Integer> multiplicationALU1BufferMap = this.getMultiplicationALU().getMultiplicationALUBufferMap();
		multiplicationALU1BufferMap.keySet().removeAll(multiplicationALU1BufferMap.keySet());
/*		Map<Instruction, Integer> memoryBufferMap = this.getMemory().getBufferMemoryMap();
		memoryBufferMap.keySet().removeAll(memoryBufferMap.keySet());
*/		
		
	}

	public CPUContext(String filePath) throws FileNotFoundException {
		
		for (int i=0;i<16;i++) 
		{
			registers[i]=new Register("R"+i);
		}

			for (int i=0;i<32;i++) 
		{
			physicalRegisters[i]=new PRF("P"+i);
		}
		fetch  = new Fetch(this);
		decoderRegisterFiles =new DecodeRegisterFiles(this);
		execution =new Execution(this);
		memory = new Mem(this);
		writeBack =new WriteBack(this);
		multiplicationALU = new MultiplicationALU(this);
		multiplicationALU2 = new MultiplicationALU2(this);
		divAlu1 = new DivALU1(this);
		divAlu2 = new DivALU2(this);
		divAlu3 = new DivALU3(this);
		divAlu4 = new DivALU4(this);
		reorderBuffer = new ReorderBuffers();
		issueQueue = new issueQueue(this);
		ARFStage = new ARFStage(this);
		LSQ = new LSQ();
		loadInstructions(filePath);


	}



	// Map with key as the memory location and value as the Instruction.
	private Map<Integer,Instruction> instructionMap = new HashMap<Integer,Instruction>(); 

	public MultiplicationALU getMultiplicationALU() {
		return multiplicationALU;
	}


	
	private void loadInstructions(String filePath) throws FileNotFoundException {
		int pcValue=4000, instructionCount=0;
		File file = new File(filePath);
		Scanner scan = new Scanner(file);
		String str,instructionNumber;
		while (scan.hasNextLine())
		{
			instructionNumber="(I"+instructionCount+")";
			str=scan.nextLine();
			Instruction instruction = new Instruction(instructionNumber,str, pcValue);
			instructionMap.put(pcValue,instruction);
			pcValue+=4;
			instructionCount++;
		}
	
	}


	public Map<Integer, Instruction> getInstructionMap() {
		return instructionMap;
	}


	public void setInstructionMap(Map<Integer, Instruction> instructionMap) {
		this.instructionMap = instructionMap;
	}


	public Register[] getRegisters() {
		return registers;
	}

	public void setRegisters(Register[] registers) {
		this.registers = registers;
	}

	public PRF[] getPhysicalRegisters() {
		return physicalRegisters;
	}

	public void setPhysicalRegisters(PRF[] physicalRegisters) {
		this.physicalRegisters = physicalRegisters;
	}

	public Fetch getFetch() {
		return fetch;
	}
	public DecodeRegisterFiles getDecoderRegisterFiles() {
		return decoderRegisterFiles;
	}
	public Execution getExecution() {
		return execution;
	}
	public Mem getMemory() {
		return memory;
	}
	public WriteBack getWriteBack() {
		return writeBack;
	}

	public int getProgramCounter() {
		return programCounter;
	}
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}


	public boolean isZeroFlag() {
		return zeroFlag;
	}


	public void setZeroFlag(boolean zeroFlag) {
		this.zeroFlag = zeroFlag;
	}


	public Instruction getZeroFlagSetBy() {
		return zeroFlagSetBy;
	}


	public void setZeroFlagSetBy(Instruction zeroFlagSetBy) {
		this.zeroFlagSetBy = zeroFlagSetBy;
	}


	public Map <Integer, Integer> getMemoryMap() {
		return memoryMap;
	}

	public DivALU1 getDivAlu1() {
		return divAlu1;
	}

	public DivALU2 getDivAlu2() {
		return divAlu2;
	}

	public DivALU3 getDivAlu3() {
		return divAlu3;
	}

	public DivALU4 getDivAlu4() {
		return divAlu4;
	}

	public int getZeroFlagSetByFetchSequence() {
		return zeroFlagSetByFetchSequence;
	}

	public void setZeroFlagSetByFetchSequence(int zeroFlagSetByFetchSequence) {
		this.zeroFlagSetByFetchSequence = zeroFlagSetByFetchSequence;
	}
//
//	public Map <Register, PRF> getRenameTable() {
//		return renameTable;
//	}
//
//	public void setRenameTable(Map <Register, PRF> renameTable) {
//		this.renameTable = renameTable;
//	}

	public ARFStage getARFStage() {
		return ARFStage;
	}

	public LSQ getLSQ() {
		return LSQ;
	}

	public Map <Instruction,Integer> getBisMap() {
		return bisMap;
	}

	public void setBisMap(Map <Instruction,Integer> bisMap) {
		this.bisMap = bisMap;
	}


}
