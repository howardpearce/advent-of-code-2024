package dayThirteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public record Pair(long x, long y) {}
  public record Machine(Pair prize, Pair a, Pair b) {}

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayThirteen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayThirteen.txt");

    List<Machine> machines = new ArrayList<>();

    for (int i = 0; i < input.size(); i+=4) {
      Pair a = parsePairFromInput(input.get(i), "+");
      Pair b = parsePairFromInput(input.get(i+1), "+");
      Pair prize = parsePairFromInput(input.get(i+2), "=");
      machines.add(new Machine(prize, a, b));
    }

    logger.info("Part one: {}", partOne(machines));
    logger.info("Part two: {}", partTwo(machines));

  }

  public static Pair parsePairFromInput(String input, String delimiter) {
    String[] split = input.split(":")[1].split(",");
    long xCoefficient = Long.parseLong(split[0].substring(split[0].trim().indexOf(delimiter)+2));
    long yCoefficient = Long.parseLong(split[1].substring(split[1].trim().indexOf(delimiter)+2));
    return new Pair(xCoefficient, yCoefficient);
  }

  public static Pair solveLinearSystem(long a1, long b1, long c1, long a2, long b2, long c2) {
    // Calculate the determinant of the coefficient matrix
    long determinant = a1 * b2 - a2 * b1;

    // no solution
    if (determinant == 0) { return null; }

    // Calculate determinants for x and y using Cramer's Rule
    long determinantX = c1 * b2 - c2 * b1;
    long determinantY = a1 * c2 - a2 * c1;

    // only want whole numbers
    if (determinantX % determinant != 0 || determinantY % determinant != 0) { return null; }

    return new Pair((determinantX / determinant), (determinantY / determinant));
  }

  public static long partOne(List<Machine> machines) {
    long sum = 0;
    for (Machine machine : machines) {
      Pair solution = solveLinearSystem(machine.a.x, machine.b.x, machine.prize.x, machine.a.y, machine.b.y, machine.prize.y);
      if (solution != null) { sum += 3*solution.x + solution.y; }
    }
    return sum;
  }

  // This is a very silly part two.
  public static long partTwo(List<Machine> machines) {
    long sum = 0;
    for (Machine machine : machines) {
      long newPrizeX = 10000000000000L + machine.prize.x;
      long newPrizeY = 10000000000000L + machine.prize.y;
      Pair solution = solveLinearSystem(machine.a.x, machine.b.x, newPrizeX, machine.a.y, machine.b.y, newPrizeY);
      if (solution != null) { sum += 3*solution.x + solution.y; }
    }
    return sum;
  }

}

