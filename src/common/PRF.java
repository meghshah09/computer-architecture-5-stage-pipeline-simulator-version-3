package common;

import java.util.ArrayList;
import java.util.List;

public class PRF implements Cloneable{
	
	private List<Instruction> lastReferredBy = new ArrayList<Instruction>();
	
	
	public PRF(String name) {
		super();
		value = 0;
		this.name = name;
		isValid= true;
		status = false;
		allocated = false;
		renamed = false;
		zeroFlag=false;

	}
	private int value;
	private String name; 
	private boolean isValid;	
	private boolean status;
	private boolean zeroFlag;
	private boolean allocated;
	private boolean renamed;
//	private Instruction lastReferredBy;
	private boolean isReadyToFree=true;
	
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
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
	public boolean isAllocated() {
		return allocated;
	}
	public void setAllocated(boolean allocated) {
		this.allocated = allocated;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public boolean isRenamed() {
		return renamed;
	}
	public void setRenamed(boolean renamed) {
		this.renamed = renamed;
	}
	public boolean isZeroFlag() {
		return zeroFlag;
	}
	public void setZeroFlag(boolean zeroFlag) {
		this.zeroFlag = zeroFlag;
	}
/*	public Instruction getLastReferredBy() {
		return lastReferredBy;
	}
	public void setLastReferredBy(Instruction lastReferredBy) {
		this.lastReferredBy = lastReferredBy;
	}
*/	public boolean isReadyToFree() {
		return isReadyToFree;
	}
	public void setReadyToFree(boolean isReadyToFree) {
		this.isReadyToFree = isReadyToFree;
	}
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();
	}
	public List<Instruction> getLastReferredBy() {
		return lastReferredBy;
	}
	public void setLastReferredBy(List<Instruction> lastReferredBy) {
		this.lastReferredBy = lastReferredBy;
	}
}
