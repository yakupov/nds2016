package ru.itmo.nds.front_storage;

import java.util.Collection;

/**
 * id - rank of the individuals from this front
 * T - type of the individuals' fitness, usually double[]
 */
public class Front<T> {
    private int id;
    private Collection<T> fitnesses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<T> getFitnesses() {
        return fitnesses;
    }

    public void setFitnesses(Collection<T> fitnesses) {
        this.fitnesses = fitnesses;
    }
}
