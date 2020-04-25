import constraint.AlldiffConstraint;
import constraint.Constraint;
import constraint.FixedAssignmentConstraint;
import org.xcsp.parser.callbacks.XCallbacks2;
import org.xcsp.parser.entries.XVariables;
import variable.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class XCSPReader implements XCallbacks2 {
    private Set<Variable> variables = new HashSet<>();
    private Set<Constraint> constraints = new HashSet<>();
    private HashMap<String, Variable> idToVariableMap = new HashMap<>();

    private Implem implem = new Implem(this);

    @Override
    public Implem implem() {
        return implem;
    }

    public XCSPReader(String fileName) throws Exception {
        loadInstance(fileName);
    }

    @Override
    public void buildVarInteger(XVariables.XVarInteger x, int[] values) {
        Variable variable = new Variable(x.id);
        variables.add(variable);
        idToVariableMap.put(x.id, variable);
    }

    @Override
    public void buildVarInteger(XVariables.XVarInteger x, int minValue, int maxValue) {
        Variable variable = new Variable(x.id);
        variables.add(variable);
        idToVariableMap.put(x.id, variable);
    }

    @Override
    public void buildCtrAllDifferent(String id, XVariables.XVarInteger[] list) {
        Set<Variable> constraintVariables = new HashSet<>();

        for (XVariables.XVarInteger var : list) {
            Variable variable = idToVariableMap.get(var.id);
            constraintVariables.add(variable);
        }

        constraints.add(new AlldiffConstraint(constraintVariables));
    }

    @Override
    public void buildCtrInstantiation(String id, XVariables.XVarInteger[] list, int[] values) {
        // Maybe it is better to just restrict the domain of the variable?
        for (int i = 0; i < list.length; i++) {
            XVariables.XVarInteger var = list[i];
            Variable variable = idToVariableMap.get(var.id);
            constraints.add(new FixedAssignmentConstraint(variable, values[i]));
        }
    }

    public Instance getProblemInstance() {
        return new Instance(variables, constraints);
    }
}
