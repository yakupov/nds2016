package ru.itmo.iyakupov.nsga2nds;

import java.util.ArrayList;
import java.util.List;

public class Individual {
    double[] fitnesses;
    int nDominating;
    List<Individual> dominated;

    public Individual(double[] fitnesses) {
        this.fitnesses = fitnesses;
        nDominating = 0;
        dominated = new ArrayList<Individual>();
    }

    public double[] getFitnesses() {
        return fitnesses;
    }
}
