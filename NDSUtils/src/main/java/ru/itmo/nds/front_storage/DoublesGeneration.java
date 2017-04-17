package ru.itmo.nds.front_storage;

import ru.itmo.nds.util.RankedIndividual;
import ru.itmo.nds.util.RankedPopulation;

import java.util.Arrays;

/**
 * Generation of individuals of a type double[]
 */
public class DoublesGeneration extends Generation<double[]> {
    public RankedPopulation<double[]> getLexSortedRankedPop() {
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
            Arrays.sort(pop, (o1, o2) -> {
                for (int i = 0; i < o1.length; ++i) {
                    if (o1[i] < o2[i])
                        return -1;
                    else if (o1[i] > o2[i])
                        return 1;
                }
                return 0;
            });
            return new RankedPopulation<>(pop, sortedRanks); //TODO: check
        }
    }
}
