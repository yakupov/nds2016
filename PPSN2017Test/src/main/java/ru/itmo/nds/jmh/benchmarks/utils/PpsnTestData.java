package ru.itmo.nds.jmh.benchmarks.utils;

import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.reference.treap2015.TreapPopulation;
import ru.itmo.nds.util.RankedPopulation;

import java.util.List;
import java.util.Set;

public class PpsnTestData {
    private final double[] nextAdddend;
    private final RankedPopulation<double[]> rankedPopulation;
    private final Population<double[]> population;
    private final TreapPopulation treapPopulation;
    private final Set<double[]> enluIndividuals;
    private final List<Set<double[]>> enluLayers;

    /**
     * Test data
     *
     * @param nextAdddend      For all
     * @param rankedPopulation For all PPSN-based except LevelPPSN
     * @param population       For LevelPPSN
     * @param treapPopulation  For Treap2015
     * @param enluIndividuals  For ENLU
     * @param enluLayers       For ENLU
     */
    public PpsnTestData(double[] nextAdddend,
                        RankedPopulation<double[]> rankedPopulation,
                        Population<double[]> population,
                        TreapPopulation treapPopulation,
                        Set<double[]> enluIndividuals,
                        List<Set<double[]>> enluLayers) {
        this.nextAdddend = nextAdddend;
        this.rankedPopulation = rankedPopulation;
        this.population = population;
        this.treapPopulation = treapPopulation;
        this.enluIndividuals = enluIndividuals;
        this.enluLayers = enluLayers;
    }

    public double[] getNextAdddend() {
        return nextAdddend;
    }

    public RankedPopulation<double[]> getRankedPopulation() {
        return rankedPopulation;
    }

    public Population<double[]> getPopulation() {
        return population;
    }

    public TreapPopulation getTreapPopulation() {
        return treapPopulation;
    }

    public Set<double[]> getEnluIndividuals() {
        return enluIndividuals;
    }

    public List<Set<double[]>> getEnluLayers() {
        return enluLayers;
    }
}
