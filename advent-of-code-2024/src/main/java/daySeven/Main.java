package daySeven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public static enum Operations {
    ADDITION('+'),
    MULTIPLICATION('*');
    char symbol;
    Operations(char symbol) {
      this.symbol = symbol;
    }
  }

  public static class Equation {
    public long value;
    public List<Long> operands;
    public List<Operations> operations = new ArrayList<>();

    public Equation(long value, List<Long> operands) {
      this.value = value;
      this.operands = operands;
    }

    public Equation(long value, long a, long b, Operations op) {
      this.value = value;
      this.operands = List.of(a, b);
      this.operations.add(op);
    }

    public Equation(Equation e) {
      this.value = e.value;
      this.operands = new ArrayList<>(e.operands);
      this.operations = new ArrayList<>(e.operations);
    }

    public void addOperation(Operations op) {
      this.operations.add(op);
    }

    public String toString() {
      if (operands.size() - 1 != operations.size()) {
        return "Not enough operations.";
      }
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < operands.size(); i++) {
        sb.append(operands.get(i));
        if (i != operands.size() - 1) {
          sb.append(operations.get(i).symbol);
        }
      }
      return sb.toString();
    }

    public long evaluate() {
      long result = operands.get(0);
      for (int i = 0; i < operands.size() - 1; i++) {
        if (operations.get(i) == Operations.ADDITION) {
          result += operands.get(i + 1);
        } else {
          result *= operands.get(i + 1);
        }
      }
      return result;
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(daySix.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/daySeven.txt");
    List<Equation> equations = new ArrayList<>();

    for (String line : input) {
      String[] split = line.split(":");
      long value = Long.parseLong(split[0]);

      String[] unparsedOperands = split[1].split(" ");
      List<Long> operands = Arrays.stream(Arrays.stream(unparsedOperands)
                             .filter(s -> !s.isEmpty())
                             .mapToLong(Long::parseLong).toArray()).boxed().toList();

      equations.add(new Equation(value, operands));
    }

    logger.info("Part one: {}", partOne(equations));

  }

  public static List<Equation> generateSolutions(Equation eq) {
    // base case
    if (eq.operands.size() == 2) {
      List<String> base = new ArrayList<>();
      long a = eq.operands.get(0);
      long b = eq.operands.get(1);
      Equation eq1 = new Equation(eq.value, a, b, Operations.ADDITION);
      Equation eq2 = new Equation(eq.value, a, b, Operations.MULTIPLICATION);
      return List.of(eq1, eq2);
    }

    List<Long> lessOps = eq.operands.subList(0, eq.operands.size() - 1);
    Equation subEq = new Equation(eq.value, lessOps);

    List<Equation> solutions = generateSolutions(subEq);
    List<Equation> result = new ArrayList<>();

    for (Equation e : solutions) {
      Equation eq1 = new Equation(e);
      Equation eq2 = new Equation(e);
      eq1.addOperation(Operations.ADDITION);
      eq2.addOperation(Operations.MULTIPLICATION);
      eq1.operands.add(eq.operands.get(eq.operands.size() - 1));
      eq2.operands.add(eq.operands.get(eq.operands.size() - 1));
      result.add(eq1);
      result.add(eq2);
    }

    return result;
  }

  public static long partOne(List<Equation> equations) {
    long sum = 0;

    for (Equation eq : equations) {
      for (Equation solution : generateSolutions(eq)) {

        if (solution.evaluate() == solution.value) {
          logger.info(solution);
          logger.warn(solution.evaluate());
          sum += solution.evaluate();
          break;
        }
      }
    }

    return sum;
  }

  public static long partTwo() {
    return 0;
  }

}
