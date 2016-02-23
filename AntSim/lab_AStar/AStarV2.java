package lab_AStar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AStarV2 extends Ant{
	//adding open and closed list for A* algorithm 
	private ArrayList<Node> closedList = new ArrayList<Node>(); 
	private ArrayList<Node> open = new ArrayList<Node>(); 
	
	Random random = new Random();
	RowCol currentPosition; 
	AntWorld antWorld; 
	
	AStarV2(int r, int c, AntWorld AntWorld) {
		super(r, c);
		// TODO Auto-generated constructor stub
		currentPosition = new RowCol(row, col); 
		antWorld = AntWorld; 
	}
	
	void AStarGraphSearch(){ 
		Node currentNode = new Node(8, currentPosition);
		int parentgncount = currentNode.getParent().getGnCount(); 
		currentNode.setGNCount(parentgncount++); 
		open.add(currentNode); 
		
		if (open.isEmpty()){ return; }
		
		//calculating f for each Node in fringe
		Map<Node, Double> fringe = makeFringeWithFVals(antWorld); 
		
		//find the node with the lowest f 
		Node chosenNode = returnNodeWithSmallestF(fringe); 
		
		if(chosenNode != null){ 
			//remove from the fringe the node whose state has lowest f
			//will remove the first instance of the node, works like a stack 
			boolean removedLowestFringe = open.remove(chosenNode); 
			chosenNode.setParentNode(currentNode); 
			int currentNodeGnCount = currentNode.getGnCount(); 
			chosenNode.setGNCount(currentNodeGnCount++); 
			
			//if node state satisfies the goal test return the corresponding solution
			if (chosenNode.getPosition().equals(antWorld.food)){ 
				move(chosenNode.getNumber()); 
			}
			
			if (!closedList.contains(chosenNode)){ 
				closedList.add(chosenNode); 
				
				//expand the node and add resulting nodes to the fringe
				//use available squares to make open nodes 
				ArrayList<Integer> available = availableCells(antWorld);
				expandNodes(available); 
		}			
			
		}	
		
	}
	
//	// Find free cells - on grid, and not obstacle
//	ArrayList<RowCol> availablePositions(AntWorld antWorld) {
//		
//		ArrayList<RowCol> list = new ArrayList<RowCol>();
//		for (int dir = 0; dir < 8; dir++) {
//
//			move(dir);
//			if (antWorld.inGrid(row, col)
//					&& antWorld.getCellState(row, col) != AntWorld.OBSTACLE) {
//
//				list.add(dir);
//			}
//
//			undoMove(dir);
//		}
//
//		return list;
//	}
	
	// executes the move
	RowCol newPosition(int dir) {

		if (dir == Ant.EAST) {
			return new RowCol(currentPosition.row, currentPosition.col++);
		} else if (dir == Ant.NORTHEAST) {
			return new RowCol(currentPosition.row--, currentPosition.col++); 
		} else if (dir == Ant.NORTH) {
			return new RowCol(currentPosition.row--, currentPosition.col); 
		} else if (dir == Ant.NORTHWEST) {
			return new RowCol(currentPosition.row--, currentPosition.col--); 
		} else if (dir == Ant.WEST) {
			return new RowCol(currentPosition.row--, currentPosition.col); 
		} else if (dir == Ant.SOUTHWEST) {
			return new RowCol(currentPosition.row++, currentPosition.col--); 
		} else if (dir == Ant.SOUTH) {
			return new RowCol(currentPosition.row++, currentPosition.col);
		} else if (dir == Ant.SOUTHEAST) {
			return new RowCol(currentPosition.row++, currentPosition.col++);
		} else{ 
			return currentPosition; 
		}
	}
	
	Map<Node, Double> makeFringeWithFVals(AntWorld antworld){ 
		//should this make a new fringe everytime and choose from there, recalculating f every time 
		//or should fringe be kept up there
		//does f change for each node? 
		
		Map<Node, Double> fringe = new HashMap<Node, Double>();
		
		for (Node n : open){ 
			int parentg = n.getParent().getGnCount(); 
			int childg = parentg++; 
			
			double f; 
			if(n.equals(antworld.food)){ 
				f = childg + 0.0; 
			}
			else { 
				f = childg + calculateHeuristicN(n);
			}
			
			fringe.put(n, f); 			
		}
		return fringe; 
	}
	
	Node returnNodeWithSmallestF(Map<Node, Double> map){ 
		Double lowestf = Collections.min(map.values());
		for(Node n: map.keySet()){ 
			if(map.get(n).equals(lowestf)){ 
				return n; 
			}
		}
		return null; 		
	}
	
	double guessAboutHn(RowCol position, int antworldnumofcols, int antworldnumofrows){
		
		//this is making an educated guess about the heuristic of node n, H(n'). 
		//The heuristic is the cost of getting from node n to the food. 
		
		int columndistance = antworldnumofcols - position.col; 
		int rowdistance = antworldnumofrows - position.row; 
		
		return Math.sqrt(Math.pow(columndistance, 2.0) + Math.pow(rowdistance, 2.0)); 
	}
	
	double calculateHeuristicN(Node n){ 
		//h(n) <= h(n') + d(n', n) 

		//finding h(n)
		double HNGuess = guessAboutHn(currentPosition, antWorld.numCols, antWorld.numRows); 
		
		//NOT SURE ABOUT THIS, pull h(n') from next available node?
		ArrayList<Integer> available = availableCells(antWorld); 
		int indexOfPositionOfNPrime = available.get(0);
		
		Node nPrime = findNode(indexOfPositionOfNPrime); 
		
		double HNPrimeGuess = guessAboutHn(nPrime.getPosition(), antWorld.numCols, antWorld.numRows); 
		
		//now calculate d(n', n')
		double dofNandNPrime = calculateDistanceOfNandNPrime(currentPosition, nPrime.getPosition()); 
		
		if (HNGuess <= (HNPrimeGuess +  dofNandNPrime)){ 
			return HNGuess; 			
		}
		else{ 
			double randomVal = HNPrimeGuess + (dofNandNPrime - HNPrimeGuess)*random.nextDouble(); 
			System.out.println(randomVal); 
			System.out.println(HNGuess - randomVal); 
			return (HNGuess - randomVal);  
		}			
	}
	
	double calculateDistanceOfNandNPrime(RowCol n, RowCol nPrime){ 
		int coldiff = n.col - nPrime.col; 
		int rowdiff = n.row -nPrime.row; 
		
		return Math.sqrt(Math.pow(coldiff, 2.0) + Math.pow(rowdiff, 2.0)); 
	}
	
	void expandNodes(ArrayList<Integer> available){ 
		for(int d: available){ 
			 Node n = new Node(d, newPosition(d)); 
			 open.add(n); 
		}
	}
	
	Node findNode(int number){
		for(Node n: open){ 
			if(n.getNumber() == number){ 
				return n; 
			}
		}
		return null; 		
	}

}
