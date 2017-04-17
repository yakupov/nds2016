package ru.itmo.nds.util;

public class RankedPopulation<T> {
    private final T[] pop;
    private final int[] ranks;

    public RankedPopulation(T[] pop, int[] ranks) {
        this.pop = pop;
        this.ranks = ranks;
    }

    public T[] getPop() {
        return pop;
    }

    public int[] getRanks() {
        return ranks;
    }
}
