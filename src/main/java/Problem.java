import java.util.Set;

public class Problem {
    private Set<Variable> variables;
    private Set<Constraint> constraints;

    public Problem(Set<Variable> variables, Set<Constraint> constraints) {
        this.variables = variables;
        this.constraints = constraints;
    }
}
