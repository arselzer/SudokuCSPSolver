import constraint.Constraint;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSPSolver {
    Instance instance;

    public CSPSolver(Path cspFile) throws Exception {
        XCSPReader reader = new XCSPReader(cspFile.toString());

        instance = reader.getProblemInstance();

        int [][] sudokuGrid = getSudokuGridFromInstance(instance);

        printSudokuGrid(sudokuGrid);

        ArrayList<int[][]> solutions = new ArrayList<>();
        solveUsingBacktracking(sudokuGrid, solutions);

        for (int[][] s : solutions) {
            printSudokuGrid(s);
        }

//        for (Constraint c : instance.getConstraints()) {
//            System.out.println(c);
//        }
    }

    private void printSudokuGrid(int[][] grid) {
        printSudokuGrid(grid, false);
    }

    private void printSudokuGrid(int[][] grid, boolean fillEmptyValuesWithZeros) {
        System.out.println("|-------+-------+-------|");
        for (int x = 0; x < grid.length; x++) {
            System.out.print("| ");
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == 0 && !fillEmptyValuesWithZeros) {
                    System.out.print("  ");
                } else {
                    System.out.print(grid[x][y] + " ");
                }
                if (y % 3 == 2) {
                    System.out.print("| ");
                }
            }
            if (x % 3 == 2) {
                System.out.println();
                System.out.print("|-------+-------+-------|");
            }
            System.out.println();
        }
    }

    private int[][] getSudokuGridFromInstance(Instance instance) {
        int grid[][] = new int[9][9];

        for (Constraint v : instance.getConstraints()) {
            if (v.toString().contains("FIXED")) {
                int[] r = getSudokuValueFromPosition(v.toString());
                grid[r[0]][r[1]] = r[2];
            }
        }
        return grid;
    }

    private int[] getSudokuValueFromPosition(String constraint) {
        Pattern pattern = Pattern.compile("FIXED \\(x\\[([0-9])\\]\\[([0-9])\\] = ([0-9])\\)");
        Matcher matcher = pattern.matcher(constraint);
        if (matcher.find()) {
            return new int[]{Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))};
        }
        return null;
    }

    private boolean isPossible(int[][] grid, int positionRow, int positionColumn, int value) {
        // Check if value is already in row (if yes then immediately return false)
        for (int x = 0; x < grid.length; x++) {
            if (grid[x][positionColumn] == value) {
                return false;
            }
        }

        // Check if value is already in column (if yes then immediately return false)
        for (int y = 0; y < grid[positionRow].length; y++) {
            if (grid[positionRow][y] == value) {
                return false;
            }
        }

        // Check if value is already in square (if yes then immediately return false)
        int startSquareX = positionRow - (positionRow % 3);
        int startSquareY = positionColumn - (positionColumn % 3);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int currentX = startSquareX + x;
                int currentY = startSquareY + y;
                if (grid[currentX][currentY] == value && !(currentX== positionRow && currentY == positionColumn)) {
                    return false;
                }
            }
        }

        // If variable was not found, it is possible to be set here
        return true;
    }

    private int[][] getCopyOfSudokuGrid(int[][] grid) {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                copy[x][y] = grid[x][y];
            }
        }
        return copy;
    }

    public void solveUsingBacktracking(int[][] grid, ArrayList<int[][]> solutions) {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                // If the number at this point is not set yet, make a guess and check if it is possible
                if (grid[x][y] == 0) {
                    for (int guess = 1; guess < 10; guess++) {
                        if (isPossible(grid, x, y, guess)) {
                            grid[x][y] = guess;
                            solveUsingBacktracking(grid, solutions);
                            // Use backtracking if the choice was bad: reset the value and try again
                            grid[x][y] = 0;
                        }
                    }
                    // If all possibilities were checked, and none of them worked we are at a dead end and return
                    return;
                }
            }
        }
        solutions.add(getCopyOfSudokuGrid(grid));
        //printSudokuGrid(grid);
    }

    public void solve() {
        // TODO
    }

    public Instance getInstance() {
        return instance;
    }
}
