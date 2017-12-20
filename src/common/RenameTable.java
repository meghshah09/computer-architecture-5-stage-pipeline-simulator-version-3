package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenameTable {

	private List<RenameTableEntry> renameTableEntry = new ArrayList<RenameTableEntry>();

	public List<RenameTableEntry> getRenameTableEntry() {
		return renameTableEntry;
	}

	public void setRenameTableEntry(List<RenameTableEntry> renameTableEntry) {
		this.renameTableEntry = renameTableEntry;
	} 
	
	
	/*
	private Map <String,String> renameTable = new HashMap<String,String>();

	*/
//	private String architectureRegister;
//	private String physicalRegister;
//	private boolean srcBit;
//	private Register[] architectureRegisterList = new Register[16];
//	
////	
//////	public RenameTable(String architectureRegister,String physicalRegister, boolean srcBit) {
//////		super();
////////		this.architectureRegister = architectureRegister;
//////		for (int i=0;i<16;i++) 
//////		{
//////			this.architectureRegisterList[i]= new Register("R"+i);
//////		}
//////		
//////	}
////	
////	public RenameTable(String architectureRegister, String physicalRegister, boolean srcBit) {
////		super();
////		this.architectureRegister = architectureRegister;
////		this.physicalRegister = physicalRegister;
////		this.srcBit = srcBit;
////	}
////
////	
////	public void setRenameTable(String architectureRegister,String physicalRegister, boolean srcBit) {
////		List<RenameTable> renameTableList = new ArrayList<RenameTable>();
////		RenameTable renameTable = new RenameTable (architectureRegister,physicalRegister,srcBit);
////		renameTableList.add(renameTable);
////	}
////	
////	
//////	RenameTable renameStack = new RenameTable(architectureRegister,physicalRegister,srcBit); 
////
//	
//	public Register[] getRenameTable() {
//	
//		
//		return null;
//	
//	}
//	
//	
//	public String getPhysicalRegister() {
//		return physicalRegister;
//	}
//	public void setPhysicalRegister(String physicalRegister) {
//		this.physicalRegister = physicalRegister;
//	}
//	public String getArchitectureRegister() {
//		return architectureRegister;
//	}
//	public void setArchitectureRegister(String architectureRegister) {
//		this.architectureRegister = architectureRegister;
//	}
//	public boolean isSrcBit() {
//		return srcBit;
//	}
//	public void setSrcBit(boolean srcBit) {
//		this.srcBit = srcBit;
//	}
//	public Register[] getArchitectureRegisterList() {
//		return architectureRegisterList;
//	}
//	public void setArchitectureRegisterList(Register[] architectureRegisterList) {
//		this.architectureRegisterList = architectureRegisterList;
//	}
	

}
