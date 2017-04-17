package ru.itmo.nds;

import ru.itmo.nds.util.RankedPopulation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static ru.itmo.nds.util.ComparisonUtils.dominates;

/**
 * Can perform NDS only on a sorted population with one new point (or a contiguous set of points with the same rank)
 */
public class IncrementalPPSN<T> extends PPSN2014<T> {
    public IncrementalPPSN(Function<T, double[]> objectivesExtractor) {
        super(objectivesExtractor);
    }

    /**
     * Add a set of already evaluated points of the same rank to the sorted population,
     * if ALL POINTS IN POP HAVE THE SAME RANK
     *
     * @param pop Sorted population
     * @param ranks Ranks of the members of {@code pop}
     * @param addends Points to add to {@code pop}
     * @param rankHint Pre-evaluated rank of {@code addends} based on domination
     *                 relationships with the members of {@code pop}
     * @return Updated population
     */
    public RankedPopulation<T> addRankedMembers(List<T> pop, int[] ranks, List<T> addends,
                                             @SuppressWarnings("SameParameterValue") int rankHint) { //TODO: UT
        final Class<?> tClass = !addends.isEmpty() ? addends.get(0).getClass() : pop.get(0).getClass();

        final double[] ultimateAddend = new double[extractObj(addends.get(0)).length];
        for (int i = 0; i < ultimateAddend.length; ++i) {
            ultimateAddend[i] = Double.POSITIVE_INFINITY;
        }
        for (T addend: addends) {
            final double[] obj = extractObj(addend);
            for (int i = 0; i < ultimateAddend.length; ++i) {
                ultimateAddend[i] = Math.min(ultimateAddend[i], obj[i]);
            }
        }

        @SuppressWarnings("unchecked") final T[] newPop = (T[]) Array.newInstance(tClass, pop.size() + addends.size());
        final int[] newRanks = new int[newPop.length];
        final int dim = extractObj(addends.get(0)).length;

        int iPop = 0;
        int iAdd = 0;
        final List<Integer> hSet = new ArrayList<>(pop.size());
        final List<Integer> lSet = new ArrayList<>(pop.size() + addends.size());
        for (int i = 0; i < newPop.length; ++i) {
            if (iAdd == addends.size() ||
                    iPop < pop.size() && lexCompare(extractObj(pop.get(iPop)), extractObj(addends.get(iAdd)), dim) <= 0) {
                newPop[i] = pop.get(iPop);
                newRanks[i] = ranks[iPop++];

                if (dominates(ultimateAddend, extractObj(newPop[i]), dim) < 0)
                    hSet.add(i);
            } else {
                newPop[i] = addends.get(iAdd++);
                newRanks[i] = rankHint;
                lSet.add(i);
            }
        }

        ndHelperB(newPop, newRanks, dim - 1, lSet, hSet, 0);
        //ndHelperA(newPop, newRanks, dim - 1, hSet, 0);

        return new RankedPopulation<>(newPop, newRanks);
    }

    @Override
    public int[] performNds(T[] population) {
        throw new UnsupportedOperationException("Can't perform non-incremental sorting");
    }

    @Override
    protected void sweepA(T[] pop, int[] ranks, List<Integer> workingSet) {
        final Map<Integer, Integer> rightmostStairs = new HashMap<>(); //From rank to index

        for (int index : workingSet) {
            final double[] popIndex = extractObj(pop[index]);
            if (rightmostStairs.containsKey(ranks[index])) {
                final int t = rightmostStairs.get(ranks[index]);
                if (dominates(extractObj(pop[t]), popIndex, extractObj(pop[t]).length) < 0) {
                    ++ranks[index];
                }
            }

            final Integer t  = rightmostStairs.get(ranks[index]);
            if (t == null || extractObj(pop[t])[1] > popIndex[1])
                rightmostStairs.put(ranks[index], index);
        }
    }

    @Override
    protected boolean sweepB(T[] pop, int[] ranks, List<Integer> lSet, List<Integer> hSet) {
        if (lSet == null || lSet.isEmpty() || hSet == null || hSet.isEmpty())
            return false;

        final Map<Integer, Integer> rightmostStairs = new HashMap<>(); //From rank to index

        int lIndex = 0;
        boolean rankChanged = false;
        for (int h : hSet) {
            while (lIndex < lSet.size() && lSet.get(lIndex) < h) {
                final int l = lSet.get(lIndex);
                final double[] popL = extractObj(pop[l]);
                if (!rightmostStairs.containsKey(ranks[l])) {
                    rightmostStairs.put(ranks[l], l);
                } else {
                    final int t = rightmostStairs.get(ranks[l]);
                    if (extractObj(pop[t])[1] >= popL[1])
                        rightmostStairs.put(ranks[l], l);
                }

                lIndex++;
            }

            if (rightmostStairs.containsKey(ranks[h])) {
                final int t = rightmostStairs.get(ranks[h]);
                if (dominates(extractObj(pop[t]), extractObj(pop[h]), extractObj(pop[t]).length) < 0) {
                    ++ranks[h];
                    rankChanged = true;
                }
            }
        }
        return rankChanged;
    }
}
