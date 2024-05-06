package com.example.app_2100.search;

import java.util.HashSet;

public class FieldIndex<T extends Comparable<T>, K extends Comparable<K>> implements Comparable<FieldIndex<T, K>> {
    private T field;
    private HashSet<K> indices = new HashSet<>();

    public FieldIndex(T field, K integer) {
        this.field = field;
        this.indices.add(integer);
    }

    public FieldIndex(T field, HashSet<K> integers) {
        this.field = field;
        this.indices = integers;
    }

    public void addIndex(K index) {
        indices.add(index);
    }

    public void merge(FieldIndex<T, K> a) {
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

    public HashSet<K> getIndices() {
        return indices;
    }

    public void setIndices(HashSet<K> indices) {
        this.indices = indices;
    }

    @Override
    public int compareTo(FieldIndex<T, K> other) {
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
