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
    MULTIPLICATION('*'),
    CONCATENATION('|');
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
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < operands.size(); i++) {
        sb.append(operands.get(i));
        try {
          sb.append(operations.get(i).symbol);
        } catch (IndexOutOfBoundsException e) {
          // do nothing
        }
      }
      return sb.toString();
    }

    public long evaluate() {
      Equation copy = new Equation(this);
      long result = copy.operands.getFirst();
      copy.operands.removeFirst();
      while(!copy.operations.isEmpty()) {
        if (copy.operations.getFirst() == Operations.ADDITION) {
          result += copy.operands.getFirst();
        } else if (copy.operations.getFirst() == Operations.MULTIPLICATION) {
          result *= copy.operands.getFirst();
        } else {
          long value = Long.parseLong(String.valueOf(result) + String.valueOf(copy.operands.getFirst()));
          if (copy.operands.size() == 1) {
            return value;
          }
          copy.operands.set(0, value);
          result = value;
        }
        copy.operations.removeFirst();
        copy.operands.removeFirst();
      }
      return result;
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(daySix.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/daySeven.txt");
    List<Equation> equations = new ArrayList<>();

    for (String line : input) {
      if (line.isEmpty()) { continue; }

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
      Equation eq3 = new Equation(eq.value, a, b, Operations.CONCATENATION);
      return List.of(eq1, eq2, eq3);
    }

    List<Long> lessOps = eq.operands.subList(0, eq.operands.size() - 1);
    Equation subEq = new Equation(eq.value, lessOps);

    List<Equation> solutions = generateSolutions(subEq);
    List<Equation> result = new ArrayList<>();

    for (Equation e : solutions) {
      Equation eq1 = new Equation(e);
      Equation eq2 = new Equation(e);
      Equation eq3 = new Equation(e);
      eq1.addOperation(Operations.ADDITION);
      eq2.addOperation(Operations.MULTIPLICATION);
      eq3.addOperation(Operations.CONCATENATION);
      eq1.operands.add(eq.operands.get(eq.operands.size() - 1));
      eq2.operands.add(eq.operands.get(eq.operands.size() - 1));
      eq3.operands.add(eq.operands.get(eq.operands.size() - 1));
      result.add(eq1);
      result.add(eq2);
      result.add(eq3);
    }

    return result;
  }

  public static long partOne(List<Equation> equations) {
    long sum = 0;

    for (Equation eq : equations) {
      for (Equation solution : generateSolutions(eq)) {
        long eval = solution.evaluate();
        if (eval == solution.value) {
          logger.info(eval + "=" + solution);
          sum += eval;
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
