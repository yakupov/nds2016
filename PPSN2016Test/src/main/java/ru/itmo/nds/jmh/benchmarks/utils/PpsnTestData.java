package ru.itmo.nds.jmh.benchmarks.utils;

import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.util.RankedPopulation;

import java.util.List;
import java.util.Set;

public class PpsnTestData {
    private final double[] nextAdddend;
    private final RankedPopulation rankedPopulation;
    private final Population population;
    private final Set<double[]> enluIndividuals;
    private final List<Set<double[]>> enluLayers;

    /**
     * Test data
     *
     * @param nextAdddend      For all
     * @param rankedPopulation For all PPSN-based except LevelPPSN
     * @param population       For LevelPPSN
     * @param enluIndividuals  For ENLU
     * @param enluLayers       For ENLU
     */
    public PpsnTestData(double[] nextAdddend,
                        RankedPopulation rankedPopulation,
                        Population population,
                        Set<double[]> enluIndividuals,
                        List<Set<double[]>> enluLayers) {
        this.nextAdddend = nextAdddend;
        this.rankedPopulation = rankedPopulation;
        this.population = population;
        this.enluIndividuals = enluIndividuals;
        this.enluLayers = enluLayers;
    }

    public double[] getNextAdddend() {
        return nextAdddend;
    }

    public RankedPopulation getRankedPopulation() {
        return rankedPopulation;
    }

    public Population getPopulation() {
        return population;
    }

    public Set<double[]> getEnluIndividuals() {
        return enluIndividuals;
    }

    public List<Set<double[]>> getEnluLayers() {
        return enluLayers;
    }
}
