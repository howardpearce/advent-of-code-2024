package dayNineteen;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public static void main(String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayNineteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayNineteen.txt");

    Set<String> patternSet = Stream.of(input.getFirst().split(",")).map(String::trim).collect(Collectors.toSet());
    input = input.subList(2, input.size());

    List<String> desiredDesigns = new ArrayList<>(input);

    logger.info("Part one: " + partOne(patternSet, desiredDesigns));
    logger.info("Part two: " + partTwo(patternSet, desiredDesigns));
  }

  private static boolean canConstructDesign(String design, Set<String> patterns, Map<String, Boolean> memo) {
    if (memo.containsKey(design)) { return memo.get(design); }
    if (design.isEmpty()) { return true; }

    for (String pattern : patterns) {
      if (design.startsWith(pattern)) {
        String remainingDesign = design.substring(pattern.length());
        if (canConstructDesign(remainingDesign, patterns, memo)) {
          memo.put(design, true);
          return true;
        }
      }
    }

    return false;
  }

  private static long countWaysToConstruct(String design, Set<String> patterns, Map<String, Long> memo) {
    if (memo.containsKey(design)) { return memo.get(design); }
    if (design.isEmpty()) { return 1; }

    long totalWays = 0;

    for (String pattern : patterns) {
      if (design.startsWith(pattern)) {
        String remainingDesign = design.substring(pattern.length());
        totalWays += countWaysToConstruct(remainingDesign, patterns, memo);
      }
    }

    memo.put(design, totalWays);
    return totalWays;
  }

  public static int partOne(Set<String> patternSet, List<String> desiredDesigns) {
    int possibleCount = 0;
    for (String design : desiredDesigns) {
      if (canConstructDesign(design, patternSet, new HashMap<>())) {
        possibleCount++;
      }
    }
    return possibleCount;
  }

  public static long partTwo(Set<String> patternSet, List<String> desiredDesigns) {
    long totalWays = 0;
    for (String design : desiredDesigns) {
      totalWays += countWaysToConstruct(design, patternSet, new HashMap<>());
    }
    return totalWays;
  }
}
