package ru.itmo.nds.front_storage;

import java.util.List;
import java.util.Objects;

public class DoublesAdditionProblem {
    private final List<List<double[]>> fronts;
    private final List<double[]> addends;

    public DoublesAdditionProblem(List<List<double[]>> fronts, List<double[]> addends) {
        this.fronts = fronts;
        this.addends = addends;
    }

    public List<List<double[]>> getFronts() {
        return fronts;
    }

    public List<double[]> getAddends() {
        return addends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoublesAdditionProblem that = (DoublesAdditionProblem) o;
        return Objects.equals(fronts, that.fronts) &&
                Objects.equals(addends, that.addends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fronts, addends);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DoublesAdditionProblem{");
        sb.append("fronts=").append(fronts);
        sb.append(", addends=").append(addends);
        sb.append('}');
        return sb.toString();
    }
}
