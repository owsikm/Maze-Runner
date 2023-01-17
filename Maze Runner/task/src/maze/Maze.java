package maze;
import java.util.*;
import static maze.Main.isLoaded;
import static maze.Main.loadedMaze;


class Maze {
    public static final String wall = "\u2588\u2588";
    public static final String space = "  ";
    private static int rows;
    private static int columns;

    private static int exitX;
    private static int exitY;

    private static char[][] maze;
    public static char[][] wallMaze;
    static ArrayList<int[]> exits = new ArrayList<>();
    static int[] entrance;
    static int[] exit;

    Maze(int rows, int columns) {
        Maze.rows = rows;
        Maze.columns = columns;

        if (rows <= 4 || columns <= 4) {
            System.out.println("Too low maze size.");
        } else {
            maze = generateMaze();
            wallify();

            makeExitTunnel('F', 'S');
            makeExitTunnel('E', 'T');
            wallMaze = Arrays.copyOf(wallMaze, wallMaze.length - 2);

            exits();
            printMaze();
            System.out.println();
        }
    }

    static void printMaze() {
        for (char[] row : wallMaze) {
            for (char block : row) {
                if (block == '1') {
                    System.out.print(wall);
                } else {
                    System.out.print(space);
                }
            }
            System.out.println();
        }
    }


    private static boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= rows || col < 0 || col >= columns;
    }

    private static void directExit(String direction) {
        switch (direction) {
            case "R":
                exitY++;
                break;
            case "L":
                exitY--;
                break;
            case "T":
                exitX--;
                break;
            case "B":
                exitX++;
                break;
        }
    }

    private static void makeExitTunnel(char in, char out) {
        final char[][] matrix = wallMaze;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == in) {
                    exitX = i;
                    exitY = j;
                }
            }
        }

        int bounds = 1;
        String direction;

        while (true) {
            if (isOutOfBounds(exitX, exitY + bounds)) {
                direction = "R";
                break;
            } else if (isOutOfBounds(exitX, exitY - bounds)) {
                direction = "L";
                break;
            } else if (isOutOfBounds(exitX - bounds, exitY)) {
                direction = "T";
                break;
            } else if (isOutOfBounds(exitX + bounds, exitY)) {
                direction = "B";
                break;
            }
            bounds++;
        }
        bounds--;

        while (bounds > 1) {
            directExit(direction);
            wallMaze[exitX][exitY] = '0';
            bounds--;
        }
        directExit(direction);
        wallMaze[exitX][exitY] = out;
        exits.add(new int[]{exitX, exitY});
    }

    public static void exits() {
        entrance = (exits.get(exits.size() - 1));
        exit = (exits.get(exits.size() - 2));
    }

    private static <T> T[] addStartingElement(T[] elements, T element) {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }

    private static <T> T[] append(T[] array, T value) {
        T[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = value;
        return result;
    }

    private void wallify() {
        char[] wallHorizontal = new char[columns - 2];
        Arrays.fill(wallHorizontal, '1');

        maze = addStartingElement(maze, wallHorizontal);
        maze = append(maze, wallHorizontal);
        String tempRow;
        char[] toReplace;
        char[][] tempMaze = new char[rows + 2][columns + 2];
        for (int i = 0; i < maze.length; i++) {
            tempRow = "1" + String.copyValueOf(maze[i]) + "1";
            toReplace = tempRow.toCharArray();
            tempMaze[i] = toReplace;
        }

        wallMaze = tempMaze;
    }

    public char[][] generateMaze() {
        int r = rows - 2, c = columns - 2;

        StringBuilder s = new StringBuilder(c);
        s.append("1".repeat(Math.max(0, c)));
        char[][] maz = new char[r][c];
        for (int x = 0; x < r; x++) maz[x] = s.toString().toCharArray();

        Point st = new Point((int) (Math.random() * r), (int) (Math.random() * c), null);
        maz[st.r][st.c] = 'F';

        ArrayList<Point> frontier = new ArrayList<>();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || x != 0 && y != 0)
                    continue;
                try {
                    if (maz[st.r + x][st.c + y] == '0') continue;
                } catch (Exception e) {
                    continue;
                }
                frontier.add(new Point(st.r + x, st.c + y, st));
            }

        Point last = null;
        while (!frontier.isEmpty()) {
            Point cu = frontier.remove((int) (Math.random() * frontier.size()));
            Point op = cu.opposite();
            try {
                if (maz[cu.r][cu.c] == '1') {
                    if (maz[op.r][op.c] == '1') {

                        maz[cu.r][cu.c] = '0';
                        maz[op.r][op.c] = '0';
                        last = op;
                        for (int x = -1; x <= 1; x++)
                            for (int y = -1; y <= 1; y++) {
                                if (x == 0 && y == 0 || x != 0 && y != 0)
                                    continue;
                                try {
                                    if (maz[op.r + x][op.c + y] == '0') continue;
                                } catch (Exception e) {
                                    continue;
                                }
                                frontier.add(new Point(op.r + x, op.c + y, op));
                            }
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                if (frontier.isEmpty()) {
                    assert last != null;
                    maz[last.r][last.c] = 'E';
                }
            } catch (Exception e) {
                System.exit(0);
            }
        }
        return maz;
    }

    public static class Point {
        Integer r;
        Integer c;
        Point parent;

        public Point(int x, int y, Point p) {
            r = x;
            c = y;
            parent = p;
        }

        public Point opposite() {
            if (this.r.compareTo(parent.r) != 0)
                return new Point(this.r + this.r.compareTo(parent.r), this.c, this);
            if (this.c.compareTo(parent.c) != 0)
                return new Point(this.r, this.c + this.c.compareTo(parent.c), this);
            return null;
        }
    }

    static void solve() {
        String[][] arrayMaze = new String[rows][columns];

        int stepX;
        int stepY;
        if (isLoaded) {
            rows = loadedMaze.toArray().length;
            columns = loadedMaze.get(0).length() / 2;
            arrayMaze = new String[rows][columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns * 2; j++) {
                    if (j % 2 == 0 && loadedMaze.get(i).charAt(j) == '█') {
                        arrayMaze[i][j / 2] = "1";
                    } else if (j % 2 != 0) {//exclude
                    } else arrayMaze[i][j / 2] = "0";
                }
            }

            ArrayList<int[]> exitsLoad = new ArrayList<>();
            boolean visited1 = false;
            boolean visited2 = false;
            boolean visited3 = false;
            boolean visited4 = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (arrayMaze[i][0].equals("0") && !visited1) {
                        exitsLoad.add(new int[]{i, 0});
                        visited1 = true;
                    }
                    if (arrayMaze[i][columns - 1].equals("0") && !visited2) {
                        exitsLoad.add(new int[]{i, columns - 1});
                        visited2 = true;
                    }
                    if (arrayMaze[0][j].equals("0") && !visited3) {
                        exitsLoad.add(new int[]{0, j});
                        visited3 = true;
                    }
                    if (arrayMaze[rows - 1][j].equals("0") && !visited4) {
                        exitsLoad.add(new int[]{rows - 1, j});
                        visited4 = true;
                    }
                }
            }
            entrance = exitsLoad.get(0);
            exit = exitsLoad.get(1);
        } else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (Character.isDigit(wallMaze[i][j])) {
                        arrayMaze[i][j] = String.valueOf(wallMaze[i][j] - 48);
                    } else arrayMaze[i][j] = "0";
                }
            }
        }
        stepY = entrance[0];
        stepX = entrance[1];
        boolean[][] visitedCrossroad = new boolean[rows][columns];
        boolean[][] visitedNode = new boolean[rows][columns];
        visitedNode[stepY][stepX] = true;
        arrayMaze[entrance[0]][entrance[1]] = "//";
        Deque<int[]> crossroads = new ArrayDeque<>();
        Deque<int[]> stack = new ArrayDeque<>();
        while (stepY != exit[0] || stepX != exit[1]) {
            int crossroad = 0;
            if (!isOutOfBounds(stepY + 1, stepX) && arrayMaze[stepY + 1][stepX].equals("0") && !visitedCrossroad[stepY + 1][stepX]) {
                crossroad++;
                visitedCrossroad[stepY + 1][stepX] = true;

            }
            if (!isOutOfBounds(stepY - 1, stepX) && arrayMaze[stepY - 1][stepX].equals("0") && !visitedCrossroad[stepY - 1][stepX]) {
                crossroad++;
                visitedCrossroad[stepY - 1][stepX] = true;
            }
            if (!isOutOfBounds(stepY, stepX + 1) && arrayMaze[stepY][stepX + 1].equals("0") && !visitedCrossroad[stepY][stepX + 1]) {
                crossroad++;
                visitedCrossroad[stepY][stepX + 1] = true;
            }
            if (!isOutOfBounds(stepY, stepX - 1) && arrayMaze[stepY][stepX - 1].equals("0") && !visitedNode[stepY][stepX - 1]) {
                crossroad++;
                visitedCrossroad[stepY][stepX - 1] = true;
            }

            if (!isOutOfBounds(stepY + 1, stepX) && arrayMaze[stepY + 1][stepX].equals("0") && !visitedNode[stepY + 1][stepX]) {
                visitedNode[stepY + 1][stepX] = true;
                stack.push(new int[]{stepY, stepX});
                stepY++;
            } else if (!isOutOfBounds(stepY - 1, stepX) && arrayMaze[stepY - 1][stepX].equals("0") && !visitedNode[stepY - 1][stepX]) {
                visitedNode[stepY - 1][stepX] = true;
                stack.push(new int[]{stepY, stepX});
                stepY--;
            } else if (!isOutOfBounds(stepY, stepX + 1) && arrayMaze[stepY][stepX + 1].equals("0") && !visitedNode[stepY][stepX + 1]) {
                visitedNode[stepY][stepX + 1] = true;
                stack.push(new int[]{stepY, stepX});
                stepX++;
            } else if (!isOutOfBounds(stepY, stepX - 1) && arrayMaze[stepY][stepX - 1].equals("0") && !visitedNode[stepY][stepX - 1]) {
                visitedNode[stepY][stepX - 1] = true;
                stack.push(new int[]{stepY, stepX});
                stepX--;
            } else {
                int diff = (Math.abs(stepY - crossroads.getFirst()[0])) + (Math.abs(stepX - crossroads.getFirst()[1]));
                if (diff == 0) {
                    crossroads.pop();
                }
                stepY = crossroads.getFirst()[0];
                stepX = crossroads.getFirst()[1];
                while (stepY != stack.getFirst()[0] || stepX != stack.getFirst()[1]) {
                    stack.pop();
                }
            }
            if (crossroad > 1) {
                crossroads.push(stack.getFirst());
            }
        }
        int stackSize = stack.size();
        for (int i = 0; i < stackSize; i++) {
            arrayMaze[stack.getFirst()[0]][stack.getFirst()[1]] = "//";
            stack.pop();
        }
        arrayMaze[exit[0]][exit[1]] = "//";
        System.out.println(Arrays.deepToString(arrayMaze)
                .replace("], ", "]\n").replace(" ", "")
                .replace("[[", "[").replace("]]", "]")
                .replace(",", "").replace("[", "")
                .replace("0", "  ").replace("1", "██")
                .replace("]", ""));
    }
}

// The base for the Maze class was (with changes): https://github.com/YuraVolk/Hyperskill-Maze-Runner/blob/master/Maze.java


