import java.util.ArrayList;
import java.util.Random;

// drawing, animation
int worldWidth, worldHeight;
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

public void setup() {

  // animation
  size(512, 512);
  pause = true;
  backgroundColor = color(255);
  frameRate(10);

  // pseudorandom number generator
  // random = new Random(1066); // with seed
  random = new Random();

  // the world of cells
  worldWidth = width; 
  worldHeight = height; // the world
  numRows = numCols = 16;
  cellWidth = cellHeight = worldWidth / numCols;
  cells = new int[numRows][numCols];

  // add nest
  nest = new RowCol(numRows / 2, 2);
  cells[nest.row][nest.col] = NEST;

  // add food
  food = new RowCol(numRows / 2, numCols - 3);
  cells[food.row][food.col] = FOOD;

  // ants
  ants = new ArrayList<Ant>();
  numAnts = 5;
  for (int i = 0; i < numAnts; i++)
    ants.add(new Ant(nest.row, nest.col));

  // termination
  maxIts = 10000;
  its = 0;

  // best path
  resetBestPath();
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

    // ant.randomMove(this);
    // ant.moveTowardsFood(this);
    // ant.moveTowardsFood2(this);
    ant.globalBest(this);

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

      println("Best path length " + (bestPathLength - 1));
      println(bestPath);
      println("\n");
    }
  }

  its++;
  println("iteration: " + its);
}

// Specify termination condition
boolean terminate() {

  if (its >= maxIts)
    return true;
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

  // set cells on new best path to BEST
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