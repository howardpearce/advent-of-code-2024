package daySeven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public enum Operations {
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

    // resolve the equation to get its calculated value
    public long evaluate() {
      // copy it so that we don't modify the original equation (for debugging purposes)
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

  // use recursion to generate all permutations of possible equations
  public static List<Equation> generateSolutions(Equation eq, List<Operations> ops) {
    // base case
    if (eq.operands.size() == 2) {
      List<Equation> eqs = new ArrayList<>();
      for(Operations op : ops) {
        eqs.add(new Equation(eq.value, eq.operands.get(0), eq.operands.get(1), op));
      }
      return eqs;
    }

    List<Long> lessOps = eq.operands.subList(0, eq.operands.size() - 1);
    Equation subEq = new Equation(eq.value, lessOps);

    List<Equation> solutions = generateSolutions(subEq, ops);
    List<Equation> result = new ArrayList<>();

    for (Equation e : solutions) {
      for (Operations op : ops) {
        Equation tempEq = new Equation(e);
        tempEq.addOperation(op);
        tempEq.operands.add(eq.operands.get(eq.operands.size() - 1));
        result.add(tempEq);
      }
    }

    return result;
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
      List<Long> operands = Arrays.stream(Arrays.stream(unparsedOperands).filter(s -> !s.isEmpty()).mapToLong(Long::parseLong).toArray()).boxed().toList();

      equations.add(new Equation(value, operands));
    }

    logger.info("Part one: {}", solve(equations, List.of(Operations.ADDITION, Operations.MULTIPLICATION)));
    logger.info("Part two: {}", solve(equations, List.of(Operations.ADDITION, Operations.MULTIPLICATION, Operations.CONCATENATION)));

  }

  public static long solve(List<Equation> equations, List<Operations> ops) {
    long sum = 0;

    for (Equation eq : equations) {
      for (Equation solution : generateSolutions(eq, ops)) {
        long eval = solution.evaluate();
        if (eval == solution.value) {
          sum += eval;
          break;
        }
      }
    }

    return sum;
  }

}
