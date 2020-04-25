package variable;

import java.util.Objects;

public class Variable {
    private String id;

    private Integer assignment;

    public Variable(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Integer getAssignment() {
        return assignment;
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
