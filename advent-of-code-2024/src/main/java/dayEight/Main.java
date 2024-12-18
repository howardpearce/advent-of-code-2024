package dayEight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;
  static Set<Character> frequencies = new HashSet<>();

  public static class CityMap {
    Map<Point, Character> map;
    int width;
    int height;

    public CityMap(Map<Point, Character> map, int width, int height) {
      this.map = map;
      this.width = width;
      this.height = height;
    }

    public CityMap(CityMap cityMap) {
      this.map = new HashMap<>(cityMap.map);
      this.width = cityMap.width;
      this.height = cityMap.height;
    }
  }

  public record Point(int x, int y){
    @Override
    public String toString() {
      return String.format("(%d, %d)", x, y);
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayEight.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayEight.txt");


    HashMap<Point, Character> map = new HashMap<>();
    for(int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        map.put(new Point(x, y), line.charAt(x));
        if (line.charAt(x) != '.') {
          frequencies.add(line.charAt(x));
        }
      }
    }

    CityMap cityMap = new CityMap(map, input.getFirst().length(), input.size());

    logger.info("Part one: {}", partOne(new CityMap(cityMap)));
    logger.info("Part two: {}", partTwo(new CityMap(cityMap)));

  }

  public static int partOne(CityMap cm) {
    List<Point> antinodes = new ArrayList<>();
    for (char frequency : frequencies) {
      List<Point> towers = new ArrayList<>();
      for (Point point : cm.map.keySet()) {
        if (cm.map.get(point) == frequency) {
          towers.add(new Point(point.x, point.y));
        }
        for (Point a : towers) {
          for (Point b : towers) {
            if (a != b) {
              Point c = new Point(a.x + (a.x - b.x), a.y + (a.y - b.y));
              if (cm.map.containsKey(c) && cm.map.get(c) != frequency) {
                antinodes.add(c);
              }
            }
          }
        }
      }
    }

    return antinodes.stream().distinct().toList().size();
  }

  // very messy solution, I want to spend some more time on this. It seems like I can reduce a lot of duplicated code but I am busy
  public static int partTwo(CityMap cm) {
    List<Point> antinodes = new ArrayList<>();
    for (char frequency : frequencies) {
      List<Point> towers = new ArrayList<>();
      for (Point point : cm.map.keySet()) {
        if (cm.map.get(point) == frequency) {
          towers.add(new Point(point.x, point.y));
        }
        for (Point a : towers) {
          for (Point b: towers) {
            if (a != b) {
              int i = 0;
              while(true) {
                // get distance
                int xDistance = a.x - b.x;
                int yDistance = a.y - b.y;
                Point c = new Point(a.x + xDistance * i, a.y + yDistance * i);
                if (!cm.map.containsKey(c)) {
                  break;
                }
                if (!antinodes.contains(c)) {
                  antinodes.add(c);;
                }
                i++;
              }
            }
          }
        }
      }
    }

    return antinodes.size();
  }

}


