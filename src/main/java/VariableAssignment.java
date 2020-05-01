import java.util.HashMap;
import java.util.Map;

public class VariableAssignment {
    private Map<String, Integer> assignment;

    public VariableAssignment(Map<String, Integer> assignment) {
        this.assignment = new HashMap<>(assignment);
    }

    public Integer getValue(String variable) {
        return assignment.get(variable);
    }

    public void assign(String variable, Integer value) {
        assignment.put(variable, value);
    }
}
