public interface ICSPSolver {

    public void solveUsingBacktracking(boolean verbose);

    public void solveUsingForwardChecking(boolean verbose);

    public void solveUsingForwardCheckingDynamicallyOrdered(boolean verbose);

    public void benchmark();
}
