package dayFourteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import utils.utilities;

// this was fun and simple but there is a ton of extra code that could be cut down. Very verbose.
public class Main {
  static Logger logger;

  public static class Pair {
    public int x, y;

    public Pair(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {return true;}
      if (o == null || getClass() != o.getClass()) {return false;}
      Pair pair = (Pair) o;
      return x == pair.x && y == pair.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

  }

  public static class Robot {
    public Pair p, v;

    public Robot(Pair p, Pair v) {
      this.p = p;
      this.v = v;
    }

    public Robot(Robot r) {
      this.p = new Pair(r.p.x, r.p.y);
      this.v = new Pair(r.v.x, r.v.y);
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayFourteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayFourteen.txt");
    List<Robot> robots = new ArrayList<>();

    Pattern pattern = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");
    for (String line : input) {
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        Pair p = new Pair(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        Pair v = new Pair(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        robots.add(new Robot(p, v));
      }
    }

    // deep copy the robots list to avoid modifying the original list
    logger.info("Part one: {}", partOne(robots.stream().map(Robot::new).collect(Collectors.toList()), 101, 103, 100));
    logger.info("Part two: {}", partTwo(robots.stream().map(Robot::new).collect(Collectors.toList()), 101, 103));
  }

  public static List<Robot> simulate(List<Robot> robots, int width, int height, int seconds) {
    for (int i = 0; i < seconds; i++) {
      for (Robot r : robots) {
        r.p.x = (r.p.x + r.v.x + width) % width;
        r.p.y = (r.p.y + r.v.y + height) % height;
      }
    }
    return robots;
  }

  public static int partOne(List<Robot> robots, int width, int height, int seconds) {
    robots = simulate(new ArrayList<>(robots), width, height, seconds);

    // divide into quadrants
    int middleX = width / 2;
    int middleY = height / 2;
    int[] quadrants = new int[4];

    for (Robot r : robots) {
      if (r.p.x < middleX && r.p.y < middleY) { quadrants[0]++; }
      if (r.p.x > middleX && r.p.y < middleY) { quadrants[1]++; }
      if (r.p.x < middleX && r.p.y > middleY) { quadrants[2]++; }
      if (r.p.x > middleX && r.p.y > middleY) { quadrants[3]++; }
    }

    int sum = 1;
    for (int quadrant : quadrants) {sum *= quadrant;}
    return sum;
  }

  public static void printGrid(List<Robot> robots, int width, int height) {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        boolean found = false;
        for (Robot r : robots) {
          if (r.p.x == j && r.p.y == i) {
            System.out.print("#");
            found = true;
            break;
          }
        }
        if (!found) { System.out.print("."); }
      }
      System.out.println();
    }
  }

  public static int partTwo(List<Robot> robots, int width, int height) {
    int count = 0;
    while(true) {
      robots = simulate(new ArrayList<>(robots), width, height, 1);
      count++;
      // check if every robot is in a distinct area
      Set<Pair> locations = new HashSet<>();
      for (Robot r : robots) { locations.add(r.p); }
      if (locations.size() == robots.size()) {
        printGrid(robots, width, height);
        return count;
      }
    }
  }

}
