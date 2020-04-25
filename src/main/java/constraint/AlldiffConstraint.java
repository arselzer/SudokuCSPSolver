package constraint;

import variable.Variable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In our case, constraint.Constraint is an alldiff constraint
 */
public class AlldiffConstraint implements Constraint {
    private Set<Variable> variables;

    public AlldiffConstraint(Set<Variable> variables) {
        this.variables = variables;
    }

    /**
     * @return whether a constraint violation exists
     */
    @Override
    public boolean isConsistent() {
        HashSet<Integer> values = new HashSet<>();

        for (Variable v : variables) {
            if (v != null) {
                if (values.contains(v.getAssignment())) {
                    return false;
                }
                values.add(v.getAssignment());
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("ALLDIFF (%s)", variables.stream().map(Variable::getId).collect(Collectors.toList()));
    }
}
