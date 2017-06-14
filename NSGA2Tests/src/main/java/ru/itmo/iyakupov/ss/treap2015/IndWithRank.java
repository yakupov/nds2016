package ru.itmo.iyakupov.ss.treap2015;

import org.moeaframework.core.Solution;

public class IndWithRank {
    private final Solution ind;
    private final int rank;

    public IndWithRank(Solution ind, int rank) {
        super();
        this.ind = ind;
        this.rank = rank;
    }

    public Solution getInd() {
        return ind;
    }

    public int getRank() {
        return rank;
    }

    public String toString() {
        return "Rank = " + rank + ", value = " + String.valueOf(ind);
    }
}
