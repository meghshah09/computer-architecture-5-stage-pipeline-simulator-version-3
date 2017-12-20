package common;

public class RegisterBufferWrapper {
		
		private int value;
		private Instruction setByInstruction;

		
		public RegisterBufferWrapper(int value, Instruction setByInstruction) {
			super();
			this.value = value;
			this.setByInstruction = setByInstruction;
		}
		public Instruction getSetByInstruction() {
			return setByInstruction;
		}
		public void setSetByInstruction(Instruction setByInstruction) {
			this.setByInstruction = setByInstruction;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
}
