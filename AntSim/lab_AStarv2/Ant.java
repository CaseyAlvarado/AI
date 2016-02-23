package lab_AStarv2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Ant {
	
	int row, col;
	int state;

	final static double TWOPI = 2 * Math.PI;

	// directions relative to current cell
	final static int EAST = 0, NORTHEAST = 1, NORTH = 2, NORTHWEST = 3,
			WEST = 4, SOUTHWEST = 5, SOUTH = 6, SOUTHEAST = 7;

	// memory of visited cells
	ArrayList<RowCol> memory;
	
	//adding open and closed list for A* algorithm 
	private ArrayList<Node> closedList; 
	private ArrayList<Node> open;  

	Ant(int r, int c) {

		row = r;
		col = c;
		state = 0;
		memory = new ArrayList<RowCol>();
		closedList = new ArrayList<Node>(); 
		memory.add(new RowCol(row, col));
	}
	
	Random random = new Random();
	RowCol currentPosition;  
	Node currentNode; 
	
	// return to nest of current cell is food
	boolean returnToNest(AntWorld antWorld) {

		if (antWorld.food.row == row && antWorld.food.col == col) { // we're there

			memory = new ArrayList<RowCol>();
			closedList = new ArrayList<Node>(); 

			row = antWorld.nest.row;
			col = antWorld.nest.col;

			memory.add(new RowCol(row, col));
			return true;
		}

		else
			return false;
	}

	// ant walks aimlessly, choosing neighbour cells at random
	void randomMove(AntWorld antWorld) {

		if (returnToNest(antWorld))
			return;

		ArrayList<Integer> available = availableCells(antWorld);
		if (available.isEmpty())
			return;

		int index = antWorld.random.nextInt(available.size());
		int dir = available.get(index);
		move(dir);
		memory.add(new RowCol(row, col));
	}

	// will move towards food if the way ahead is clear with probability 0.5;
	// otherwise choose random direction
	void moveTowardsFood(AntWorld antWorld) {

		if (returnToNest(antWorld))
			return;

		ArrayList<Integer> available = availableCells(antWorld);
		if (available.isEmpty())
			return;

		int dirToFood = directionToFood(antWorld);

		if (available.contains(dirToFood) && antWorld.random.nextDouble() < 0.5)
			move(dirToFood);

		else {
			int index = antWorld.random.nextInt(available.size());
			int randomDir = available.get(index);
			move(randomDir);
		}

		memory.add(new RowCol(row, col));

	}

	// will move towards food if the way ahead is clear;
	// otherwise will chose the closest available with 50% probability,
	// random direction with 50% probability
	void moveTowardsFood2(AntWorld antWorld) {

		if (returnToNest(antWorld))
			return;

		ArrayList<Integer> available = availableCells(antWorld);
		if (available.isEmpty())
			return;

		int dirToFood = directionToFood(antWorld);

		ArrayList<Integer> nearestDirs = new ArrayList<Integer>();
		int nearestIndex = (dirToFood + 4) % 8;

		for (int i = 0; i < available.size(); i++) {

			int dir = available.get(i);

			int diff1 = clock(8, dir - dirToFood);
			int diff2 = clock(8, dirToFood - dir);
			int minDiff = Math.min(diff1, diff2);
			if (minDiff == clock(8, nearestIndex - dirToFood)) {
				nearestDirs.add(dir);
				nearestIndex = dir;
			} else if (minDiff < clock(8, nearestIndex - dirToFood)) {
				nearestDirs.clear();
				nearestDirs.add(dir);
				nearestIndex = dir;
			}
		}

		int index = antWorld.random.nextInt(nearestDirs.size());
		int dirToMove = nearestDirs.get(index);
		if (dirToMove == dirToFood)
			move(dirToFood);
		else if (antWorld.random.nextDouble() < 0.5)
			move(dirToMove);
		else {
			move(available.get(antWorld.random.nextInt(available.size())));
		}

		memory.add(new RowCol(row, col));
	}

	// will move towards food if the way ahead is clear with probability 0.5;
	// otherwise will move, if possible, to a cell on the best path with
	// probability 0.5,
	// otherwise, will choose random direction
	void globalBest(AntWorld antWorld) {

		if (returnToNest(antWorld))
			return;

		ArrayList<Integer> available = availableCells(antWorld);
		if (available.isEmpty())
			return;

		ArrayList<Integer> onOptimalPath = new ArrayList<Integer>();
		for (int d : available) {
			move(d);
			for (RowCol rc : antWorld.bestPath) {
				if (rc.row == row && rc.col == col) {
					onOptimalPath.add(d);
				}
			}

			undoMove(d);
		}

		int dirToFood = directionToFood(antWorld);

		double randomDouble = antWorld.random.nextDouble();
		if (available.contains(dirToFood) && randomDouble < 0.5)
			move(dirToFood);
		else if (!onOptimalPath.isEmpty() && randomDouble < 0.75) {
			move(onOptimalPath.get(antWorld.random.nextInt(onOptimalPath.size())));
		}

		else {
			int index = antWorld.random.nextInt(available.size());
			int randomDir = available.get(index);
			move(randomDir);
		}

		memory.add(new RowCol(row, col));

	}
	
	void AStarGraphSearch(AntWorld antWorld){ 
	
		if (returnToNest(antWorld))
			return;
		
		//make a new open list/fringe
		open = new ArrayList<Node>();
		
		//make a current Node with current position 
		currentPosition = new RowCol(row, col); 
		System.out.println("current position: " + currentPosition); 
		currentNode = new Node(8, currentPosition);
		
		//do not want to go back, so add current node to closed list
		closedList.add(currentNode); 
		memory.add(currentNode.getPosition()); 	
		
		//expand currentNode, add available nodes to fringe 
		ArrayList<Integer> available = availableCells_AStar(antWorld);
		if (available.isEmpty())
			return;
		
		addNodesToOpenList(available); 
		
		//calculate f for each Node in fringe
		Map<Node, Double> fringe = makeFringeWithFVals(antWorld); 
		printFringe(fringe); 
		
		//find the node with the lowest f value 
		Node chosenNode = findNodeWithSmallestF(fringe); 
			
		//choose the node with the smallest f, remove from the fringe
		//will remove the first instance of the node, works like a stack 
		boolean removedLowestFringe = open.remove(chosenNode); 

		//current node is the parent for chosen node			
		chosenNode.setParentNode(currentNode); 
		//set the g(n) count for the chosen Node to be the g(n) count of the parent +1
		int currentNodeGnCount = currentNode.getGnCount(); 
		chosenNode.setGNCount(++currentNodeGnCount); 
		
		//move to chosen node and add to memory of visited places
		move(chosenNode.getNumber()); 				
	}
	
	// Converts an integer (can be negative) to mod N
	public static int clock(int N, int n) {

		return ((Math.abs(n) / N + 1) * N + n) % N;
	}

	// Find free cells - on grid, and not obstacle
	ArrayList<Integer> availableCells(AntWorld antWorld) {

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int dir = 0; dir < 8; dir++) {
			move(dir);
			if (antWorld.inGrid(row, col)
					&& antWorld.getCellState(row, col) != AntWorld.OBSTACLE) {				
				list.add(dir);
			}

			undoMove(dir);
		}

		return list;
	}
	
	
	ArrayList<Integer> availableCells_AStar(AntWorld antWorld) {
		//this is a modified function of available cells. 
		//this version returns available cells that: 
		//1. are in the grid 
		//2. are not an obstacle
		//3. are not in the closed list
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int dir = 0; dir < 8; dir++) {
			move(dir);
			if (antWorld.inGrid(row, col)
					&& antWorld.getCellState(row, col) != AntWorld.OBSTACLE && !elementInClosed(new RowCol(row, col))) {
				list.add(dir);
			}

			undoMove(dir);
		}

		return list;
	}

	int directionToFood(AntWorld antWorld) {

		double x = antWorld.food.col - col;
		double y = -(antWorld.food.row - row);
		double theta = Math.atan2(y, x);

		if (theta < 0)
			theta += TWOPI;

		int dir = (int) Math.round(theta / (Math.PI / 4));
		dir %= 8;

		return dir;
	}
	
	void undoMove(int dir){
		
		move((dir + 4) % 8);
	}

	// executes the move
	void move(int dir) {

		if (dir == EAST) {
			east();
		} else if (dir == NORTHEAST) {
			north();
			east();
		} else if (dir == NORTH) {
			north();
		} else if (dir == NORTHWEST) {
			north();
			west();
		} else if (dir == WEST) {
			west();
		} else if (dir == SOUTHWEST) {
			south();
			west();
		} else if (dir == SOUTH) {
			south();
		} else if (dir == SOUTHEAST) {
			south();
			east();
		}
	}
	
	// optional methods if world edges wrap-around
	void northInTorus(AntWorld antWorld) {
		row = (row == 0 ? antWorld.numRows - 1 : row - 1);
	}

	void southInTorus(AntWorld antWorld) {
		row = (row == antWorld.numRows - 1 ? 0 : row + 1);
	}

	void eastInTorus(AntWorld antWorld) {
		col = (col == antWorld.numCols - 1 ? 0 : col + 1);
	}

	void westInTorus(AntWorld antWorld) {
		col = (col == 0 ? antWorld.numCols - 1 : col - 1);
	}

	// the basic movements
	void north() {
		row--;
	}

	void south() {
		row++;
	}

	void east() {
		col++;
	}

	void west() {
		col--;
	}

	public String toString() {

		return "posn = (" + row + ", " + col + ")";
	}
	
	RowCol newPosition(int dir) {
		
		//Essentially a lookup table to find the coordinates of the available positions
		//instead of using direction numbers
		int currentrow = currentPosition.row; 
		int currentcol = currentPosition.col; 

		if (dir == Ant.EAST) {
			return new RowCol(currentrow, ++currentcol);
		} else if (dir == Ant.NORTHEAST) {
			return new RowCol(--currentrow, ++currentcol); 
		} else if (dir == Ant.NORTH) {
			return new RowCol(--currentrow, currentcol); 
		} else if (dir == Ant.NORTHWEST) {
			return new RowCol(--currentrow, --currentcol); 
		} else if (dir == Ant.WEST) {
			return new RowCol(currentrow, --currentcol); 
		} else if (dir == Ant.SOUTHWEST) {
			return new RowCol(++currentrow, --currentcol); 
		} else if (dir == Ant.SOUTH) {
			return new RowCol(++currentrow, currentcol);
		} else if (dir == Ant.SOUTHEAST) {
			return new RowCol(++currentrow, ++currentcol);
		} 
		
		return null; 
	} 
	
	Map<Node, Double> makeFringeWithFVals(AntWorld antWorld){ 
		//a new fringe is made every time we evaluate in case something has changed. 
		//Although A Star is not receptive to sudden changes 
		Map<Node, Double> fringe = new HashMap<Node, Double>();
		
		//for each node calculate f(n) = g(n) + h(n) 
		for (Node n : open){ 
			//get g(n)
			int currentgn = currentNode.getGnCount();
			int newgn = currentgn++; 
			
			double f; 
			if(n.equals(antWorld.food)){ 
				//if this is the food, then we are at the goal and the cost from 
				//current node to food is 0. 
				f = newgn + 0.0; 
			}
			else { 
				//otherwise, if not at food, calculate h(n)
				f = newgn++ + calculateHeuristicN(n, antWorld);
			}
			
			fringe.put(n, f); 			
		}
		return fringe; 
	}
	
	Node findNodeWithSmallestF(Map<Node, Double> map){ 
		//find smallest f value
		Double lowestf; 
		lowestf = Collections.min(map.values());
		
		//find node that matches this f value 
		for(Node n: map.keySet()){ 
			if(map.get(n).equals(lowestf)){ 
				System.out.println("Node chosen:" + n.getPosition() + ",  f value:" + lowestf); 
				return n; 
			}
		}
		return null; 		
	}
	
	double calculateHeuristicN(Node n, AntWorld antWorld){ 	
		//calculate straight line distance from node n to food 
		int xdiff = n.getPosition().col - antWorld.food.col; 
		int ydiff = n.getPosition().row - antWorld.food.row; 		
		return Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)); 
	} 

	void addNodesToOpenList(ArrayList<Integer> available){ 
		//self explanatory 
		for(int d: available){ 
			 Node n = new Node(d, newPosition(d)); 
			 open.add(n); 
		}
	}
	
	void printOpenList(ArrayList<Node> al){ 
		System.out.println("printing open list"); 
		for(Node n : al){ 
			System.out.println("print number: " + n.getNumber()); 
			System.out.println("print position: " + n.getPosition()); 
		}
	}

	void printAvailable(ArrayList<Integer> al){ 
		System.out.println("printing available list"); 
		for(Integer i : al){ 
			System.out.println(i); 
		}
	}
	
	void printFringe(Map<Node, Double> map){ 
		System.out.println("Fringe options:"); 
		for(Node n: map.keySet()){ 
			System.out.println("Node Position:" + n.getPosition()+ "," + "F val:" + map.get(n)); 
		}
		
	}
	
	void printingClosedList(){ 
		System.out.println("printing closed list"); 
		for (Node n : closedList){ 
			System.out.println(n.getPosition()); 
		}
	}
	
	boolean elementInClosed(RowCol element){
		//asks if an element is in the closed list
		for(Node n : closedList){ 
			if(n.getPosition().equals(element)){ 
				return true; 
			}
		}
		return false; 
	}
}
