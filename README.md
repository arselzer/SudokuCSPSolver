# SudokuCSPSolver
Filip Darmanovic 01527089

Alexander Selzer 01633655

Julian Vecera 01627770 

## Prerequisites
To build SudokuCSPSolver, `maven` and `JDK >= v11` is required.
To run it, `JDK >= v11` is required.

## Build
To build the jar, execute `mvn package` on Linux/OSX or `maven package` on Windowsö

## Usage
The solver offers 2 modes:

1. The singe-instance mode, which solves the sudoku instance with a chosen approach,
Backtracking(BT), Forward Checking(FC), or Forward Checking with Dynamic Ordering(FC-DO).
It this mode outputs the input and one possible solution to the std. output.
To use it, run:

    `java -jar SudokuCSPSolver-1.0-SNAPSHOT.jar --instance path_to_instance --method [BT|FC|FC-DO]`
2. The benchmark mode, which takes as an argument one folder with Sudoku instances,
and has a CSV-style output in the form of `file_name,backtracking_ms,forward_checking_ms,FC_DO_ms`,
with the last 3 columns referring to execution time in milliseconds of each method on each instance
in the given folder. To use it, run:

    `java -jar SudokuCSPSolver-1.0-SNAPSHOT.jar --benchmark folder_with_instances`

## Examples

`java -jar target/SudokuCSPSolver-1.0-SNAPSHOT.jar --benchmark src/test/resources/sudoku/ > benchmarkResults.csv`

# Sources

Instances were taken from here: http://xcsp.org/series
