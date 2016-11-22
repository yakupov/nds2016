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
        final Map<Integer, Integer> leftmostStairs = new HashMap<>(); //From rank to index
        for (int index : workingSet) {
            if (!leftmostStairs.containsKey(ranks[index]))
                leftmostStairs.put(ranks[index], index);
        }

        for (int index : workingSet) {
            if (leftmostStairs.containsKey(ranks[index])) {
                Integer t = leftmostStairs.get(ranks[index]);
                if (dominates(pop[t], pop[index], pop[t].length) < 0) {
                    ++ranks[index];

                    t = leftmostStairs.get(ranks[index]);
                    if (t == null || lexCompare(pop[t], pop[index], pop[t].length) > 0)
                        leftmostStairs.put(ranks[index], index);
                }
            }
        }
    }

    @Override
    protected void sweepB(double[][] pop, int[] ranks, List<Integer> lSet, List<Integer> hSet) {
        final Map<Integer, Integer> leftmostStairs = new HashMap<>(); //From rank to index
        for (int l : lSet) {
            if (!leftmostStairs.containsKey(ranks[l]))
                leftmostStairs.put(ranks[l], l);
        }

        for (int h : hSet) {
            if (leftmostStairs.containsKey(ranks[h])) {
                final int t = leftmostStairs.get(ranks[h]);
                if (dominates(pop[t], pop[h], pop[t].length) < 0)
                    ++ranks[h];
            }
        }
    }
}
