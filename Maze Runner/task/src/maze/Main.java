package maze;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static maze.Maze.*;

public class Main {
    static Scanner scan = new Scanner(System.in);
    static Maze maze;

    static String filepath = "C:\\Users\\dobrz\\Desktop\\Maze Runner\\Maze Runner\\task\\src\\maze\\";

    static List<String> loadedMaze = new ArrayList<>();
    static boolean isLoaded;


    public static void main(String[] args) {
        System.out.println("=== Menu ===\n" +
                "1. Generate a new maze\n" +
                "2. Load a maze\n" +
                "0. Exit");
        int answer = scan.nextInt();
        switch (answer) {
            case 1:
                generateNewMaze();
                SecondMenu();
                break;
            case 2:
                LoadMaze();
                SecondMenu();
                break;
            case 0:
                Exit();
                break;
        }
    }

    public static void SecondMenu() {
        System.out.println("=== Menu ===\n" +
                "1. Generate a new maze\n" +
                "2. Load a maze\n" +
                "3. Save the maze\n" +
                "4. Display the maze\n" +
                "5. Find the escape\n" +
                "0. Exit");

        int answer = scan.nextInt();
        switch (answer) {
            case 1:
                generateNewMaze();
                SecondMenu();
                break;
            case 2:
                LoadMaze();
                SecondMenu();
                break;
            case 3:
                SaveMaze();
                SecondMenu();
                break;
            case 4:
                DisplayMaze();
                SecondMenu();
                break;
            case 5:
                solve();
                SecondMenu();
                break;
            case 0:
                Exit();
                break;
        }
    }

    private static void generateNewMaze() {
        isLoaded=false;
        System.out.println("Enter the size of a new maze");
        Scanner scan = new Scanner(System.in);
        String inputString = scan.nextLine();
        String[] split = inputString.split("\\s+");
        int rows = Integer.parseInt((split[0]));
        int columns;
        if (split.length == 1) {
            columns = rows;
        } else {
            columns = Integer.parseInt((split[1]));
        }
        maze = new Maze(rows, columns);
    }

    private static void LoadMaze() {
        Scanner scan = new Scanner(System.in);
        String fileName = scan.nextLine();
        String fullPath = filepath + fileName;

        try {
            File file = new File(fullPath);
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                loadedMaze.add(input.nextLine());
            }
            isLoaded=true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void Exit() {
        System.out.println("Bye!");
        System.exit(0);
    }

    private static void SaveMaze() {
        Scanner scan = new Scanner(System.in);
        String fileName = scan.nextLine();
        File file = new File(filepath + fileName);

        try (FileWriter writer = new FileWriter(file)) {
            for (char[] row : wallMaze) {
                for (char block : row) {
                    if (block == '1') {
                        writer.write(wall);
                    } else {
                        writer.write(space);
                    }
                }
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
        }
    }

    private static void DisplayMaze() {
        for (String s : loadedMaze) {
            System.out.println(s);
        }
    }
}
