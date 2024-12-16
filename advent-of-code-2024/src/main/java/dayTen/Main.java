package dayTen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public record Point(int x, int y) { }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayTen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayTen.txt");

    Map<Point, Integer> map = new HashMap<>();
    List<Point> trailHeads = new ArrayList<>();

    for(int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        map.put(new Point(x, y), Character.getNumericValue(line.charAt(x)));
        if (line.charAt(x) == '0') {
          trailHeads.add(new Point(x, y));
        }
      }
    }

    logger.info("Part one: {}", partOne(new HashMap<>(map), trailHeads));
    logger.info("Part two: {}", partTwo(new HashMap<>(map), trailHeads));
  }

  public static int getMapValue(Point point, Map<Point, Integer> map) {
    if (!map.containsKey(point)) { return -1; }
    return map.get(point);
  }

  // recursively find each trail. Return every instance a point is found representing the end of the trail.
  public static List<Point> getTrailPeaks(Point current, Map<Point, Integer> map) {
    int value = getMapValue(current, map);
    List<Point> endPoints = new ArrayList<>();

    if (value == -1) { return endPoints; }
    if (value == 9) {
      endPoints.add(current);
      return endPoints;
    }


    Point up = new Point(current.x, current.y - 1);
    Point down = new Point(current.x, current.y + 1);
    Point left = new Point(current.x - 1, current.y);
    Point right = new Point(current.x + 1, current.y);

    List<Point> directions = List.of(up, down, left, right);

    for (Point direction : directions) {
      if (getMapValue(direction, map) == value + 1) {
        List<Point> p = getTrailPeaks(direction, map);
        endPoints.addAll(p);
      }
    }

    return endPoints;
  }

  public static int partOne(Map<Point, Integer> map, List<Point> trailHeads) {
    int sum = 0;
    for (Point trailHead : trailHeads) {
      List<Point> visited = new ArrayList<>();
      sum += getTrailPeaks(trailHead, map).stream().distinct().toList().size();
    }
    return sum;
  }

  public static int partTwo(Map<Point, Integer> map, List<Point> trailHeads) {
    int sum = 0;
    for (Point trailHead : trailHeads) {
      List<Point> visited = new ArrayList<>();
      List<Point> ends = getTrailPeaks(trailHead, map);
      sum += ends.size();
    }
    return sum;
  }

}
