package dayEleven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  record Expansion(Long value, int depth) {}
  static Map<Expansion, Long> knownExpansions = new HashMap<>();

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayEleven.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayEleven.txt");
    input = Arrays.asList(input.getFirst().split(" "));
    ArrayList<Long> stones = new ArrayList<>(input.stream().map(Long::parseLong).toList());
    logger.info("Part two: {}", partTwo(new ArrayList<>(stones)));
  }

  public static void addExpansion(Expansion ex, Long value) {
    knownExpansions.put(ex, value);
  }

  public static long expand(Long stone, int depth, int maxDepth) {
    Expansion current = new Expansion(stone, depth);
    Long value;

    if (depth == 0) {
      return 1;
    }

    if (knownExpansions.containsKey(current)) {
      return knownExpansions.get(current);
    }

    if (stone == 0) {
      value =  expand(1L, depth - 1, maxDepth);
    } else if (stone.toString().length() % 2 == 0) {
      String stoneAsString = stone.toString();
      Long firstHalf = Long.parseLong(stoneAsString.substring(0, stoneAsString.length() / 2));
      Long secondHalf = Long.parseLong(stoneAsString.substring(stoneAsString.length() / 2));
      value = expand(firstHalf, depth - 1, maxDepth) + expand(secondHalf, depth - 1, maxDepth);
    } else {
      value = expand(stone * 2024, depth - 1, maxDepth);
    }

    addExpansion(current, value);
    return value;
  }


  public static long partOne(ArrayList<Long> stones) {
    long sum = 0;
    for (Long stone : stones) {
      sum += expand(stone, 6, 6);
    }
    return sum;
  }

  public static long partTwo(ArrayList<Long> stones) {
    long sum = 0;
    for (Long stone : stones) {
      sum += expand(stone, 75, 75);
    }
    return sum;
  }

}
