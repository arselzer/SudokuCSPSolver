package constraint;

import variable.Variable;

public class FixedAssignmentConstraint implements Constraint {
    Variable variable;
    int value;

    public FixedAssignmentConstraint(Variable variable, int value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public boolean isConsistent() {
        return variable == null || variable.getAssignment().equals(value);
    }

    @Override
    public String toString() {
        return String.format("FIXED (%s = %d)", variable.getId(), value);
    }
}
