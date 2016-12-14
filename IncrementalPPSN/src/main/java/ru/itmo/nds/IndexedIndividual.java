package ru.itmo.nds;

import java.util.Arrays;

/**
 * Created by Ilia on 14.12.2016.
 */
public class IndexedIndividual {
    private final double[] ind;
    private final int index;

    public IndexedIndividual(double[] ind, int index) {
        this.ind = ind;
        this.index = index;
    }

    public double[] getInd() {
        return ind;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexedIndividual that = (IndexedIndividual) o;

        if (index != that.index) return false;
        return Arrays.equals(ind, that.ind);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(ind);
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "IndexedIndividual{" +
                "ind=" + Arrays.toString(ind) +
                ", index=" + index +
                '}';
    }
}
