package lab_v1;

import java.util.ArrayList;


public class Ant {
  
  int row, col;
  int state;

  final static double TWOPI = 2 * Math.PI;

  // directions relative to current cell
  final static int EAST = 0, NORTHEAST = 1, NORTH = 2, NORTHWEST = 3,
      WEST = 4, SOUTHWEST = 5, SOUTH = 6, SOUTHEAST = 7;

  // memory of visited cells
  ArrayList<RowCol> memory;

  Ant(int r, int c) {

    row = r;
    col = c;
    state = 0;
    memory = new ArrayList<RowCol>();
    memory.add(new RowCol(row, col));
  }

  // return to nest of current cell is food
  boolean returnToNest(AntWorld antWorld) {

    if (antWorld.food.row == row && antWorld.food.col == col) { // we're there

      memory = new ArrayList<RowCol>();

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

    int dirToFood = directionToFood(antWorld); //1

    ArrayList<Integer> nearestDirs = new ArrayList<Integer>();
    int nearestIndex = (dirToFood + 4) % 8; //5

    for (int i = 0; i < available.size(); i++) {

      int dir = available.get(i);

      int diff1 = clock(8, dir - dirToFood); //0
      int diff2 = clock(8, dirToFood - dir); //2
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
}