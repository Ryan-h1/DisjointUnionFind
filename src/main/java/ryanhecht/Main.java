package ryanhecht;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

  private static final int WIDTH = 75;
  private static final int HEIGHT = 72;
  private static final int[][] DIRECTIONS = {
    {0, -1}, {1, -1}, {1, 0}, {1, 1},
    {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}
  };

  public static int computeNodeValue(int x, int y, int width) {
    return y * width + x;
  }

  public static char[][] convertTextFileToCharacterGrid(String filename, int width, int height)
      throws IOException {
    String currentDirectory = System.getProperty("user.dir");
    System.out.println("Current working directory: " + currentDirectory);

    BufferedReader reader = new BufferedReader(new FileReader(filename));
    char[][] grid = new char[height][width];

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        grid[i][j] = ' ';
      }
    }

    for (int i = 0; i < height; i++) {
      String line = reader.readLine();
      if (line == null) break;
      for (int j = 0; j < width && j < line.length(); j++) {
        if (line.charAt(j) != '\0') {
          grid[i][j] = line.charAt(j);
        }
      }
    }
    reader.close();
    return grid;
  }

  public static uandf createUnionFindFromCharacterGrid(
      char[][] grid, int[][] directions, int width, int height) {
    uandf unionAndFind = new uandf(width * height);

    System.out.println("Grid height " + grid.length + " and width " + grid[0].length);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (grid[y][x] == '+') {
          int currentNodeValue = computeNodeValue(x, y, width);
          for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
              if (grid[newY][newX] == '+') {
                int neighborNodeValue = computeNodeValue(newX, newY, width);
                unionAndFind.union_sets(currentNodeValue, neighborNodeValue);
              }
            }
          }
        }
      }
    }

    return unionAndFind;
  }

  public static void main(String[] args) {
    char[][] grid = new char[HEIGHT][WIDTH];

    try {
      String filePath = "src/main/resources/face.img.txt";
      grid = convertTextFileToCharacterGrid(filePath, WIDTH, HEIGHT);
    } catch (IOException e) {
      System.err.println("Error occurred: " + e.getMessage());
      e.printStackTrace();
    }

    uandf unionAndFind = createUnionFindFromCharacterGrid(grid, DIRECTIONS, WIDTH, HEIGHT);
    unionAndFind.final_sets();

    System.out.println("1. Print the grid: \n");
    for (char[] row : grid) {
      for (char cell : row) {
        System.out.print(cell);
      }
      System.out.println();
    }

    // Second pass: replace '+' with unique ID of the component
    for (int y = 0; y < HEIGHT; ++y) {
      for (int x = 0; x < WIDTH; ++x) {
        if (grid[y][x] == '+') {
          int nodeValue = computeNodeValue(x, y, WIDTH);
          int root = unionAndFind.find_set(nodeValue);
          char componentId = (char) (root + '0');
          grid[y][x] = componentId;
        }
      }
    }

    System.out.println("\n2. Print the modified grid: \n");
    for (char[] row : grid) {
      for (char cell : row) {
        System.out.print(cell);
      }
      System.out.println();
    }
  }
}
