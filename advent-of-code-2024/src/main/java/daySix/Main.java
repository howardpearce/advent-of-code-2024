package daySix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  static Map<Point, Character> map = new HashMap<>();
  static List<Point> visitedSpots = new ArrayList<>();
  static Guard guard;
  static char[][] grid;

  public record Point(int x, int y) {
    @Override
    public String toString() {
      return String.format("(%d, %d)", x, y);
    }
  }

  // maybe this could be a record
  public static class Guard {
    public Point position;
    public Direction direction;
    public Guard(Point position, Direction direction) {
      this.position = position;
      this.direction = direction;
    }

    public void move() throws ArrayIndexOutOfBoundsException{
      Point next = new Point(position.x + direction.xDir, position.y + direction.yDir);
      if (!map.containsKey(next)) {
        throw new ArrayIndexOutOfBoundsException("Guard is out of bounds at point: " + next.toString());
      }
      if (map.get(next) == '#') {
        // turn right
        switch (direction) {
          case UP:
            direction = Direction.RIGHT;
            break;
          case DOWN:
            direction = Direction.LEFT;
            break;
          case LEFT:
            direction = Direction.UP;
            break;
          case RIGHT:
            direction = Direction.DOWN;
            break;
        }
      } else {
        visitedSpots.add(next);
        position = next;
      }
    }
  }

  // Represents the current direction the guard is facing
  public enum Direction {
    UP('^', 0, -1), DOWN('v', 0, 1), LEFT('<', -1, 0), RIGHT('>',1,0);
    public final char character;
    public final int xDir;
    public final int yDir;
    private Direction(char label, int xDir, int yDir) {
      this.character = label;
      this.xDir = xDir;
      this.yDir = yDir;
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(daySix.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/daySix.txt");

    // for visualizing the grid
    grid = new char[input.size()][input.get(0).length()];

    for(int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        if (line.charAt(x) == '^') {
          guard = new Guard(new Point(x, y), Direction.UP);
        }
        map.put(new Point(x, y), line.charAt(x));
      }
    }

    logger.info("Guard position: {}", map.get(new Point(4, 6)));
    logger.info("Part one: {}", partOne());
  }

  public static int partOne() {
    try {
      while (true) {
        guard.move();
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      logger.debug(e.toString());
    }

    for (Point p : map.keySet()) {
      grid[p.y][p.x] = map.get(p);
    }

    for(Point p : visitedSpots) {
      grid[p.y][p.x] = 'X';
    }

    // print the grid
    for(int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        System.out.print(grid[i][j]);
      }
      System.out.print("\n");
    }

    return visitedSpots.stream().distinct().toList().size();
  }

  public static int partTwo() {
    return 0;
  }

}
