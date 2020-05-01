import java.io.File;
import java.util.Deque;
import java.util.LinkedList;

public class GeneralCSPSolver implements ICSPSolver {
    Instance instance;
    File cspFile;

    public GeneralCSPSolver(File cspFile) throws Exception {
        this.cspFile = cspFile;
        XCSPReader reader = new XCSPReader(cspFile.toString());
        instance = reader.getProblemInstance();
    }

    @Override
    public void solveUsingBacktracking(boolean verbose) {
        Deque<VariableAssignment> solutions = new LinkedList<>();
        solutions.addFirst(instance.getVariableAssignment());

        boolean done = false;
        while (solutions.size() > 0) {
            instance.setVariableAssignment(solutions.peekFirst());

            if (!(instance.isComplete() && instance.isConsistent())) {

            }
        }
    }

    @Override
    public void solveUsingForwardChecking(boolean verbose) {

    }

    @Override
    public void solveUsingForwardCheckingDynamicallyOrdered(boolean verbose) {

    }

    @Override
    public void benchmark() {

    }
}
