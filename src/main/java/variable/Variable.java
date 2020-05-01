package variable;

import java.util.Objects;

public class Variable {
    private String id;

    private Integer assignment;

    public Variable(String id) {
        this.id = id;
    }

    public Variable(String id, Integer assignment) {
        this.id = id;
        this.assignment = assignment;
    }

    public String getId() {
        return id;
    }

    public Integer getAssignment() {
        return assignment;
    }

    public void setAssignment(Integer value) {
        this.assignment = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(id, variable.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Variable{" +
                "id='" + id + '\'' +
                ", assignment=" + assignment +
                '}';
    }
}
