package ryanhecht;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == '+') {
                    unionAndFind.make_set(computeNodeValue(x, y, width));
                }
            }
        }

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

        Map<Integer, Character> compressedRep = unionAndFind.getCompressedRep();

        // Second pass: replace '+' with unique ID of the component
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < WIDTH; ++x) {
                if (grid[y][x] == '+') {
                    int nodeValue = computeNodeValue(x, y, WIDTH);
                    int set = unionAndFind.find_set(nodeValue);
                    char componentId = compressedRep.get(set);
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

        System.out.println("\n3. Print the sizes and labels of the components: \n");
        for (Integer root : compressedRep.keySet()) {
            String key = unionAndFind.getUniqueRootsCountMap().get(root).toString();
            String value = compressedRep.get(root).toString();
            System.out.println("Size: " + key + "         label: " + value);
        }

        System.out.println("\n4. Same as 2, but only the connected components whose sizes are greater than 2: \n");
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < WIDTH; ++x) {
                if (grid[y][x] != ' ') {
                    int set = unionAndFind.find_set(computeNodeValue(x, y, WIDTH));
                    if (unionAndFind.getUniqueRootsCountMap().get(set) > 2) {
                        System.out.print(grid[y][x]);
                    } else {
                        System.out.print(' ');
                    }
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }

        System.out.println("\n5. Same as 2, but only the connected components whose sizes are greater than 11: \n");
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < WIDTH; ++x) {
                if (grid[y][x] != ' ') {
                    int set = unionAndFind.find_set(computeNodeValue(x, y, WIDTH));
                    if (unionAndFind.getUniqueRootsCountMap().get(set) > 11) {
                        System.out.print(grid[y][x]);
                    } else {
                        System.out.print(' ');
                    }
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
    }
}
