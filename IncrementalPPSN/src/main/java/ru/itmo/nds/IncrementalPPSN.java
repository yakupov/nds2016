package ru.itmo.nds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Can perform NDS only on a sorted population with one new point
 */
public class IncrementalPPSN extends PPSN2014 {
    @Override
    public int[] performNds(double[][] population) {
        throw new UnsupportedOperationException("Can't perform non incremental sorting");
    }

    @Override
    protected void sweepA(double[][] pop, int[] ranks, List<Integer> workingSet) {
        final Map<Integer, Integer> rightmostStairs = new HashMap<>(); //From rank to index

        for (int index : workingSet) {
            if (rightmostStairs.containsKey(ranks[index])) {
                final int t = rightmostStairs.get(ranks[index]);
                if (dominates(pop[t], pop[index], pop[t].length) < 0) {
                    ++ranks[index];
                }
            }

            final Integer t  = rightmostStairs.get(ranks[index]);
            if (t == null || pop[t][1] > pop[index][1])
                rightmostStairs.put(ranks[index], index);
        }
    }

    @Override
    protected void sweepB(double[][] pop, int[] ranks, List<Integer> lSet, List<Integer> hSet) {
        if (lSet == null || lSet.isEmpty() || hSet == null || hSet.isEmpty())
            return;

        final Map<Integer, Integer> rightmostStairs = new HashMap<>(); //From rank to index

        int lIndex = 0;
        for (int h : hSet) {
            while (lIndex < lSet.size() && lSet.get(lIndex) < h) {
                final int l = lSet.get(lIndex);

                if (!rightmostStairs.containsKey(ranks[l])) {
                    rightmostStairs.put(ranks[l], l);
                } else {
                    final int t = rightmostStairs.get(ranks[l]);
                    if (pop[t][1] >= pop[l][1])
                        rightmostStairs.put(ranks[l], l);
                }

                lIndex++;
            }

            if (rightmostStairs.containsKey(ranks[h])) {
                final int t = rightmostStairs.get(ranks[h]);
                if (dominates(pop[t], pop[h], pop[t].length) < 0)
                    ++ranks[h];
            }
        }
    }
}
