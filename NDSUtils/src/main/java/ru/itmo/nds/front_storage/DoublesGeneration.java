package ru.itmo.nds.front_storage;

import ru.itmo.nds.util.RankedIndividual;
import ru.itmo.nds.util.RankedPopulation;

/**
 * Generation of individuals of a type double[]
 */
public class DoublesGeneration extends Generation<double[]> {

    public RankedPopulation getLexSortedRankedPop() {
        if (getFronts() == null) {
            return null;
        } else {
            final int generationSize = getFronts().stream()
                    .mapToInt(f -> f.getFitnesses() == null ? 0 : f.getFitnesses().size())
                    .sum();
            final double[][] pop = new double[generationSize][];
            final int[] ranks = new int[generationSize];

            int index = 0;
            for (Front<double[]> front: getFronts()) {
                if (front.getFitnesses() != null) {
                    for (double[] individual : front.getFitnesses()) {
                        pop[index] = individual;
                        ranks[index] = front.getId();
                        ++index;
                    }
                }
            }

            final int[] sortedRanks = RankedIndividual.sortRanksForLexSortedPopulation(ranks, pop);
            return new RankedPopulation(pop, sortedRanks);
        }
    }
}
