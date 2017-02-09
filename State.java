public class State {
	int up; 
	int upT;
	int lDiag;
	int lDiagT;
	int rDiag;
	int rDiagT;
	int left;
	int leftT;
	public State (int up, int upT, int lDiag, int lDiagT, int rDiag, int rDiagT, int left, int leftT) {
		this.up = up;
		this.lDiag = lDiag;
		this.rDiag = rDiag;
		this.left = left;
		this.upT = upT;
		this.lDiagT = lDiagT;
		this.rDiagT = rDiagT;
		this.leftT = leftT;
		
	}
}