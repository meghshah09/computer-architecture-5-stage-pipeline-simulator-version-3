package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ReorderBuffers {

	
	private List<Instruction> reorderBufferQueue = new ArrayList<Instruction>();

	public List<Instruction> getReorderBufferQueue() {
		return reorderBufferQueue;
	}

	public void setReorderBufferQueue(List<Instruction> reorderBufferQueue) {
		this.reorderBufferQueue = reorderBufferQueue;

	}


	
	private Map <Instruction,Boolean> reorderBufferMap = new HashMap<Instruction,Boolean>();
	
	public Map <Instruction,Boolean> getReorderBufferMap() {
		return reorderBufferMap;
	}

	public void setReorderBufferMap(Map <Instruction,Boolean> reorderBufferMap) {
		this.reorderBufferMap = reorderBufferMap;
	}
	
	public void display() {
		if(reorderBufferQueue.size() == 0) {
		System.out.println("ROB: Empty");
		}
		else {
			System.out.println("ROB:");
		}
		for(Instruction instruction: reorderBufferQueue) {
	//		System.out.println("TEST"+instructionQueue.size());
			System.out.println("\t"+ instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction()+" \t"+ reorderBufferMap.get(instruction));

		}
	}


	/*    
private int pcAddress;
private int arAddress; // architectural register address
private int value;
private boolean statusBit; // indicating result is valid or not
private String instrType;

    public int getPcAddress() {
        return pcAddress;
    }

    public void setPcAddress(int pcAddress) {
        this.pcAddress = pcAddress;
    }

    public int getArAddress() {
        return arAddress;
    }

    public void setArAddress(int arAddress) {
        this.arAddress = arAddress;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isStatusBit() {
        return statusBit;
    }

    public void setStatusBit(boolean statusBit) {
        this.statusBit = statusBit;
    }

    public String getInstrType() {
        return instrType;
    }

    public void setInstrType(String instrType) {
        this.instrType = instrType;
    }
*/
}
