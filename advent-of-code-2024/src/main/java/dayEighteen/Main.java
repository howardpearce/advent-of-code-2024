package dayEighteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  static final int gridWidth = 70, gridHeight = 70;

  public record Point(int x, int y) {}
  public record Distance(Point point, Integer distance) {}

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayEighteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayEighteen.txt");

    List<Point> points = new ArrayList<>();
    Map<Point, Character> map = new HashMap<>();

    for (int y = 0; y <= gridHeight; y++) {
      for (int x = 0; x <= gridWidth; x++) {
        map.put(new Point(x, y), '.');
      }
    }

    for (String line : input) {
      String[] parts = line.split(",");
      points.add(new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
    }

    logger.info("Part one: {}", partOne(map, points.subList(0, 1024)));
    logger.info("Part two: {}", partTwo(map, points, 1024));

  }

  public static void printGrid(Map<Point, Character> grid) {
    for (int y = 0; y <= gridHeight; y++) {
      for (int x = 0; x <= gridWidth; x++) {
        System.out.print(grid.get(new Point(x, y)));
      }
      System.out.println();
    }
  }

  public static int partOne(Map<Point, Character> map, List<Point> input) {

    for (Point p : input) { map.put(p, '#'); }

    Map<Point, List<Point>> graph = buildGraph(map);
    List<Point> path = getShortestPath(map, graph, new Point(0, 0), new Point(gridWidth, gridHeight));

    return path.size() - 1;
  }

  public static Point partTwo(Map<Point, Character> map, List<Point> input, int bytes) {
    for (Point p : input.subList(0, bytes)) { map.put(p, '#'); }

    Map<Point, List<Point>> shortestGraph = buildGraph(map);
    List<Point> shortestPath = getShortestPath(map, shortestGraph, new Point(0, 0), new Point(gridWidth, gridHeight));

    int counter = bytes;
    while (true) {
      map.put(input.get(counter), '#');
      if (shortestPath.contains(input.get(counter))) {
        Map<Point, List<Point>> graph = buildGraph(map);
        List<Point> path = getShortestPath(map, graph, new Point(0, 0), new Point(gridWidth, gridHeight));
        if (path.size() < 100) break;
      }
      counter++;
    }
    return input.get(counter-1);
  }

  // lifted from day 16 with some edits
  public static Map<Point, List<Point>> buildGraph(Map<Point, Character> map) {
    Map<Point, List<Point>> graph = new HashMap<>();
    for (Point p : map.keySet()) {
      if (map.get(p) == '.' || map.get(p) == 'E' || map.get(p) == 'S') {

        Point up = new Point(p.x, p.y - 1);
        Point down = new Point(p.x, p.y + 1);
        Point left = new Point(p.x - 1, p.y);
        Point right = new Point(p.x + 1, p.y);

        List<Point> directions = List.of(up, down, left, right);
        List<Point> adjacents = new ArrayList<>();
        for (Point dir : directions) {
          if (map.containsKey(dir) && map.get(dir) == '.') {
            adjacents.add(dir);
          }
        }

        graph.put(p, adjacents);
      }
    }
    return graph;
  }
  // lifted from day 16 with some edits
  public static List<Point> getShortestPath(Map<Point, Character> map, Map<Point, List<Point>> graph, Point start, Point end) {
    Map<Point, Integer> distances = new HashMap<>();
    Map<Point, Point> previous = new HashMap<>();
    Set<Point> visited = new HashSet<>();
    PriorityQueue<Distance> queue = new PriorityQueue<>(Comparator.comparingInt(Distance::distance));

    for (Point point : graph.keySet()) {
      distances.put(point, Integer.MAX_VALUE);
    }
    distances.put(start, 0);
    queue.add(new Distance(start, 0));

    while (!queue.isEmpty()) {
      Point current = queue.poll().point();
      if (visited.contains(current)) continue;
      visited.add(current);

      if (current.equals(end)) break;

      for (Point neighbor : graph.get(current)) {
        if (!visited.contains(neighbor)) {
          int newDistance = distances.get(current) + 1;

          if (newDistance < distances.get(neighbor)) {
            distances.put(neighbor, newDistance);
            previous.put(neighbor, current);
            queue.add(new Distance(neighbor, newDistance));
          }
        }
      }
    }

    List<Point> path = new LinkedList<>();
    Point current = end;
    while (current != null && !current.equals(start)) {
      path.add(0, current);
      current = previous.get(current);
    }
    path.add(0, start);

    return path;
  }

}
