package testAStar;

public class RowCol {

	int row, col;

	RowCol(int r, int c) {

		row = r;
		col = c;
	}
	
	public String toString(){
		
		return "[" + row + ", " + col + "]";
	}
	
	public boolean equals(RowCol rc){
		
		return row == rc.row && col == rc.col;
	}
}

