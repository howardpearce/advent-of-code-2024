package dayFive;

import static utils.utilities.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.Logger;
import utils.utilities;

public class Main {

  static Logger logger;
  static List<List<Integer>> goodBooks = new ArrayList<>();
  static List<List<Integer>> badBooks = new ArrayList<>();

  static List<Pair> rules = new ArrayList<Pair>();

  public record Pair ( int before, int after) {}

  public static void main (String[] args) throws IOException {
    logger = getLogger(dayOne.Main.class);

    Queue<String> input = new LinkedList<>(utilities.getInput(System.getProperty("user.dir") + "/src/main/resources/dayFive.txt"));
    List<List<Integer>> books = new ArrayList<>();

    while (input.peek() != null) {
      String line = input.poll();
      if (line.isEmpty()) break;
      String[] split = line.split("\\|");
      Pair p = new Pair(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
      rules.add(p);
    }

    while (input.peek() != null) {
      List<Integer> page = Arrays.stream(input.poll().split(",")).mapToInt(Integer::parseInt).boxed().toList();
      books.add(page);
    }

    for (List<Integer> book: books) {
      if (checkBook(book) == null) {
        goodBooks.add(book);
      } else {
        badBooks.add(book);
      }
    }

    logger.info("Part one: {}", partOne(goodBooks));
    logger.info("Part two: {}", partTwo(badBooks));
  }


  public static Pair checkBook(List<Integer> book) {
    for (int page : book) {
      Pair p;
      if ((p = getBreakingRule(page, book)) != null) {
        return p;
      }
    }
    return null;
  }

  public static Pair getBreakingRule(int page, List<Integer> book) {
    for (Pair p : rules) {
      if (p.before() == page || p.after() == page) {
        int beforeIndex = book.indexOf(p.before());
        int afterIndex = book.indexOf(p.after());
        if (afterIndex == -1 || beforeIndex == -1 ) continue;
        if (beforeIndex > afterIndex) {
          return p;
        }
      }
    }

    return null;
  }

  public static int partOne(List<List<Integer>> books) {
    int sum = 0;
    for (List<Integer> book : books) {
      sum += book.get(((book.size() - 1) / 2));
    }
    return sum;
  }

  public static int partTwo(List<List<Integer>> books) {
    int sum = 0;
    for (List<Integer> book : books) {
      Pair p;
      while ((p = checkBook(book)) != null) {
        // could do this recursively
        int beforeIndex = book.indexOf(p.before());
        int afterIndex = book.indexOf(p.after());
        List<Integer> tempBook = new ArrayList<>(book);
        tempBook.set(beforeIndex, book.get(afterIndex));
        tempBook.set(afterIndex, book.get(beforeIndex));
        book = tempBook;
      }
      sum += book.get(((book.size() - 1) / 2));
    }
    return sum;
  }
}
