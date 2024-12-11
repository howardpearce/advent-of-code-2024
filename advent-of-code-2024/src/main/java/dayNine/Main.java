package dayNine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {
  static Logger logger;
  static final FileBlock FREE_BLOCK = new FileBlock(-1, 0, 0);

  public record FileBlock (long id, int size, int seq) { }

  public static int findFreeBlocksThatFit(ArrayList<FileBlock> expanded, FileBlock file) {
    int size = 0;
    for (int i = 0; i < expanded.size(); i++) {
      if (expanded.get(i).equals(FREE_BLOCK)) {
        size++;
      } else {
        size = 0;
      }
      if (size == file.size) {
        return i - (file.size - 1);
      }
    }
    return -1;
  }

  public static long checksum(ArrayList<FileBlock> expanded) {
    long sum = 0;
    for (int i = 0; i < expanded.size(); i++) {
      if (!expanded.get(i).equals(FREE_BLOCK)) {
        sum += i * (expanded.get(i).id);
      }
    }
    return sum;
  }

  public static void main (String[] args) throws IOException {
    logger = utils.utilities.getLogger(dayNine.Main.class);
    List<String> input = utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayNine.txt");
    char[] chars = input.get(0).toCharArray();

    int id = 1;
    ArrayList<FileBlock> expanded = new ArrayList<>();
    // expand the free blocks on the disk
    for (int i = 0; i < chars.length; i++ ) {
      int num = Character.getNumericValue(chars[i]);
      if (i % 2 != 0) {
        for (int j = 0; j < num; j++) {
          expanded.add(expanded.size(), FREE_BLOCK);
        }
      } else {
        for (int j = 0; j < num; j++) {
          expanded.add(new FileBlock(id-1, num, j));
        }
        id++;
      }
    }

    logger.info("Part one: {}", partOne(new ArrayList<>(expanded)));
    logger.info("Part two: {}", partTwo(new ArrayList<>(expanded)));
  }

  public static long partOne(ArrayList<FileBlock> expanded) {
    // move blocks from right to left where free blocks exist
    int right = expanded.size() - 1;
    int left = 1;
    while (left < right) {
      while (left < right && expanded.get(right) == FREE_BLOCK) { right--; }
      while (left < right && expanded.get(left) != FREE_BLOCK) { left++; }
      if (left < right) {
        FileBlock temp = expanded.get(left);
        expanded.set(left, expanded.get(right));
        expanded.set(right, temp);
        right--;
        left++;
      }
    }

    return checksum(expanded);
  }

  public static long partTwo(ArrayList<FileBlock> expanded) {
    for (int i = expanded.size() - 1; i >= 0; i--) {
      FileBlock file = expanded.get(i);
      if (file.equals(FREE_BLOCK) || file.seq != file.size - 1) { continue; }

      int index = findFreeBlocksThatFit(expanded, file);
      if (index != -1 && index < i) {
        for (int j = index; j < index + file.size; j++) {
          expanded.set(j, expanded.get(i - (j - index)));
          expanded.set(i - (j - index), FREE_BLOCK);
        }
      }
    }

    return checksum(expanded);
  }

}
