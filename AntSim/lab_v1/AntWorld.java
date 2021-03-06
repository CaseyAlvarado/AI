package lab_v1;

import java.util.ArrayList;
import java.util.Random;

//import processing.core.*; 

import processing.core.PApplet;

// @formatter:off

/*
 * ANT WORLD
 * 
 * The cells that form this world are arranged in a grid.
 *  
 * Each cell has a state. One cell can be defined as the 'start' and one or more cells can be labeled as goals, and these goals might be dynamic. 
 * 
 * The idea is to find good (perhaps not optimal) paths between start and goal, as defined by a performance measure. 
 * 
 * All paths are constrained by the world edges - there is no wrap-around.
 * 
 * Obstacles may block direct paths; obstacles might appear, disappear or move.
 * 
 * The world is the grid of cells together with obstacles, and the start and goal (or goal) cells.
 * 
 * Paths might be discovered using ants, in which case we think of the start as the nest, and the goal as food.
 * 
 * Paths might also be found by A* or any other algorithm. In this case, the ants could represent the open list (the fringe).
 * 
 * This version of ANT WORLD keeps track of the best path found so far, where the performance measure is the number of steps from start to goal.
 * 
 * draw() is called every 1/frameRate seconds. draw() in turn calls updateAnts(). 
 * 
 * updateAnts() iterates through the ant colony, updating each ant and monitoring best path found so far.
 * 
 * Four different algorithms are implemented, randomMove, moveTowardsFood, moveTowardsFood2 and global best.
 */

 // @formatter:on

public class AntWorld extends PApplet {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// drawing, animation
	int worldWidth = 512;
	int worldHeight = worldWidth;
	
	int backgroundColor;
	boolean pause;

	// cells
	int[][] cells;
	int numRows, numCols;
	float cellWidth, cellHeight;

	// single random object for all pseudorandom numbers
	Random random;

	// the ants
	int numAnts;
	ArrayList<Ant> ants;

	// best path, and path length - a performance measure
	ArrayList<RowCol> bestPath;
	int bestPathLength;

	// food and nest - start and goal
	RowCol food, nest;

	// termination
	int maxIts, its;

	// cell state
	final static int UNDEFINED = -1;
	final static int BACKGROUND = 0;
	final static int FOOD = 1;
	final static int NEST = 2;
	final static int OBSTACLE = 3;
	final static int BEST = 4;
	
	//which algorithm to run 
	int alg = 0; 
	ArrayList<Integer> steps = new ArrayList<Integer>();
	BaselinePlotting bp = new BaselinePlotting(); 
	boolean terminate2; 
	int foodIndexY = 0; 
	int runs = 0; 
	
	public void settings(){ 
		
		size(worldWidth, worldHeight);
		pause = true;
		
	}

	public void setup() {

		println("settingup"); 
		//worldWidth = worldHeight = 512;
		random = new Random();

		// cells
		numRows = numCols = 16;
		cellWidth = cellHeight = worldWidth / numCols;
		cells = new int[numRows][numCols];

		// add nest
		nest = new RowCol(numRows / 2, 2);
		cells[nest.row][nest.col] = NEST;

		// add food
//		numRows / 2 - 5
		food = new RowCol(numCols/2, numCols - 3);
		cells[food.row][food.col] = FOOD;

		// ants
		ants = new ArrayList<Ant>();
		numAnts = 2;
		for (int i = 0; i < numAnts; i++)
			ants.add(new Ant(nest.row, nest.col));

		// termination
		maxIts = 100;
		its = 0;

		// best path
		resetBestPath();

		// now set up animation
		//size(worldWidth, worldHeight);
		
		backgroundColor = color(255);
		frameRate(10);
		terminate2 = false; 
	}

	public void draw() {
		background(backgroundColor);

		drawGrid();
		drawAnts();
		drawCells();
		

		if (!pause && !terminate()) {
			int numExtraIts = 1;
			for (int i = 0; i < numExtraIts; i++)
				updateAnts();
		}
	}

	// @formatter:off
	
	/*
	 * ALGORITHM RANDOM SEARCH(world) 
	 * Returns an estimation of the optimal path from start to goal, avoiding obstacles, and remaining within confines
	 * 
	 * set best path length to infinity
	 * 
	 * for each ant 
	 *    place on nest cell 
	 *    put location of nest cell in memory
	 * 
	 * loop until termination condition is met 
	 *    
	 *    for each ant
	 * 
	 *       if ant has found food 
	 *          return ant to nest 
	 *          erase memory
	 * 
	 *       else 
	 *          make a list of available cells i.e. neighbours that are not outside the grid, and are not an obstacle
	 * 
	 *       if list is not empty 
	 *          pick uniformly at random a cell from the list  
	 *          move the ant to this cell 
	 *          add this cell to the ant's memory of visited cells
	 * 
	 *       if ant has found food and the number of visited cells stored in the memory is equal or less than the best path length 
	 *          set best path length to number of visited cells 
	 *          copy ant memory to best path
	 * 
	 * END RANDOM SEARCH
	 * 
	 * 
	 * RANDOM SEARCH can be re-written with a procedure:
	 * 
	 * ALGORITHM RANDOM SEARCH(world) 
	 * Returns an estimation of the optimal path from start to goal, avoiding obstacles, and remaining within confines
	 * 
	 * BEGIN	
	 * 
	 * set best path length to infinity
	 * 
	 * for each ant 
	 *    place on nest cell 
	 *    put location of nest cell in memory
	 * 
	 * loop until termination condition is met 
	 * 
	 *    for each ant
	 * 
	 *       RANDOM MOVE(ant, world)
	 * 
	 *       if ant has found food and the number of visited cells stored in the memory is equal or less than the best path length 
	 *          set best path length to number of visited cells 
	 *          copy ant memory to best path
	 * 
	 * END RANDOM SEARCH
	 * 
	 * RANDOM MOVE(ant, world) 
	 * Moves an ant at random in the world, if that is possible, and returns the ant to the nest when it reaches food. 
	 * The ant has a memory of every cell visited since leaving the nest. 
	 * 
	 * BEGIN
	 * 
	 * if ant has found food 
	 *    return ant to nest 
	 *    erase memory
	 * 
	 * else 
	 *    make a list of available cells i.e. neighbours that are not outside the grid, and are not an obstacle
	 * 
	 *    if list is not empty 
	 *       pick uniformly at random a cell from the list  
	 *       move the ant to this cell 
	 *       add this cell to the ant's memory of visited cells
	 * 
	 * END RANDOM MOVE
	 */
	
	// @formatter:on
	
	void updateAnts() {
	
			for (int i = 0; i < ants.size(); i++) {
	
				Ant ant = ants.get(i);
				
				switch(alg){ 
					case 0: ant.randomMove(this);
					break; 
					case 1: ant.moveTowardsFood(this);
					break; 
					case 2: ant.moveTowardsFood2(this);
					break; 
					case 3: ant.globalBest(this);
					break; 
					default: ant.globalBest(this);
					break; 
				}
								
				if (getCellState(ant.row, ant.col) == FOOD) {
	
					println("Ant " + i + " has found the food in "
							+ (ant.memory.size() - 1) + " steps"); // number of
																	// steps is one
																	// less than
																	// number of
																	// cells visited
					println(ant.memory);
	
					if (ant.memory.size() <= bestPathLength)
						updateBestPath(ant);
	
					//println("Best path length " + (bestPathLength - 1));
					//println(bestPath);
					//println("\n");
					
					steps.add(ant.memory.size() -1); 	
					terminate2 = true; 
				}
			}
	
			its++;
			println("iteration: " + its);
	}

	boolean terminate() {
		if (terminate2){ 
			println("finally terminates"); 
			System.out.println("algorithm counter" + alg);
			println("run number before increase:" + runs); 
			bp.record(this); 
			//bp.changePositionOfFoodAndAlgorithm(this); 
			//setup(); 
			return true;
		}

//		if (its >= maxIts){ 
//			bp.record(this); 
//			return true;
//		}
		else
			return false;
	}

	void resetBestPath() {

		if (bestPath != null) {

			// reset cells on old best path to background
			for (int j = 0; j < bestPath.size(); j++) {

				RowCol rc = bestPath.get(j);
				if (!rc.equals(nest) && !rc.equals(food)
						&& cells[rc.row][rc.col] != OBSTACLE)
					cells[rc.row][rc.col] = BACKGROUND;
			}
		}
		bestPath = new ArrayList<RowCol>();
		bestPathLength = Integer.MAX_VALUE;
	}

	void updateBestPath(Ant ant) {

		// check that best path is still valid
		// ie ants don't have outdated memory
		for (int j = 0; j < ant.memory.size(); j++) {
			RowCol rc = ant.memory.get(j);
			if (cells[rc.row][rc.col] == OBSTACLE) {
				return;
			}
		}

		resetBestPath();

		// update best path and best value
		bestPath = ant.memory;
		bestPathLength = bestPath.size();

		// set sells on new best path to BEST
		for (int j = 0; j < bestPath.size(); j++) {
			RowCol rc = bestPath.get(j);
			if (!rc.equals(nest) && !rc.equals(food))
				cells[rc.row][rc.col] = BEST;
		}
	}

	void drawAnts() {

		noStroke();

		for (int i = 0; i < ants.size(); i++) {

			Ant ant = ants.get(i);

			if (inGrid(ant)) {

				fill(antColor(ant.state));
				rectMode(CORNER);
				rect(ant.col * cellWidth, ant.row * cellHeight, cellWidth,
						cellHeight);
			}
		}
	}

	int antColor(int state) {

		if (state == BACKGROUND)
			return color(128, 0, 0);

		else
			return color(255);
	}

	// cells with state BACKGROUND are not drawn - they have the background
	// colour
	void drawCells() {

		noStroke();
		rectMode(CORNER);

		for (int col = 0; col < numCols; col++) {
			for (int row = 0; row < numRows; row++) {

				if (cells[row][col] != BACKGROUND) {

					fill(cellColor(cells[row][col]));
					rect(col * cellWidth, row * cellHeight, cellWidth,
							cellHeight);
				}
			}
		}
	}

	int cellColor(int state) {

		if (state == NEST)
			return color(0, 128, 0);
		else if (state == FOOD)
			return color(0, 0, 128);
		else if (state == OBSTACLE)
			return color(0);
		else if (state == BEST)
			return color(255, 0, 0, 64);
		else
			return backgroundColor;
	}

	void drawGrid() {

		stroke(128);

		for (int col = 0; col <= numCols; col++) {
			line(col * cellWidth, 0, col * cellWidth, worldHeight);
		}

		for (int row = 0; row <= numRows; row++) {
			line(0, row * cellHeight, worldWidth, row * cellHeight);
		}
	}

	boolean inGrid(Ant ant) {

		if (inGrid(ant.row, ant.col))
			return true;
		else
			return false;
	}

	boolean inGrid(int row, int col) {

		if (row > -1 && row < numRows && col > -1 && col < numCols)
			return true;
		else
			return false;
	}

	int getCellState(int row, int col) {

		if (inGrid(row, col))
			return cells[row][col];

		else
			return UNDEFINED;
	}

	void setCellState(int row, int col, int state) {

		if (inGrid(row, col))
			cells[row][col] = state;
	}

	public void keyPressed() {

		if (key == ' ')
			pause = !pause;
	}

	public void mouseClicked() {

		int row = (int) (mouseY / cellWidth);
		int col = (int) (mouseX / cellHeight);

		resetBestPath();
		setCellState(row, col, BACKGROUND);
	}

	public void mouseDragged() {

		int row = (int) (mouseY / cellWidth);
		int col = (int) (mouseX / cellHeight);

		resetBestPath();
		setCellState(row, col, OBSTACLE);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { "lab_v1.AntWorld" });
	}
}
