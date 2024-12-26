package daySixteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  static List<List<Point>> paths = new ArrayList<>();

  public static class Point {
    public int x, y;

    public Point(Point p) {
      this(p.x, p.y);
    }

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {return true;}
      if (o == null || getClass() != o.getClass()) {return false;}
      Point point = (Point) o;
      return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

  }
  public record Distance(Point point, Integer distance) {}

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(daySixteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/daySixteen.txt");
    Point start = null, end = null;
    Map<Point, Character> map = new HashMap<>();

    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      for (int j = 0; j < line.length(); j++) {

        if (line.charAt(j) == 'S') { start = new Point(j, i); }
        if (line.charAt(j) == 'E') { end = new Point(j, i); }

        map.put(new Point(j, i), line.charAt(j));
      }
    }

    Map<Point, List<Point>> graph = buildGraph(map);

    logger.info("Part one: " + partOne(map, graph, start, end));
    logger.info("Part two: " + partTwo(map, graph, start, end));

  }

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
          if (map.get(dir) == '.' || map.get(dir) == 'E' || map.get(dir) == 'S') {
            adjacents.add(dir);
          }
        }

        graph.put(p, adjacents);
      }
    }
    return graph;
  }

  public static boolean isTurning(Point prev, Point current, Point next) {
    if (prev == null) return true;
    if (next == null) return false;

    int dx1 = current.x - prev.x;
    int dy1 = current.y - prev.y;
    int dx2 = next.x - current.x;
    int dy2 = next.y - current.y;

    return dx1 != dx2 || dy1 != dy2;
  }

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

          // Add a penalty if turning
          Point prev = previous.get(current);
          if (isTurning(prev, current, neighbor)) {
            newDistance += 1000;
          }

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

  public static int getScoreForPath(List<Point> path) {
    int sum = 0;
    Point prev = new Point(path.getFirst().x - 1, path.getFirst().y);
    for (int i = 0; i < path.size() - 1; i++) {
      if (i - 1 >= 0) { prev = path.get(i - 1); }
      Point next = path.get(i + 1);
      if (isTurning(prev, path.get(i), next)) {
        sum += 1000;
      }
    }
    return sum + path.size() - 1;
  }

  public static int partOne(Map<Point, Character> map, Map<Point, List<Point>> graph, Point start, Point end) {
    List<Point> path = getShortestPath(map, graph, start, end);
    return getScoreForPath(path);
  }

  public static Set<Point> getAlternatePaths(List<Point> path, Map<Point, Character> map, Map<Point, List<Point>> graph, Point start, Point end) {
    Set<Point> tiles = new HashSet<>();

    for (int i = 0; i < path.size() - 1; i++) {
      Point p = path.get(i);
      if (graph.get(p).size() >= 3 || p.equals(start)) {
        for (Point neighbor : graph.get(p)) {
          if (neighbor.equals(path.get(i + 1))) {
            Map<Point, Character> alternateMap = new HashMap<>(map);

            alternateMap.put(neighbor, '#');
            Map<Point, List<Point>> alternateGraph = buildGraph(alternateMap);
            List<Point> alternatePath = getShortestPath(alternateMap, alternateGraph, path.get(i), end);
            alternatePath.add(path.get(i));
            for (Point pr : path.subList(0, i).reversed()) {
              alternatePath.addFirst(pr);
            }

            if (getScoreForPath(alternatePath.stream().distinct().toList()) == getScoreForPath(path)) {
              tiles.addAll(alternatePath);
            }
          }
        }
      }
    }

    return tiles;
  }

  // very lazy, but I am sick of working on this question
  public static int partTwo(Map<Point, Character> map, Map<Point, List<Point>> graph, Point start, Point end) {
    Set<Point> bestTiles = new HashSet<>();
    List<Point> path = getShortestPath(map, graph, start, end);
    bestTiles.addAll(path);
    bestTiles.addAll(getAlternatePaths(path, map, graph, start, end));
    return bestTiles.size();
  }
}
