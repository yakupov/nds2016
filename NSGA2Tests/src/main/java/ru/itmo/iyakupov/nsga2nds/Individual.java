package ru.itmo.iyakupov.nsga2nds;

import java.util.ArrayList;
import java.util.List;

public class Individual {
    final double[] fitnesses;
    int nDominating;
    List<Individual> dominated;

    public Individual(double[] fitnesses) {
        this.fitnesses = fitnesses;
        nDominating = 0;
        dominated = new ArrayList<>();
    }

    public double[] getFitnesses() {
        return fitnesses;
    }
}
