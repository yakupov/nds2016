package ru.itmo.ppsn;

import java.util.Arrays;

public class RankedIndividual {
    private int rank;
    private double[] fitness;

    public RankedIndividual(int rank, double[] fitness) {
        this.rank = rank;
        this.fitness = fitness;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double[] getFitness() {
        return fitness;
    }

    public void setFitness(double[] fitness) {
        this.fitness = fitness;
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
