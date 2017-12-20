package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LSQ {
  
	private List<Instruction> LSQList = new ArrayList<Instruction>();
	private Map<Instruction, Integer> LSQMap = new HashMap<Instruction,Integer>();
	
	public List<Instruction> getLSQList() {
		return LSQList;
	}
	public void setLSQList(List<Instruction> lSQList) {
		LSQList = lSQList;
	}
	public Map<Instruction, Integer> getLSQMap() {
		return LSQMap;
	}
	public void setLSQMap(Map<Instruction, Integer> lSQMap) {
		LSQMap = lSQMap;
	}

	public void display() {
		if(LSQList.size() == 0) {
		System.out.println("LSQ: Empty");
		}
		else {
			System.out.println("LSQ:");
		}
		for(Instruction instruction: LSQList) {
	//		System.out.println("TEST"+instructionQueue.size());
			System.out.println("\t"+ instruction.getInstructionNumber()+" "+ instruction.getPhysicalRegisterInstruction()+" \t"+ LSQMap.get(instruction));

		}
	}

}
 /*   private boolean statusBit; // LSQ index is allocated or free
    private String instrType; // LOAD or STORE
    private boolean memAddrValid; // bit indicating if memory address is valid or not
    private int memAddress;
    private String destRegister;
    private String source1;
    private boolean dataReadyBit; // indicating if data is ready or not
    private int valueToBeStored;

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

    public boolean isMemAddrValid() {
        return memAddrValid;
    }

    public void setMemAddrValid(boolean memAddrValid) {
        this.memAddrValid = memAddrValid;
    }

    public int getMemAddress() {
        return memAddress;
    }

    public void setMemAddress(int memAddress) {
        this.memAddress = memAddress;
    }

    public String getDestRegister() {
        return destRegister;
    }

    public void setDestRegister(String destRegister) {
        this.destRegister = destRegister;
    }

    public String getSource1() {
        return source1;
    }

    public void setSource1(String source1) {
        this.source1 = source1;
    }

    public boolean isDataReadyBit() {
        return dataReadyBit;
    }

    public void setDataReadyBit(boolean dataReadyBit) {
        this.dataReadyBit = dataReadyBit;
    }

    public int getValueToBeStored() {
        return valueToBeStored;
    }

    public void setValueToBeStored(int valueToBeStored) {
        this.valueToBeStored = valueToBeStored;
    }
    
    
}
*/