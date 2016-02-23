package lab_v1;

public class RowCol {
	//RowCol - data structure 

  int row, col;

  RowCol(int r, int c) {

    row = r;
    col = c;
  }
  
  public String toString(){
    
    return "[" + row + ", " + col + "]";
  }
  
  public boolean equals(RowCol rc){
	  //this calls equals with another rc on current rc
    
    return row == rc.row && col == rc.col;
  }
}