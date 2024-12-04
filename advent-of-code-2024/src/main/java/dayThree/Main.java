package dayThree;

import static utils.utilities.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  static Pattern mulPattern = Pattern.compile("(mul)\\((\\d+),(\\d+)\\)");
  static Pattern doMulPattern = Pattern.compile("(don't)|(do)|(mul)\\((\\d+),(\\d+)\\)");

  public static void main(String[] args) throws IOException {
    logger = getLogger(dayOne.Main.class);

    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayThree.txt");

    logger.info("Part one: {}", partOne(input));
    logger.info("Part two: {}", partTwo(input));
  }

  public static int partOne(List<String> input) {
    int sum = 0;

    for (String line : input) {
      Matcher m = mulPattern.matcher(line);
      while (m.find()) {
        int a = Integer.parseInt(m.group(2));
        int b = Integer.parseInt(m.group(3));
        sum += a * b;
      }
    }

    return sum;
  }

  public static int partTwo(List<String> input) {
    int sum = 0;
    boolean add = true;

    for (String line : input) {
      Matcher m = doMulPattern.matcher(line);
      while (m.find()) {
        String matchedString = m.group(0);
        if (matchedString.equals("do")) {
          add = true;
        } else if (matchedString.equals("don't")) {
          add = false;
        } else {
          if (add) {
            int a = Integer.parseInt(m.group(4));
            int b = Integer.parseInt(m.group(5));
            sum += a * b;
          }
        }
      }
    }

    return sum;
  }

}
