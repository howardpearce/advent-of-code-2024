package dayFifteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public record Point(int x, int y) {
    @Override
    public boolean equals(Object o) {
      if (this == o) {return true;}
      if (o == null || getClass() != o.getClass()) {return false;}
      Point point = (Point) o;
      return x == point.x && y == point.y;
    }
  }

  public record Warehouse(Point robot, Map<Point, Character> map, List<Character> directions, int width, int height) { }

  public static class Box {
    Point firstHalf;
    Point secondHalf;
    public Box(Map<Point, Character> map, Point firstHalf) {
      this.firstHalf = firstHalf;
      if (map.get(firstHalf) == '[') {
        this.secondHalf = new Point(firstHalf.x + 1, firstHalf.y);
      } else if (map.get(firstHalf) == ']') {
        this.secondHalf = new Point(firstHalf.x - 1, firstHalf.y);
      } else {
        logger.error("Box is not a box");
      }
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayFifteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayFifteen.txt");

    Warehouse warehouse = parseInput(new ArrayList<>(input));

    logger.info("Part one: " + partOne(warehouse.map, warehouse.directions, warehouse.robot));

    // double the size of everything, I guess.
    List<String> partTwoInput = new ArrayList<>();
    for (String line : input) {
      line = line.replace("#", "##");
      line = line.replace(".", "..");
      line = line.replace("O", "[]");
      line = line.replace("@", "@.");
      partTwoInput.add(line);
    }

    warehouse = parseInput(new ArrayList<>(partTwoInput));
    logger.info("Part two: " + partTwo(warehouse));

  }

  public static Warehouse parseInput(List<String> input) {
    Map<Point, Character> map = new HashMap<>();
    List<Character> directions = new ArrayList<>();
    Point robot = null;
    int width = input.getFirst().length();
    int i = 0;

    while(true) {
      if (input.get(0).isEmpty()) {
        input = input.subList(1, input.size());
        break;
      }

      // parse line then remove it
      for (int j = 0; j < input.get(0).length(); j++) {
        map.put(new Point(j, i), input.get(0).charAt(j));
        if (input.get(0).charAt(j) == '@') {
          robot = new Point(j, i);
        }
      }
      i++;
      input.removeFirst();
    }

    for (String line : input) {
      for (char c : line.toCharArray()) {
        directions.add(c);
      }
    }

    return new Warehouse(robot, map, directions, width, i);
  }

  public static Point moveBoxToPoint(Map<Point, Character> map, Point start, char dir) {
    switch (dir) {
      case '^': // up
        return new Point(start.x, start.y - 1);
      case 'v': // down
        return new Point(start.x, start.y + 1);
      case '>': // right
        if (map.get(start) == '[') {
          return new Point(start.x + 2, start.y);
        }
        return new Point(start.x + 1, start.y);
      case '<': // left
        if (map.get(start) == ']') {
          return new Point(start.x - 2, start.y);
        }
        return new Point(start.x - 1, start.y);
    }
    return null;
  }

  public static Point moveToPoint(Point start, char dir) {
    switch (dir) {
      case '^': // up
        return new Point(start.x, start.y - 1);
      case 'v': // down
        return new Point(start.x, start.y + 1);
      case '>': // right
        return new Point(start.x + 1, start.y);
      case '<': // left
        return new Point(start.x - 1, start.y);
    }
    return null;
  }

  public static void printMap(Map<Point, Character> map, int width, int height) {
    // print the map as a grid
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        System.out.print(map.get(new Point(j, i)));
      }
      System.out.println();
    }
    System.out.println();
  }

  public static int partOne(Map<Point, Character> map, List<Character> directions, Point robot) {

    for (char dir : directions) {
      Point next = moveToPoint(robot, dir);
      if (map.get(next) == '#') { continue; }

      // There is a box. Can it be moved?
      if (map.get(next) == 'O') {
        Point boxNext = moveToPoint(next, dir);
        while (map.get(boxNext) == 'O') {
          boxNext = moveToPoint(boxNext, dir);
        }
        if (map.get(boxNext) == '.') {
          map.put(boxNext, 'O');
          map.put(next, '.');
          map.put(robot, '.');
          robot = next;
          map.put(robot, '@');
        }
        continue;
      }

      map.put(robot, '.');
      robot = next;
      map.put(robot, '@');
    }

    int sum = 0;

    for (Point p : map.keySet()) {
      if (map.get(p) == 'O') {
        sum += p.x + (p.y * 100);
      }
    }

    return sum;
  }

  public static boolean moveBox(Map<Point, Character> map, Box box, char dir, boolean modify) {
    Point next = moveToPoint(box.firstHalf, dir);
    Point nextBox = moveBoxToPoint(map, box.firstHalf, dir);

    Point otherHalfNext = moveToPoint(box.secondHalf, dir);
    Point otherHalfNextBox = moveBoxToPoint(map, box.secondHalf, dir);

    if (map.get(next) == '#' || map.get(otherHalfNext) == '#') { return false; }
    if (map.get(nextBox) == '.' && ((dir == '^' || dir == 'v') && map.get(otherHalfNextBox) == '.')) {
      if (modify) {
        // we can move the box
        char tempbox = map.get(box.firstHalf);
        char tempOtherHalf = map.get(box.secondHalf);
        map.put(box.firstHalf, '.');
        map.put(box.secondHalf, '.');
        map.put(next, tempbox);
        map.put(moveToPoint(box.secondHalf, dir), tempOtherHalf);
      }
      return true;
    }

    if (!checkBoxes(map, box, dir)) {
      return false;
    }

    if (dir == '^' || dir == 'v') {
      if (map.get(otherHalfNextBox) == '[' || map.get(otherHalfNextBox) == ']') {
        moveBox(map, new Box(map, otherHalfNextBox), dir, modify);
      }
    }

    // There is a box ahead. Move that first.
    if (map.get(nextBox) == '[' || map.get(nextBox) == ']') {
      moveBox(map, new Box(map, nextBox), dir, modify);
    }

    if (modify) {
      char tempbox = map.get(box.firstHalf);
      char tempOtherHalf = map.get(box.secondHalf);
      map.put(box.firstHalf, '.');
      map.put(box.secondHalf, '.');
      map.put(next, tempbox);
      map.put(moveToPoint(box.secondHalf, dir), tempOtherHalf);
    }

    return true;
  }

  // Does the same thing as above without modifying the map... Basically a duplicate of above by I am too tired to fix right now.
  public static boolean checkBoxes(Map<Point, Character> map, Box box, char dir) {
    Point next = moveToPoint(box.firstHalf, dir);
    Point nextBox = moveBoxToPoint(map, box.firstHalf, dir);
    Point otherHalfNext = moveToPoint(box.secondHalf, dir);
    Point otherHalfNextBox = moveBoxToPoint(map, box.secondHalf, dir);

    if (map.get(next) == '#' || map.get(otherHalfNext) == '#') { return false; }
    if (map.get(nextBox) == '.' && ((dir == '^' || dir == 'v') && map.get(otherHalfNextBox) == '.')) { return true; }

    boolean move = true;
    boolean moveOtherHalf = true;
    if (dir == '^' || dir == 'v') {
      if (map.get(otherHalfNextBox) == '[' || map.get(otherHalfNextBox) == ']') {
        moveOtherHalf = checkBoxes(map, new Box(map, otherHalfNextBox), dir);
      }
    }
    // There is a box ahead. Move that first.
    if (map.get(nextBox) == '[' || map.get(nextBox) == ']') {
      move = checkBoxes(map, new Box(map, nextBox), dir);
    }

    return move && moveOtherHalf;
  }

  public static int partTwo(Warehouse w) {
    Point robot = w.robot;

    int i = 0;
    for (char dir : w.directions) {
      i++;
      Point next = moveToPoint(robot, dir);
      if (w.map.get(next) == '#') { continue; }

      // There is a box. Can it be moved?
      if (w.map.get(next) == '[' || w.map.get(next) == ']') {
        boolean moved = moveBox(w.map, new Box(w.map, next), dir, true);
        if (moved) {
          w.map.put(next, '.');
        } else {
          continue;
        }
      }

      w.map.put(robot, '.');
      robot = next;
      w.map.put(robot, '@');

    }

    int sum = 0;

    for (Point p : w.map.keySet()) {
      if (w.map.get(p) == '[') {
        sum += p.x + (p.y * 100);
      }
    }

    return sum;
  }
}
