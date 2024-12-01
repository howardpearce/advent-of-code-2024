package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;

public class utilities {
  public static Logger getLogger(Class c) throws IOException {
    return LogManager.getLogger(c);
  }

  public static ArrayList<String> getInput(String filepath) throws IOException {
    File inputFile = new File(filepath);

    if (inputFile.exists()) {
      ArrayList<String> input = new ArrayList<>();
      Scanner scan = new Scanner(inputFile);
      while (scan.hasNextLine()) {
        input.add(scan.nextLine());
      }
      return input;
    } else {
      throw new FileNotFoundException(String.format("File '%s' not found", filepath));
    }
  }

}
