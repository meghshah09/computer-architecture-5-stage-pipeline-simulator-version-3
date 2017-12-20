package common;

import java.util.ArrayList;
import java.util.List;

public class Register {

	private int value;
	private String name; 
	private boolean isValid;
	private int lockedInCycle;
	private Instruction lockingInstruction;
	private boolean srcBit; // It will tell, whether value to be taken is from register or PRF.


	public Register(String name) { // Constructor for normal call.

		this.name = name;
		this.value = 0;
		this.isValid = true;
		this.srcBit = false; // false, for register and TRUE for PRF.
	
	}
	

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public int getLockedInCycle() {
		return lockedInCycle;
	}

	public void setLockedInCycle(int lockedInCycle) {
		this.lockedInCycle = lockedInCycle;
	}
	
	public Instruction getLockingInstruction() {
		return lockingInstruction;
	}

	public void setLockingInstruction(Instruction lockingInstruction) {
		this.lockingInstruction = lockingInstruction;
	}

	public boolean getSrcBit() {
		return srcBit;
	}

	public void setSrcBit(boolean srcBit) {
		this.srcBit = srcBit;
	}

	
}
