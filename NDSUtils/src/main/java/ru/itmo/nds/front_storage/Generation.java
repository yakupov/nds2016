package ru.itmo.nds.front_storage;

import java.util.Collection;

/**
 * A ranked (NDSorted) generation of individuals.
 *
 * id - number of this generation
 * fronts - collection of pairs (rank, individuals with this rank)
 */
public class Generation<T> {
    private int id;
    private Collection<Front<T>> fronts;
    private T nextAddend;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<Front<T>> getFronts() {
        return fronts;
    }

    public void setFronts(Collection<Front<T>> fronts) {
        this.fronts = fronts;
    }

    public T getNextAddend() {
        return nextAddend;
    }

    public void setNextAddend(T nextAddend) {
        this.nextAddend = nextAddend;
    }
}
