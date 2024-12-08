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

  // New exception type for when the guard is looping
  public static class LoopingException extends Exception {
    public LoopingException(String message) { super(message); }
  }

  public record Point(int x, int y) {}

  public record Move(Point position, Direction direction) {}

  // maybe this could be a record
  public static class Guard {
    public Point position;
    public Direction direction;
    public List<Point> visitedSpots = new ArrayList<>();
    public List<Move> moves = new ArrayList<>();
    public Map<Point, Character> map;
    boolean looping = false;

    public Guard(Point position, Direction direction, Map<Point, Character> map) {
      this.position = position;
      this.direction = direction;
      this.map = map;
    }

    public Guard(Guard guard) {
      this.position = new Point(guard.position.x, guard.position.y);
      this.direction = guard.direction;
      this.visitedSpots = new ArrayList<>(guard.visitedSpots);
      this.moves = new ArrayList<>(guard.moves);
      this.map = new HashMap<>(guard.map);
    }

    public void move() throws ArrayIndexOutOfBoundsException, LoopingException {
      Point next = new Point(position.x + direction.xDir, position.y + direction.yDir);
      if (!this.map.containsKey(next)) {
        throw new ArrayIndexOutOfBoundsException("Guard is out of bounds at point: " + next.toString());
      }
      if (this.map.get(next) == '#') {
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
        Move move = new Move(next, direction);
        if (moves.contains(move)) {
          throw new LoopingException("Guard is looping at point: " + next.toString());
        }
        moves.add(new Move(next, direction));
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

  public static boolean checkIfLooping(Guard guard) {
    try {
      while (true) {
        guard.move();
      }
    } catch(ArrayIndexOutOfBoundsException e) {
      return false;
    } catch (LoopingException e) {
      return true;
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(daySix.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/daySix.txt");

    // for visualizing the grid
    Move origin = new Move(new Point(0, 0), Direction.UP);

    for(int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        if (line.charAt(x) == '^') {
          origin = new Move(new Point(x, y), Direction.UP);
        }
        map.put(new Point(x, y), line.charAt(x));
      }
    }
    try {
      logger.info("Part one: {}", partOne(origin));
      logger.info("Part two: {}", partTwo(origin));
    } catch (LoopingException e) {
      logger.debug("Guard is looping: {}", e.getMessage());
    }
  }

  public static int partOne(Move origin) throws LoopingException {
    Guard guard = new Guard(origin.position, origin.direction, map);
    try {
      while (true) {
        guard.move();
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      logger.debug("Guard reached the end of the map: {}", guard.position.toString());
    }

    return guard.visitedSpots.stream().distinct().toList().size();
  }

  public static int partTwo(Move origin) throws LoopingException {
    Guard guard = new Guard(origin.position, origin.direction, map);
    List<Point> obstacles = new ArrayList<>();
    try {
      while (true) {
        Point next = new Point(guard.position.x + guard.direction.xDir, guard.position.y + guard.direction.yDir);
        if (guard.map.containsKey(next) && guard.map.get(next) != '#') {
          if (!guard.visitedSpots.contains(next)) {
            Guard newGuard = new Guard(guard);
            newGuard.map.put(next, '#');
            if (checkIfLooping(newGuard)) {
              obstacles.add(next);
            }
          }
        }
        guard.move();
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // do nothing
    }

    return obstacles.stream().distinct().toList().size();
  }

}
