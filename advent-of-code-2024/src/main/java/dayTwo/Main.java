package dayTwo;

import static utils.utilities.getLogger;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;
  public static void main(String[] args) throws IOException {
    logger = getLogger(dayOne.Main.class);

    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayTwo.txt");

    List<List<Integer>> report = new ArrayList<>();

    for (String line : input) {
      String[] split = line.split(" ");
      List<Integer> row = new ArrayList<>();
      for (String num : split) {
        row.add(Integer.parseInt(num));
      }
      report.add(row);
    }

    logger.info("Part one: {}", partOne(report));
    logger.info("Part two: {}", partTwo(report));
  }

  public static int partOne(List<List<Integer>> report) {
    int sum = 0;

    for (List<Integer> row : report) {
      boolean status = isSafe(row, false);
      if (status) sum++;
    }

    return sum;
  }

  public static int partTwo(List<List<Integer>> report) {
    int sum = 0;

    for (List<Integer> row : report) {
      boolean status = isSafe(row, true);
      if (status) sum++;
    }

    return sum;
  }

  public static List<Integer> removeElement(List<Integer> row, int index) {
    if (index < 0 || index >= row.size()) return row;

    List<Integer> copy = new ArrayList<>(row);
    copy.remove(index);
    return copy;
  }

  public static boolean isSafe(List<Integer> row, boolean ignoreFirst) {
    int last = row.get(0);
    int direction;
    if (last < row.get(1)) {
      direction = -1;
    } else if (last > row.get(1)) {
      direction = 1;
    } else {
      if (ignoreFirst) {
        return isSafe(removeElement(row, 0), false);
      } else {
        return false;
      }
    }

    for (int i = 1; i < row.size(); i++) {
      int diff = row.get(i-1) - row.get(i);
      if (!(Integer.signum(diff) == direction && Math.abs(diff) <= 3)) {
        if (ignoreFirst) {
          // lazy but oh wellc
          return isSafe(removeElement(row, i), false) || isSafe(removeElement(row, i-1), false) || isSafe(removeElement(row, i-2), false);
        } else {
          return false;
        }
      }
    }

    return true;
  }
}
