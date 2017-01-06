package ru.itmo.nds;

import java.util.Arrays;

/**
 * Utility data structure for sweeps in PPSN.
 * In contains individuals' fitnesses and its index in the population array.
 */
class IndexedIndividual implements Comparable<IndexedIndividual> {
    private final double[] fitness;
    private final int index;

    IndexedIndividual(double[] fitness, int index) {
        this.fitness = fitness;
        this.index = index;
    }

    private double[] getFitness() {
        return fitness;
    }

    int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final IndexedIndividual that = (IndexedIndividual) o;
        return index == that.index && Arrays.equals(fitness, that.fitness);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(fitness);
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "IndexedIndividual{" +
                "fitness=" + Arrays.toString(fitness) +
                ", index=" + index +
                '}';
    }

    @Override
    public int compareTo(IndexedIndividual o) {
        if (this.getFitness()[1] != o.getFitness()[1])
            return Double.compare(this.getFitness()[1], o.getFitness()[1]);
        else if (this.getFitness()[0] != o.getFitness()[0])
            return Double.compare(this.getFitness()[0], o.getFitness()[0]);
        else
            return Integer.compare(this.getIndex(), o.getIndex());
    }
}
