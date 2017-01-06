package ru.itmo.nds.jmh.benchmarks.utils;

import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.util.RankedPopulation;

public class PpsnTestData {
    private final double[] nextAdddend;
    private final RankedPopulation rankedPopulation;
    private final Population population;

    public PpsnTestData(double[] nextAdddend, RankedPopulation rankedPopulation, Population population) {
        this.nextAdddend = nextAdddend;
        this.rankedPopulation = rankedPopulation;
        this.population = population;
    }

    public RankedPopulation getRankedPopulation() {
        return rankedPopulation;
    }

    public double[] getNextAdddend() {
        return nextAdddend;
    }

    public Population getPopulation() {
        return population;
    }
}
