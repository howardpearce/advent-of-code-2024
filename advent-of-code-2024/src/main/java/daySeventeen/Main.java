package daySeventeen;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;

  public enum Instruction {
    ADV(0),
    BXL(1),
    BST(2),
    JNZ(3),
    BXC(4),
    OUT(5),
    BDV(6),
    CDV(7);

    public final int opcode;

    Instruction(int opcode) {
      this.opcode = opcode;
    }

    public static Instruction fromOpcode(int opcode) {
      for (Instruction i : values()) {
        if (i.opcode == opcode) {
          return i;
        }
      }
      return null;
    }
  }

  public static class Computer {
    public long A;
    public long B;
    public long C;
    public List<Integer> program;
    private boolean next = true;
    private int pointer = 0;
    public List<Long> output = new ArrayList<>();

    public Computer(long A, long B, long C, List<Integer> program) {
      this.A = A;
      this.B = B;
      this.C = C;
      this.program = program;
    }

    public String toString() {
      return String.format("A: %d, B: %d, C: %d, Program: %s", A, B, C, program.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }

    public List<Long> getOutput() {
      return output;
    }

    public void runProgram() {
      while (pointer < program.size()) {
        Instruction i = Instruction.fromOpcode(program.get(pointer));
        if (i == null) { logger.error("Null instruction"); return; }
        performInstruction(i, program.get(pointer + 1));
        if (next) {
          pointer+=2;
        } else {
          next = true;
        }
      }
    }

    public long getComboOperand(long operand) throws IllegalArgumentException {
      if (operand >= 0 && operand <= 3) { return operand; }
      if (operand == 4) { return A; }
      if (operand == 5) { return B; }
      if (operand == 6) { return C; }

      logger.error("Invalid operand received: {}", operand);
      return -1;
    }

    public void performInstruction(Instruction instruction, int operand) {
      switch (instruction) {
        case ADV:
          A = (long) (A / Math.pow(2, getComboOperand(operand)));
          break;
        case BXL:
          B = B ^ operand;
          break;
        case BST:
          B = (getComboOperand(operand) % 8);
          break;
        case JNZ:
          if (A != 0) {
            pointer = operand;
            next = false;
          }
          break;
        case BXC:
          B = B ^ C;
          break;
        case OUT:
          output.add(getComboOperand(operand) % 8);
          break;
        case BDV:
          B = (long) (A / Math.pow(2, getComboOperand(operand)));
          break;
        case CDV:
          C = (long) (A / Math.pow(2, getComboOperand(operand)));
          break;
        default:
          logger.error("Unsupported instruction");
          break;
      }
    }
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(daySeventeen.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/daySeventeen.txt");

    int A = Integer.parseInt(input.get(0).substring(12));
    int B = Integer.parseInt(input.get(1).substring(12));
    int C = Integer.parseInt(input.get(2).substring(12));

    List<Integer> program = Arrays.stream(
        input.get(4).substring(9).split(",")
    ).map(Integer::valueOf).toList();

    logger.info("Part one: " + partOne(new Computer(A, B, C, program)));
    logger.info("Part two: " + partTwo(new Computer(A, B, C, program)));
  }

  public static String partOne(Computer comp) {
    comp.runProgram();
    return comp.getOutput().stream().map(String::valueOf).collect(Collectors.joining(","));
  }


  // I wanted to do this in a loop but there can multiple solutions for a given index which requires you to check them
  public static long solve(long value, int index, Computer comp) {
    if (index == -1) { return value; }

    for (int i = 0; i < 8; i++) {
      long next = (value << 3) + i;
      Computer newComp = new Computer(next, comp.B, comp.C, comp.program);
      newComp.runProgram();
      if (newComp.getOutput().getFirst().equals(Long.valueOf(comp.program.get(index)))) {
        long result = solve(next, index - 1, comp);
        if (result != -1) { return result; }
      }
    }
    return -1;
  }

  public static long partTwo(Computer comp) {
    return solve(0, comp.program.size() - 1, comp);
  }

}
