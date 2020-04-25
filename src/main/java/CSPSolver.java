import constraint.Constraint;

import java.nio.file.Path;
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

    public void solve() {
        // TODO
    }

    public Instance getInstance() {
        return instance;
    }
}
