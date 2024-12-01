package dayOne;

import static utils.utilities.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  public static void main(String[] args) throws IOException {
    Logger logger = getLogger(Main.class);

    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayOne.txt");

    List<Integer> l1 = new ArrayList<>(), l2 = new ArrayList<>();

    for (String line : input) {
      String[] split = line.split(" {3}");
      l1.add(Integer.parseInt(split[0]));
      l2.add(Integer.parseInt(split[1]));
    }

    logger.info("Part one: {}", partOne(l1, l2));
    logger.info("Part two: {}", partTwo(l1, l2));

  }

  public static int partOne(List<Integer> l1, List<Integer> l2) {
    Collections.sort(l1);
    Collections.sort(l2);

    List<Integer> diff = new ArrayList<>();
    for (int i = 0; i < l1.size(); i++) {
      diff.add(Math.abs(l1.get(i) - l2.get(i)));
    }

    return diff.stream().reduce(0, Integer::sum);
  }

  public static int partTwo(List<Integer> l1, List<Integer> l2) {
    Map<Integer, Integer> occurrences = new HashMap<>();
    int score = 0;

    for (Integer num: l1) {
      if (!occurrences.containsKey(num)) {
        occurrences.put(num, Collections.frequency(l2, num));
      }
      score += num * occurrences.get(num);
    }

    return score;
  }
}
