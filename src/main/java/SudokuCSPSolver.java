import constraint.Constraint;
import variable.Variable;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.Collections.reverseOrder;

public class SudokuCSPSolver implements ICSPSolver {
    Instance instance;
    File cspFile;
    private int gridLength = 9;

    private Set<Integer>[][] originalCandidates;
    private Set<Integer>[][] currentCandidates;

    public SudokuCSPSolver(File cspFile) throws Exception {
        this.cspFile = cspFile;
        XCSPReader reader = new XCSPReader(cspFile.toString());
        instance = reader.getProblemInstance();
    }

    @Override
    public void solveUsingBacktracking(boolean verbose) {
        int [][] sudokuGrid = getSudokuGridFromInstance(instance);
        ArrayList<int[][]> solutions = new ArrayList<>();
        if(verbose){
            System.out.print("Running solver using Backtracking\nInput problem:\n");
            printSudokuGrid(sudokuGrid);
            runBacktracking(sudokuGrid,solutions);
            System.out.println("Solutions:");
            for (int[][] s : solutions) {
                printSudokuGrid(s);
            }
        } else {
            runBacktracking(sudokuGrid, solutions);
        }
    }

    @Override
    public void solveUsingForwardChecking(boolean verbose) {
        int [][] sudokuGrid = getSudokuGridFromInstance(instance);
        originalCandidates = initCandidates(sudokuGrid);
        currentCandidates = copyCandidates(originalCandidates);

        ArrayList<int[][]> solutions = new ArrayList<>();
        if(verbose){
            System.out.print("Running solver using Forward Checking\nInput problem:\n");
            printSudokuGrid(sudokuGrid);
            runForwardChecking(sudokuGrid,solutions);
            System.out.println("Solutions:");
            for (int[][] s : solutions) {
                printSudokuGrid(s);
            }
        } else {
            runForwardChecking(sudokuGrid, solutions);
        }
    }

    @Override
    public void solveUsingForwardCheckingDynamicallyOrdered(boolean verbose) {
        int [][] sudokuGrid = getSudokuGridFromInstance(instance);
        LinkedHashMap<Integer, Integer> constraints = getListOfMostConstraintVariablesOrdered(sudokuGrid);
        ArrayList<int[][]> solutions = new ArrayList<>();
        if(verbose){
            System.out.print("Running solver using Forward Checking and Dynamic Ordering\nInput problem:\n");
            printSudokuGrid(sudokuGrid);
            runForwardCheckingDynamicallyOrdered(sudokuGrid,solutions, constraints);
            System.out.println("Solutions:");
            for (int[][] s : solutions) {
                printSudokuGrid(s);
            }
        }else {
            runForwardCheckingDynamicallyOrdered(sudokuGrid, solutions, constraints);
        }
    }

    @Override
    public void benchmark() {
        int [][] sudokuGrid = getSudokuGridFromInstance(instance);
        LinkedList<int[][]> solutions = new LinkedList<>();

        long start = System.nanoTime();
        solveUsingBacktracking(false);
        long stop = System.nanoTime();
        long backTrackingMs = (stop - start) / 1000000;

        start = System.nanoTime();
        solveUsingForwardChecking(false);
        stop = System.nanoTime();
        long forwardCheckingMs = (stop - start) / 1000000;

        start = System.nanoTime();
        solveUsingForwardCheckingDynamicallyOrdered(false);
        stop = System.nanoTime();
        long FCDOMs = (stop - start) / 1000000;

        System.out.printf("%s,%d,%d,%d\n", cspFile.getName(),backTrackingMs,forwardCheckingMs,FCDOMs);

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
        Pattern arrayIndexPattern = Pattern.compile("\\[(\\d+)\\]\\[\\d+\\]");
        int max = 0;
        for (Variable var : instance.getVariables()) {
            Matcher m = arrayIndexPattern.matcher(var.getId());

            if (m.find()) {
                int index = Integer.parseInt(m.group(1));
                if (index > max) {
                    max = index;
                }
            }
        }

        gridLength = max + 1;

        int grid[][] = new int[gridLength][gridLength];

        for (Constraint v : instance.getConstraints()) {
            if (v.toString().contains("FIXED")) {
                int[] r = getSudokuValueFromPosition(v.toString());
                grid[r[0]][r[1]] = r[2];
            }
        }
        return grid;
    }

    private int[] getSudokuValueFromPosition(String constraint) {
        Pattern pattern = Pattern.compile("FIXED \\(x\\[(\\d+)\\]\\[(\\d+)\\] = (\\d+)\\)");
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

    public void runBacktracking(int[][] grid, ArrayList<int[][]> solutions) {
        if (solutions.size() < 1) {
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[x].length; y++) {
                    // If the number at this point is not set yet, make a guess and check if it is possible
                    if (grid[x][y] == 0) {
                        for (int guess = 1; guess < gridLength+1; guess++) {
                            if (isPossible(grid, x, y, guess)) {
                                grid[x][y] = guess;
                                runBacktracking(grid, solutions);
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
        }
        //printSudokuGrid(grid);
    }

    private Set<Integer>[][] initCandidates(int[][] grid) {
        Set<Integer>[][] candidates = new Set[gridLength][gridLength];

        List<Integer> sudokuValues = new LinkedList<>();
        for (int i = 1; i <= gridLength; i++) {
            sudokuValues.add(i);
        }

        for (int x = 0; x < gridLength; x++) {
            for (int y = 0; y < gridLength; y++) {
                candidates[x][y] = new HashSet<>(sudokuValues);
            }
        }

        for (int x = 0; x < gridLength; x++) {
            for (int y = 0; y < gridLength; y++) {
                int value = grid[x][y];

                if (value != 0) {
                    for (int i = 0; i < gridLength; i++) {
                        candidates[x][i].remove(value);
                    }

                    for (int i = 0; i < gridLength; i++) {
                        candidates[i][y].remove(value);
                    }

                    int startSquareX = x - (x % 3);
                    int startSquareY = y - (y % 3);
                    for (int r = 0; r < 3; r++) {
                        for (int c = 0; c < 3; c++) {
                            int currentX = startSquareX + r;
                            int currentY = startSquareY + c;
                            candidates[currentX][currentY].remove(value);
                        }
                    }
                }
            }
        }

        return candidates;
    }

    private Set[][] copyCandidates(Set<Integer>[][] candidates) {
        Set<Integer>[][] candidatesCopy = new Set[gridLength][gridLength];

        for (int x = 0; x < gridLength; x++) {
            for (int y = 0; y < gridLength; y++) {
                candidatesCopy[x][y] = new HashSet<>(candidates[x][y]);
            }
        }

        return candidatesCopy;
    }

    private void resetCandidates() {
        for (int x = 0; x < gridLength; x++) {
            for (int y = 0; y < gridLength; y++) {
                currentCandidates[x][y] = new HashSet<>(originalCandidates[x][y]);
            }
        }
    }

    private void updateCandidates(Set<Integer>[][] candidates, int row, int col, int value) {
        for (int x = 0; x < gridLength; x++) {
            candidates[x][col].remove(value);
        }

        // Remove all values from column from candidates
        for (int y = 0; y < gridLength; y++) {
            candidates[row][y].remove(value);
        }

        // Remove all values from square from candidates
        int startSquareX = row - (row % 3);
        int startSquareY = col - (col % 3);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int currentX = startSquareX + x;
                int currentY = startSquareY + y;
                candidates[currentX][currentY].remove(value);
            }
        }
    }

    public void runForwardChecking(int[][] grid, ArrayList<int[][]> solutions) {
        if (solutions.size() < 1) {
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[x].length; y++) {
                    // If the number at this point is not set yet, get possible candidates (forward checking) and try
                    if (grid[x][y] == 0) {
                        // Prevent ConcurrentModificationException
                        for (Integer guess : new HashSet<>(currentCandidates[x][y])) {
                            grid[x][y] = guess;
                            //Set<Integer>[][] candidatesCopy = copyCandidates(candidates);
                            updateCandidates(currentCandidates, x, y, guess);

                            runForwardChecking(grid, solutions);
                            resetCandidates();
                            // Use backtracking if the choice was bad: reset the value and try again
                            grid[x][y] = 0;
                        }
                        // If all possibilities were checked, and none of them worked we are at a dead end and return
                        return;
                    }
                }
            }
            solutions.add(getCopyOfSudokuGrid(grid));
        }
    }

    // Returns from max to min ordered list with sudoku field (row*9+column) as key and degree of constraint as value
    private LinkedHashMap<Integer, Integer> getListOfMostConstraintVariablesOrdered(int[][] grid) {
        HashMap<Integer, Integer> constraintVariables = new HashMap<>();

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == 0) {
                    constraintVariables.put(x*gridLength+y, getDegreeOfConstraint(grid, x, y));
                }
            }
        }

        LinkedHashMap<Integer, Integer> result = constraintVariables.entrySet().stream()
                .sorted(reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (x, y) -> x,
                        LinkedHashMap::new
                ));

        return result;
    }

    // Updates the constraint database (the LinkedHashMap) when a variable was assigned or reset
    // -1 when variable is set and +1 when variable is reset to 0
    private void updateConstraints(LinkedHashMap<Integer, Integer> constraints, int positionRow, int positionColumn, int change) {
        // Update all values for row
        for (int x = 0; x < gridLength; x++) {
            int key = x*gridLength+positionColumn;
            if (constraints.containsKey(key)) {
                constraints.replace(key, constraints.get(key)+change);
            }
        }

        // Update all values for column
        for (int y = 0; y < gridLength; y++) {
            int key = positionRow*gridLength+y;
            if (constraints.containsKey(key)) {
                constraints.replace(key, constraints.get(key) + change);
            }
        }

        // Search in square
        int startSquareX = positionRow - (positionRow % 3);
        int startSquareY = positionColumn - (positionColumn % 3);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int currentX = startSquareX + x;
                int currentY = startSquareY + y;
                int key = currentX*gridLength+currentY;
                if (constraints.containsKey(key)) {
                    constraints.replace(key, constraints.get(key), constraints.get(key) + change);
                }
            }
        }
    }

    // Calculates the degree of constraint by adding values in the same row, column and square of a given field together
    private int getDegreeOfConstraint(int[][] grid, int positionRow, int positionColumn) {
        int degree = 0;

        // Search in row
        for (int x = 0; x < grid.length; x++) {
            if (grid[x][positionColumn] != 0) {
                degree++;
            }
        }

        // Search in column
        for (int y = 0; y < grid[0].length; y++) {
            if (grid[positionRow][y] != 0) {
                degree++;
            }
        }

        // Search in square
        int startSquareX = positionRow - (positionRow % 3);
        int startSquareY = positionColumn - (positionColumn % 3);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int currentX = startSquareX + x;
                int currentY = startSquareY + y;
                if (grid[currentX][currentY] != 0) {
                    degree++;
                }
            }
        }

        return degree;
    }

    private LinkedHashMap<Integer, Integer> updateValuesOrderedByPossibility(LinkedHashMap<Integer, Integer> values, int value, int change) {
        values.replace(value, values.get(value)+change);

        LinkedHashMap<Integer, Integer> result = values.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (x, y) -> x,
                        LinkedHashMap::new
                ));
        values = result;
        return values;
    }

    private LinkedHashMap<Integer, Integer> getValuesOrderedByPossibility(int[][] grid) {
        HashMap<Integer, Integer> values = new HashMap<>();
        for (int i = 1; i < gridLength+1; i++) {
            values.put(i, 0);
        }

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] != 0) {
                    values.replace(grid[x][y], values.get(grid[x][y])+1);
                }
            }
        }

        LinkedHashMap<Integer, Integer> result = values.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (x, y) -> x,
                        LinkedHashMap::new
                ));
        return result;
    }

    private int[] convertAbsoluteGridPositionToXandYCoordinate(int absolute) {
        return new int[]{absolute/gridLength,absolute % gridLength};
    }

    public void runForwardCheckingDynamicallyOrdered(int[][] grid, ArrayList<int[][]> solutions, LinkedHashMap<Integer, Integer> constraints) {
        if (solutions.size() < 1) {

            //LinkedHashMap<Integer, Integer> constraints = getListOfMostConstraintVariablesOrdered(grid); // {9=13, 71=13, 20=12, 60=12, 1=11, 6=11, 17=11, 27=11, 35=11, 39=11, 41=11, 45=11, 53=11, 63=11, 74=11, 79=11,
            LinkedHashMap<Integer, Integer> values = getValuesOrderedByPossibility(grid); // {3=2, 5=2, 1=3, 2=3, 6=3, 7=3, 8=3, 4=5, 9=6}

            constraints = constraints.entrySet().stream()
                    .sorted(reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue,
                            (x, y) -> x,
                            LinkedHashMap::new
                    ));
            for (Map.Entry<Integer, Integer> entry : new LinkedHashMap<Integer, Integer>(constraints).entrySet()) {
                Integer key = entry.getKey();
                int posX = convertAbsoluteGridPositionToXandYCoordinate(key)[0];
                int posY = convertAbsoluteGridPositionToXandYCoordinate(key)[1];
                // If the number at this point is not set yet, get most probable candidate (dynamic forward checking) and try
                if (grid[posX][posY] == 0) {
                    for (Map.Entry<Integer, Integer> entry1 : values.entrySet()) {
                        Integer k = entry1.getKey();
                        if (!isPossible(grid, posX, posY, k)) {
                            continue;
                        }
                        grid[posX][posY] = k;
                        updateValuesOrderedByPossibility(values, k, +1);
                        updateConstraints(constraints, posX, posY, +1);
                        runForwardCheckingDynamicallyOrdered(grid, solutions, constraints);
                        // Use backtracking if the choice was bad: reset the value and try again
                        grid[posX][posY] = 0;
                        updateValuesOrderedByPossibility(values, k, -1);
                        updateConstraints(constraints, posX, posY, -1);
                    }
                    ;
                    // If all possibilities were checked, and none of them worked we are at a dead end and return
                    return;
                }

            }
            solutions.add(getCopyOfSudokuGrid(grid));
        }
        //printSudokuGrid(grid);
    }

    public Instance getInstance() {
        return instance;
    }
}
