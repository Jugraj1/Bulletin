import java.util.HashSet;

public class FieldIndex<T extends Comparable<T>> implements Comparable<FieldIndex<T>> {
    private T field;
    private HashSet<Integer> indices = new HashSet<>();

    public FieldIndex(T field, Integer integer) {
        this.field = field;
        this.indices.add(integer);
    }

    public FieldIndex(T field, HashSet<Integer> integers) {
        this.field = field;
        this.indices = integers;
    }

    public void addIndex(Integer index) {
        indices.add(index);
    }

    public void merge(FieldIndex<T> a) {
        if (this.compareTo(a) == 0) {
            this.indices.addAll(a.getIndices());
        } else {
            throw new IllegalArgumentException("!!! Compared object has different fields, can't be merged");
        }
    }

    public T getField() {
        return field;
    }

    public void setField(T field) {
        this.field = field;
    }

    public HashSet<Integer> getIndices() {
        return indices;
    }

    public void setIndices(HashSet<Integer> indices) {
        this.indices = indices;
    }

    @Override
    public int compareTo(FieldIndex<T> other) {
        // Implement comparison logic here
        // For example, if T is Date, you can compare the dates
        // Assuming T extends Comparable<T>
        return field.compareTo(other.field);
    }

    @Override
    public String toString() {
        return "Field{" +
                "field=" + field +
                ", indices=" + indices +
                '}';
    }

}
