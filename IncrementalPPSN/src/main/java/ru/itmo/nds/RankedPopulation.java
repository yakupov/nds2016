package ru.itmo.nds;

public class RankedPopulation {
    private final double[][] pop;
    private final int[] ranks;

    public RankedPopulation(double[][] pop, int[] ranks) {
        this.pop = pop;
        this.ranks = ranks;
    }

    public double[][] getPop() {
        return pop;
    }

    public int[] getRanks() {
        return ranks;
    }
}
