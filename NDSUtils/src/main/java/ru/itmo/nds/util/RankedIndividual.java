package ru.itmo.nds.util;

import java.util.Arrays;

public class RankedIndividual {
    private final int rank;
    private final double[] fitness;

    private RankedIndividual(int rank, double[] fitness) {
        this.rank = rank;
        this.fitness = fitness;
    }

    private int getRank() {
        return rank;
    }

    private double[] getFitness() {
        return fitness;
    }

    public static int[] sortRanksForLexSortedPopulation(int[] ranks, double[][] pop) {
        final RankedIndividual[] ri = new RankedIndividual[ranks.length];
        for (int i = 0; i < ranks.length; ++i) {
            ri[i] = new RankedIndividual(ranks[i], pop[i]);
        }

        Arrays.sort(ri, (o1, o2) -> {
            for (int i = 0; i < o1.getFitness().length; ++i) {
                if (o1.getFitness()[i] < o2.getFitness()[i])
                    return -1;
                else if (o1.getFitness()[i] > o2.getFitness()[i])
                    return 1;
            }
            return 0;
        });

        return Arrays.stream(ri).mapToInt(RankedIndividual::getRank).toArray();
    }
}
