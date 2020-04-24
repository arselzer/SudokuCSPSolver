import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * In our case, Constraint is an alldiff constraint
 */
public class AlldiffConstraint implements Constraint {
    private LinkedHashSet<Variable> variables;

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
}
