package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class StateBackup {
	
	PRF[] physicalRegisters = new PRF[32];
	private List<Instruction> reorderBufferQueue = new LinkedList<Instruction>();
	private Map <String,String> renameTable = new HashMap<String,String>();
	private List<Instruction> LSQList = new ArrayList<Instruction>();
	private List<Instruction> pendingInstructionsList = new ArrayList<Instruction>();
	private final List<Instruction> instructionQueue = new ArrayList<Instruction>();
	private List<Instruction> pendingInstructionQueue = new ArrayList<Instruction>();
	private final List<Instruction> copyInstructionQueue = new ArrayList<Instruction>();
	
	public List<Instruction> getPendingInstructionQueue() {
		return pendingInstructionQueue;
	}

	public void setPendingInstructionQueue(List<Instruction> pendingInstructionQueue) {
		this.pendingInstructionQueue = pendingInstructionQueue;
	}

	CPUContext cpuContext;
	public StateBackup(CPUContext cpuContext) throws CloneNotSupportedException {
		this.cpuContext = cpuContext;
		copyPhysicalRegisters();
		copyRenameTable();
		copyROB();
		copyLSQ();
		copyIssueQueue();
	}
	
	private void copyPhysicalRegisters() throws CloneNotSupportedException {
		PRF[] physicalRegistersOrg = cpuContext.getPhysicalRegisters();
		for (int i=0;i<32;i++) {
			physicalRegisters[i]=(PRF)physicalRegistersOrg[i].clone();
		}
	}
	
	/**
	 * 
	 */
	private void copyRenameTable(){
		Map <String,String> renameTableOrg = cpuContext.getRenameTable();
		for(String key: renameTableOrg.keySet()){
			this.renameTable.put(key, renameTableOrg.get(key));
		}
	}
	
	public PRF[] getPhysicalRegisters() {
		return physicalRegisters;
	}

	public void setPhysicalRegisters(PRF[] physicalRegisters) {
		this.physicalRegisters = physicalRegisters;
	}

	public List<Instruction> getReorderBufferQueue() {
		return reorderBufferQueue;
	}

	public void setReorderBufferQueue(List<Instruction> reorderBufferQueue) {
		this.reorderBufferQueue = reorderBufferQueue;
	}

	public Map<String, String> getRenameTable() {
		return renameTable;
	}

	public void setRenameTable(Map<String, String> renameTable) {
		this.renameTable = renameTable;
	}

	public List<Instruction> getLSQList() {
		return LSQList;
	}

	public void setLSQList(List<Instruction> lSQList) {
		LSQList = lSQList;
	}

	public List<Instruction> getPendingInstructionsList() {
		return pendingInstructionsList;
	}

	public void setPendingInstructionsList(List<Instruction> pendingInstructionsList) {
		this.pendingInstructionsList = pendingInstructionsList;
	}

	public CPUContext getCpuContext() {
		return cpuContext;
	}

	public void setCpuContext(CPUContext cpuContext) {
		this.cpuContext = cpuContext;
	}

	public List<Instruction> getInstructionQueue() {
		return instructionQueue;
	}

	private void copyROB(){
		List<Instruction> reorderBufferQueueOrg = cpuContext.getReorderBuffer().getReorderBufferQueue();
		for (Instruction instr:reorderBufferQueueOrg){
			this.reorderBufferQueue.add(instr);
		}
	}
	
	private void copyLSQ(){
		List<Instruction>LSQListOrg = cpuContext.getLSQ().getLSQList();
		for (Instruction instr:LSQListOrg){
			this.LSQList.add(instr);
		}
	}
	
	private void copyIssueQueue(){
		List<Instruction>instructionQueueOrg = cpuContext.getIssueQueue().getInstructionQueue();
		for (Instruction instr:instructionQueueOrg){
			this.instructionQueue.add(instr);
		}
	}
	
	private void addPendingInstructions() {
		pendingInstructionsList.add(cpuContext.getDivAlu4().getInstruction());
		pendingInstructionsList.add(cpuContext.getDivAlu3().getInstruction());
		pendingInstructionsList.add(cpuContext.getDivAlu2().getInstruction());
		pendingInstructionsList.add(cpuContext.getDivAlu1().getInstruction());
		pendingInstructionsList.add(cpuContext.getMultiplicationALU2().getInstruction());
		pendingInstructionsList.add(cpuContext.getMultiplicationALU().getInstruction());
		pendingInstructionsList.add(cpuContext.getExecution().getInstruction());
		for (Instruction instr:instructionQueue){
			pendingInstructionsList.add(instr);
		}
	}
}
