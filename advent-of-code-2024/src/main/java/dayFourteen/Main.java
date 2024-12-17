package dayFourteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import utils.utilities;

// this was fun and simple but there is a ton of extra code that could be cut down. Very verbose.
public class Main {
  static Logger logger;

  public static class Vec2D {
    public int x;
    public int y;

    public Vec2D(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public Vec2D(Vec2D v) {
      this.x = v.x;
      this.y = v.y;
    }
  }

  public static class Robot {
    public Vec2D p;
    public Vec2D v;

    public Robot(Vec2D p, Vec2D v) {
      this.p = p;
      this.v = v;
    }

    public Robot(Robot r) {
      this.p = new Vec2D(r.p.x, r.p.y);
      this.v = new Vec2D(r.v.x, r.v.y);
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayFourteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayFourteen.txt");

    List<Robot> robots = new ArrayList<>();

    for (String line : input) {
      String[] split = line.split(" ");
      String point = split[0].substring(split[0].indexOf("=")+1);
      Vec2D p = new Vec2D(Integer.parseInt(point.split(",")[0]), Integer.parseInt(point.split(",")[1]));
      String velocity = split[1].substring(split[1].indexOf("=")+1);
      Vec2D v = new Vec2D(Integer.parseInt(velocity.split(",")[0]), Integer.parseInt(velocity.split(",")[1]));
      robots.add(new Robot(p, v));
    }

    // deep copy the robots list to avoid modifying the original list
    logger.info("Part one: {}", partOne(robots.stream().map(Robot::new).collect(Collectors.toList()), 101, 103, 100));
    logger.info("Part two: {}", partTwo(robots.stream().map(Robot::new).collect(Collectors.toList()), 101, 103));
  }

  public static List<Robot> simulate(List<Robot> robots, int width, int height, int seconds) {
    for (int i = 0; i < seconds; i++) {
      for (Robot r : robots) {
        int newX = r.p.x + r.v.x;
        if (newX >= width || newX < 0) {
          if (newX < 0) {
            r.p.x = width - Math.abs(newX) % width;
          } else {
            r.p.x = Math.abs(newX) % width;
          }
        } else {
          r.p.x += r.v.x;
        }

        int newY = r.p.y + r.v.y;
        if (newY >= height || newY < 0) {
          if (newY < 0) {
            r.p.y = height - Math.abs(newY) % height;
          } else {
            r.p.y = Math.abs(newY) % height;
          }
        } else {
          r.p.y += r.v.y;
        }
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
    for (int i = 0; i < quadrants.length; i++) { sum *= quadrants[i]; }

    return sum;
  }

  public static int partTwo(List<Robot> robots, int width, int height) {
    int count = 0;
    while(true) {
      robots = simulate(new ArrayList<>(robots), width, height, 1);
      count++;
      // check if every robot is in a distinct area
      boolean distinct = true;
      for (Robot r : robots) {
        for (Robot r2 : robots) {
          if (r != r2 && r.p.x == r2.p.x && r.p.y == r2.p.y) {
            distinct = false;
            break;
          }
        }
        if (!distinct) { break; }
      }
      if (distinct) {
        // print the grid to see if it's a tree
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
        return count;
      }
    }
  }

}
