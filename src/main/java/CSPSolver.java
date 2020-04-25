import constraint.Constraint;

import java.nio.file.Path;

public class CSPSolver {
    Instance instance;

    public CSPSolver(Path cspFile) throws Exception {
        XCSPReader reader = new XCSPReader(cspFile.toString());

        instance = reader.getProblemInstance();

        System.out.println(instance);

//        System.out.println(instance.getVariables());
//        for (Constraint c : instance.getConstraints()) {
//            System.out.println(c);
//        }
    }

    public void solve() {
        // TODO
    }

    public Instance getInstance() {
        return instance;
    }
}
