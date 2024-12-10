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

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();

      char[][] mapArray = new char[this.width][this.height];

      for (Point point : map.keySet()) {
        mapArray[point.x][point.y] = map.get(point);
      }

      for (int y = 0; y < this.height; y++) {
        for (int x = 0; x < this.width; x++) {
          sb.append(mapArray[x][y]);
        }
        sb.append("\n");
      }

      return sb.toString();
    }
  }

  public record Point(int x, int y){
    @Override
    public String toString() {
      return String.format("(%d, %d)", x, y);
    }
  }

  public record Antinode(Point point) {}

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

    logger.info("Part one: {}", partOne(cityMap));
    logger.info("Part two: {}", partTwo(cityMap));

  }

  public static int partOne(CityMap cityMap) {
    CityMap cm = new CityMap(cityMap);

    List<Antinode> antinodes = new ArrayList<>();
    for (char frequency : frequencies) {
      List<Point> towers = new ArrayList<>();
      for (Point point : cm.map.keySet()) {
        if (cm.map.get(point) == frequency) {
          towers.add(new Point(point.x, point.y));
        }
        for (Point a : towers) {
          for (Point b: towers) {
            if (a != b) {
              // get distance
              int xDistance = a.x - b.x;
              int yDistance = a.y - b.y;
              Point c = new Point(a.x + xDistance, a.y + yDistance);
              if (cm.map.containsKey(c) && cm.map.get(c) != frequency) {
                antinodes.add(new Antinode(new Point(a.x + xDistance, a.y + yDistance)));
                //cityMap.map.put(new Point(a.x + xDistance, a.y + yDistance), '#');
              }
            }
          }
        }
      }
    }

    return antinodes.stream().distinct().toList().size();
  }

  public static int partTwo(CityMap cityMap) {
    CityMap cm = new CityMap(cityMap);

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

    for(Point antinode : antinodes) {
      cm.map.put(antinode, '#');
    }
    System.out.println(cm);

    return antinodes.size();
  }

}


