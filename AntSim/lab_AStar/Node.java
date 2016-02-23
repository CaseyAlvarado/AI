package lab_AStar;

public class Node {
	private String Name; 
	private Node Parent; 
	private int GNcount;
	private RowCol Position; 
	private int Number; 
	
	public Node(int number, RowCol position){
		// TODO Auto-generated constructor stub	
		Number = number;
		Position = position; 	 
	}

	public void setParentNode(Node parent){
		Parent = parent; 	
	}
	
	public void displayNodeName(){ 
		System.out.println("Node Name: " + Name); 	
	}
	
	public void displayParentNode(){ 
		System.out.println("Parent Name: " + Parent.Name); 
	}
	
	public int getGnCount(){ 
		return GNcount; 
	}
	
	public Node getParent(){
		return Parent; 
	} 

	public int getNumber(){ 
		return Number; 
	}
	
	public RowCol getPosition(){
		return Position; 
	}
	
	public void setGNCount(int gncount){ 
		GNcount = gncount;
	}

}
