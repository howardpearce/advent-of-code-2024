package dayFour;

import static utils.utilities.getLogger;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {

  static Logger logger;

  // Convolution patterns to detect for XMAS
  // You could cut out half of these, and check the convolutions in reverse as well
  static String[] c1  = { "XMAS", "....", "....", "...." };
  static String[] c2  = { "SAMX", "....", "....", "...." };
  static String[] c3  = { "S...", "A...", "M...", "X..." };
  static String[] c4  = { "X...", "M...", "A...", "S..." };
  static String[] c5  = { "X...", ".M..", "..A.", "...S" };
  static String[] c6  = { "S...", ".A..", "..M.", "...X" };
  static String[] c7  = { "...X", "..M.", ".A..", "S..." };
  static String[] c8  = { "...S", "..A.", ".M..", "X..." };
  static List<String[]> partOneConvolutions = List.of(c1, c2, c3, c4, c5, c6, c7, c8);

  static String[] c9  = { "M.S", ".A.", "M.S" };
  static String[] c10 = { "S.S", ".A.", "M.M" };
  static String[] c11 = { "S.M", ".A.", "S.M" };
  static String[] c12 = { "M.M", ".A.", "S.S" };
  static List<String[]> partTwoConvolutions = List.of(c9, c10, c11, c12);

  public static void main (String[] args) throws IOException {
    logger = getLogger(dayOne.Main.class);

    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayFour.txt");

    logger.info("Part one: {}", partOne(input));
    logger.info("Part two: {}", partTwo(input));
  }

  public static boolean checkConvolution(List<String> input, String[] convolution, int row, int index) {
    int size = convolution.length;

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        char c = convolution[i].charAt(j);
        char compare = '.';
        try {
          compare = input.get(row + i).charAt(index + j);
        } catch (IndexOutOfBoundsException e) {
          // do nothing, treat as out of bounds and don't match
        }
        if (c != '.' && c != compare) {
          return false;
        }
      }
    }

    return true;
  }

  public static int solve(List<String> input, List<String[]> convolutions) {
    int sum = 0;
    for (int i = 0; i < input.size(); i++) {
      for (int j = 0; j < input.get(i).length(); j++) {
        for (String[] convolution : convolutions) {
          if (checkConvolution(input, convolution, i, j)) {
            sum++;
          }
        }
      }
    }
    return sum;
  }

  public static int partOne(List<String> input) {
    return solve(input, partOneConvolutions);
  }

  public static int partTwo(List<String> input) {
    return solve(input, partTwoConvolutions);
  }
}

