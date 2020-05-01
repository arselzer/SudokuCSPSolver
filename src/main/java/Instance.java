import constraint.Constraint;
import variable.Variable;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class Instance {
    private Set<Variable> variables;
    private Set<Constraint> constraints;

    public Instance(Set<Variable> variables, Set<Constraint> constraints) {
        this.variables = variables;
        this.constraints = constraints;
    }

    public boolean isComplete() {
        return variables.stream()
                .allMatch(variable -> variable.getAssignment() != null);
    }

    public boolean isConsistent() {
        return constraints.stream()
                .allMatch(Constraint::isConsistent);
    }

    public boolean isCorrect() {
        return isComplete() && isConsistent();
    }

    public VariableAssignment getVariableAssignment() {
        HashMap<String, Integer> assignmentMap = new HashMap<>();

        for (Variable variable : variables) {
            assignmentMap.put(variable.getId(), variable.getAssignment());
        }

        return new VariableAssignment(assignmentMap);
    }

    public void setVariableAssignment(VariableAssignment variableAssignment) {
        for (Variable variable : variables) {
            variable.setAssignment(variableAssignment.getValue(variable.getId()));
        }
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    public Set<Constraint> getConstraints() {
        return constraints;
    }

    @Override
    public String toString() {
        return String.format("Variables: (%s)\nConstraints: %s",
                variables.stream().map(var -> var.getId() + ":" + var.getAssignment()).collect(Collectors.joining(", ")),
                constraints.stream().map(Constraint::toString).collect(Collectors.joining("\n")));
    }
}
