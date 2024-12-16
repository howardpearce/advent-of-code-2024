package dayTwelve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public record Point(int x, int y) {}
  public record Plot(char type, List<Point> plants) { }

  static Map<Point, Character> map = new HashMap<>();
  static List<Plot> plots = new ArrayList<>();

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayTwelve.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayTwelve.txt");

    for(int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        map.put(new Point(x, y), line.charAt(x));
      }
    }

    for (Point p : map.keySet()) {
      boolean foundPlot = true;
      for (Plot plot : plots) {
        if (in(plot, p)) {
          foundPlot = false;
        }
      }
      if (foundPlot) {
        // create new plot
        Plot plot = new Plot( map.get(p), new ArrayList<>());
        // discover all other plants in plot
        List<Point> contiguousPoints = getContiguousPoints(p, new ArrayList<>(), map.get(p));
        plot.plants.addAll(contiguousPoints);
        plots.add(plot);
      }
    }

    logger.info("Part one: {}", partOne(plots));
    logger.info("Part two: {}", partTwo(plots));
  }

  public static List<Point> getSurroundingPoints(Point p) {
    Point up = new Point(p.x, p.y - 1);
    Point down = new Point(p.x, p.y + 1);
    Point left = new Point(p.x - 1, p.y);
    Point right = new Point(p.x + 1, p.y);

    return Arrays.asList(up, down, left, right);
  }

  public static List<Point> getContiguousPoints(Point p, List<Point> discovered, Character a) {
    ArrayList<Point> points = new ArrayList<>();
    discovered.add(p);
    points.add(p);

    for (Point direction : getSurroundingPoints(p)) {
      if (!discovered.contains(direction) && map.containsKey(direction) && map.get(direction) == a) {
        points.addAll(getContiguousPoints(direction, discovered, a));
      }
    }

    return points;
  }

  public static int getPlotPerimeter(Plot plot) {
    int perimiter = 0;
    for (Point p : plot.plants) {
      for (Point direction : getSurroundingPoints(p)) {
        if (!in(plot, direction)) {
          perimiter++;
        }
      }
    }

    return perimiter;
  }

  public static int getPlotSides(Plot plot) {
    int sides = 0;

    for (Point p : plot.plants) {
      Point up = new Point(p.x, p.y - 1);
      Point upLeft = new Point(p.x-1, p.y - 1);
      Point upRight = new Point(p.x+1, p.y - 1);
      Point down = new Point(p.x, p.y + 1);
      Point downLeft = new Point(p.x-1, p.y + 1);
      Point downRight = new Point(p.x+1, p.y + 1);
      Point left = new Point(p.x - 1, p.y);
      Point right = new Point(p.x + 1, p.y);

      if (!in(plot, up) && !in(plot, left)) {sides++;}
      if (!in(plot, up) && !in(plot, right)) {sides++;}
      if (!in(plot, down) && !in(plot, left)) {sides++;}
      if (!in(plot, down) && !in(plot, right)) {sides++;}

      if (in(plot, up) && in(plot, left) && !in(plot, upLeft)) {sides++;}
      if (in(plot, up) && in(plot, right) && !in(plot, upRight)) {sides++;}
      if (in(plot, down) && in(plot, left) && !in(plot, downLeft)) {sides++;}
      if (in(plot, down) && in(plot, right) && !in(plot, downRight)) {sides++;}
    }

    return sides;
  }

  public static boolean in(Plot plt, Point p) {
    return plt.plants.contains(p);
  }

  public static int partOne(List<Plot> plots) {
    int sum = 0;
    for (Plot plot : plots) {
      sum += getPlotPerimeter(plot) * plot.plants.size();
    }
    return sum;
  }

  public static int partTwo(List<Plot> plots) {
    int sum = 0;
    for (Plot plot : plots) {
      sum += getPlotSides(plot) * plot.plants.size();
    }
    return sum;
  }

}

