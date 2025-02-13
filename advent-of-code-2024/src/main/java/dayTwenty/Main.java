package dayTwenty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import dayEighteen.Main.Point;
import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public record Point(int x, int y) {}

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayTwenty.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayTwenty.txt");
    Map<Point, Character> map = new HashMap<>();
    Point start = null, end = null;

    // record the points in the map
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      for (int j = 0; j < line.length(); j++) {

        if (line.charAt(j) == 'S') {
          start = new Point(j, i);
        }
        else if (line.charAt(j) == 'E') {
          end = new Point(j, i);
        }

        map.put(new Point(j, i), line.charAt(j));

      }
    }

    // get the path from start to end
    List<Point> path = getShortestPath(map, start, end);
    // solve based on how many steps you can cheat
    logger.info("Part one: {}", solve(path, 2));
    logger.info("Part two: {}", solve(path, 20));
  }

  public static int solve(List<Point> path, int cheatDistance) {

    int sum = 0;

    for (int i = 0; i < path.size(); i++) {
      Point p = path.get(i);
      // don't bother checking the close by points
      // as we need to cheat a certain number of points
      // checking if we can cheat to the next point is pointless
      for (int j = i + cheatDistance; j < path.size(); j++) {
        Point p2 = path.get(j);
        if (getManhattanDistance(p, p2) <= cheatDistance) {
          // getting this cheat time formula down was the hardest part for me
          int cheatTime = j - getManhattanDistance(p, p2) - i;
          if (cheatTime >= 100 ) {
            sum++;
          }
        }
      }
    }

    return sum;
  }

  public static int getManhattanDistance(Point p1, Point p2) {
    return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
  }

  public static List<Point> getShortestPath(Map<Point, Character> map, Point start, Point end) {
    Stack<List<Point>> stack = new Stack<>();
    stack.push(new ArrayList<>(List.of(start)));

    List<Character> validChars = List.of('.', 'E', 'S');

    while (!stack.isEmpty()) {
      List<Point> path = stack.pop();
      Point current = path.get(path.size() - 1);

      if (current.equals(end)) { return path; }

      for (Point neighbor : getNeighbors(current)) {
        if (!map.containsKey(neighbor) || !validChars.contains(map.get(neighbor)) || path.contains(neighbor)) {
          continue;
        }

        List<Point> newPath = new ArrayList<>(path);
        newPath.add(neighbor);
        stack.push(newPath);
      }
    }
    // no path was found
    return null;
  }

  private static List<Point> getNeighbors(Point p) {
    return List.of(
        new Point(p.x, p.y - 1), // Up
        new Point(p.x, p.y + 1), // Down
        new Point(p.x - 1, p.y), // Left
        new Point(p.x + 1, p.y)  // Right
    );
  }


}

