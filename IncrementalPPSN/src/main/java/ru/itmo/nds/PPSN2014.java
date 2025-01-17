package ru.itmo.nds;

import ru.itmo.nds.util.RankedPopulation;
import ru.itmo.util.QuickSelect;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static ru.itmo.nds.util.ComparisonUtils.dominates;

/**
 * Another implementation of a NDS, proposed in the following paper:
 * <p>
 * incollection{
 * author       = {Maxim Buzdalov and Anatoly Shalyto},
 * title        = {A Provably Asymptotically Fast Version of the Generalized Jensen Algorithm
 * for Non-dominated Sorting},
 * booktitle    = {Parallel Problem Solving from Nature XIII},
 * series       = {Lecture Notes on Computer Science},
 * number       = {8672},
 * year         = {2005},
 * pages        = {528-537},
 * langid       = {english}
 * }
 */
@SuppressWarnings({"UnnecessaryReturnStatement", "Convert2streamapi"})
public class PPSN2014<T> {
    public static final String ENABLE_PPSN_TRACE_PROPERTY = "ru.itmo.ppsn.trace_to_stdout";
    @SuppressWarnings("WeakerAccess")
    public static final String ENABLE_PPSN_DEBUG_PROPERTY = "ru.itmo.ppsn.debug_on";

    private final boolean traceToStdout;
    private final boolean debugEnabled;

    private final QuickSelect quickSelect = new QuickSelect();
    private final Function<T, double[]> objectivesExtractor;

    public PPSN2014(Function<T, double[]> objectivesExtractor) {
        this.objectivesExtractor = objectivesExtractor;

        traceToStdout = System.getProperty(ENABLE_PPSN_TRACE_PROPERTY) != null;
        debugEnabled = System.getProperty(ENABLE_PPSN_DEBUG_PROPERTY) != null;
    }

    /**
     * Add one point and do incremental sorting.
     *
     * @param sortedPop Sorted population to add the new points into
     * @param ranks     Ranks of the population
     * @param addend    New point
     * @return New sorted population and its ranks
     */
    public RankedPopulation<T> performIncrementalNds(final T[] sortedPop, final int[] ranks, final T addend) {
        final double[] addendObjectives = extractObj(addend);
        if (debugEnabled) {
            assert (ranks.length == sortedPop.length);
            assert (sortedPop.length == 0 || addendObjectives.length == extractObj(sortedPop[0]).length);
        }

        final int dim = addendObjectives.length;
        @SuppressWarnings("unchecked") final T[] newPop = (T[]) Array.newInstance(addend.getClass(), sortedPop.length + 1);
        final int[] newRanks = new int[ranks.length + 1];
        final List<Integer> hSet = new ArrayList<>(ranks.length);
        //final List<Integer> lSet = new ArrayList<>(ranks.length);

        int writeIndex = 0;
        int addendIndex = -1;
        int addendRank = 0;
        for (int i = 0; i < sortedPop.length; ++i) {
            if (addendIndex < 0 && lexCompare(addendObjectives, extractObj(sortedPop[i]), dim) <= 0) {
                addendIndex = writeIndex;
                newPop[addendIndex] = addend;
                newRanks[addendIndex] = addendRank;
                //lSet.add(addendIndex);
                writeIndex++;
            }

            newRanks[writeIndex] = ranks[i];
            newPop[writeIndex] = sortedPop[i];

            final int dom = dominates(extractObj(sortedPop[i]), addendObjectives, dim);
            if (dom > 0) {
                hSet.add(writeIndex);
            } else {
                //lSet.add(writeIndex);
                if (dom < 0)
                    addendRank = Math.max(addendRank, ranks[i] + 1);
            }

            writeIndex++;
        }

        if (addendIndex < 0) {
            addendIndex = newPop.length - 1;
            newPop[addendIndex] = addend;
            newRanks[addendIndex] = addendRank;
            //lSet.add(addendIndex);
        }

        //ndHelperB(newPop, newRanks, dim - 1, lSet, hSet, 0);
        ndHelperB(newPop, newRanks, dim - 1, Collections.singletonList(addendIndex), hSet, 0);
        ndHelperA(newPop, newRanks, dim - 1, hSet, 0);

        return new RankedPopulation<>(newPop, newRanks);
    }

    double[] extractObj(T t) {
        return objectivesExtractor.apply(t);
    }

    /**
     * Sorts population lexicographically (input array will be modified!) and returns array of ranks
     * (for the sorted population)
     *
     * @param population population
     * @return ranks
     */
    public int[] performNds(T[] population) {
        if (population == null || population.length == 0)
            return new int[0];

        Arrays.sort(population, (o1, o2) -> {
            final double[] o1Obj = extractObj(o1);
            final double[] o2Obj = extractObj(o2);
            for (int i = 0; i < o1Obj.length; ++i) {
                if (o1Obj[i] < o2Obj[i])
                    return -1;
                else if (o1Obj[i] > o2Obj[i])
                    return 1;
            }
            return 0;
        });

        final int[] ranks = new int[population.length];
        final int k = extractObj(population[0]).length;
        if (traceToStdout) {
            logToStdout(0, "Performing non-dominating sort with K = " + k);
        }

        if (k == 0) {
            return ranks;
        } else if (k == 1) {
            for (int i = 1; i < population.length; ++i) {
                if (extractObj(population[i])[0] == extractObj(population[i - 1])[0])
                    ranks[i] = ranks[i - 1];
                else
                    ranks[i] = ranks[i - 1] + 1;
            }
            return ranks;
        } else {
            final List<Integer> workingSet = new ArrayList<>(population.length);
            for (int i = 0; i < population.length; ++i)
                workingSet.add(i);

            ndHelperA(population, ranks, k - 1, workingSet, 0);
            return ranks;
        }
    }

    /**
     * Assign ranks to the points of {@code workingSet} basing on the first {@code k} + 1 coordinates
     * using the "divide and conquer" approach.
     *
     * @param pop        Lexicographically sorted population
     * @param ranks      ranks[i] is the rank of individual pop[i]
     * @param k          Maximum comparable coordinate index
     * @param workingSet Indices of the population members that should be analyzed during the current run. Must be sorted.
     * @param level      Recursion level (used for logging)
     */
    void ndHelperA(T[] pop, int[] ranks, int k, List<Integer> workingSet, int level) {
        if (debugEnabled) {
            assert (pop.length == ranks.length);
            assert (workingSet == null || workingSet.size() <= pop.length);
        }

        if (traceToStdout) {
            logToStdout(level, "NDHelperA. K = " + k + ", workingSet = " + workingSet);
            logToStdout(level, ("Ranks = " + Arrays.toString(ranks)));
        }

        if (workingSet == null || workingSet.size() < 2) {
            return;
        } else if (workingSet.size() == 2) {
            if (dominates(extractObj(pop[workingSet.get(0)]),
                    extractObj(pop[workingSet.get(1)]), k + 1) < 0)
                ranks[workingSet.get(1)] = Math.max(ranks[workingSet.get(1)], ranks[workingSet.get(0)] + 1);
        } else if (k == 1) {
            sweepA(pop, ranks, workingSet);
        } else {
            Double sKPrev = null;
            for (int index : workingSet) {
                final double[] popIndex = extractObj(pop[index]);
                if (sKPrev != null && sKPrev != popIndex[k]) {
                    final double[] kth = new double[workingSet.size()];
                    for (int i = 0; i < workingSet.size(); ++i) {
                        kth[i] = extractObj(pop[workingSet.get(i)])[k];
                    }
                    final double median = quickSelect.getMedian(kth);
                    final List<Integer> l = new ArrayList<>(workingSet.size());
                    final List<Integer> m = new ArrayList<>(workingSet.size());
                    final List<Integer> h = new ArrayList<>(workingSet.size());
                    split(pop, k, median, workingSet, l, m, h);
                    final List<Integer> lm = sortedMerge(l, m);

                    if (traceToStdout) {
                        logToStdout(level, "Performing split. L, M, H:");
                        logToStdout(level, l);
                        logToStdout(level, m);
                        logToStdout(level, h);
                        logToStdout(level, "Ranks: " + Arrays.toString(ranks));
                    }

                    ndHelperA(pop, ranks, k, l, level + 1);
                    ndHelperB(pop, ranks, k - 1, l, m, level + 1);
                    ndHelperA(pop, ranks, k - 1, m, level + 1);
                    ndHelperB(pop, ranks, k - 1, lm, h, level + 1);
                    ndHelperA(pop, ranks, k, h, level + 1);
                    return;
                } else {
                    sKPrev = popIndex[k];
                }
            }

            ndHelperA(pop, ranks, k - 1, workingSet, level + 1);
        }
    }

    /**
     * Assign ranks using the first two coordinates
     *
     * @param pop        Lexicographically sorted population
     * @param ranks      ranks[i] is the rank of individual pop[i]
     * @param workingSet Indices of the population members that should be analyzed during the current run. Must be sorted.
     */
    protected void sweepA(T[] pop, int[] ranks, List<Integer> workingSet) {
        if (debugEnabled) {
            assert (pop.length == ranks.length);
            assert (workingSet.size() > 0 && workingSet.size() <= pop.length);
        }

        final TreeSet<IndexedIndividual> secondCoordSet = new TreeSet<>();
        final Map<Integer, Integer> rankToIndex = new HashMap<>();

        secondCoordSet.add(new IndexedIndividual(extractObj(pop[workingSet.get(0)]), workingSet.get(0)));
        rankToIndex.put(ranks[workingSet.get(0)], workingSet.get(0));

        for (int i = 1; i < workingSet.size(); ++i) {
            final int currIndex = workingSet.get(i);
            final double[] currIndividual = extractObj(pop[currIndex]);

            int r = Integer.MIN_VALUE;
            for (IndexedIndividual ii : secondCoordSet.headSet(new IndexedIndividual(currIndividual, currIndex), true)) {
                final double[] popIIIndex = extractObj(pop[ii.getIndex()]);
                if (popIIIndex[1] < currIndividual[1] || popIIIndex[1] == currIndividual[1] && popIIIndex[0] < currIndividual[0]) {
                    r = Math.max(r, ranks[ii.getIndex()]);
                }
            }
            ranks[currIndex] = Math.max(r + 1, ranks[currIndex]);

            cleanupTSet(secondCoordSet.tailSet(new IndexedIndividual(currIndividual, currIndex), true),
                    rankToIndex, ranks, ranks[currIndex]);

            rankToIndex.put(ranks[currIndex], currIndex);
            secondCoordSet.add(new IndexedIndividual(extractObj(pop[currIndex]), currIndex));

        }
    }

    /**
     * Remove unnecessary points from the "staircase"
     *
     * @param secondCoordSet Set of points, lexicographically sorted by second coordinate
     * @param rankToIndex    Maps rank to the lexicographically rightmost point with this rank
     * @param ranks          Mapping from point indices to point ranks
     * @param currRank       Rank of the next point to be added to the staircase
     */
    private void cleanupTSet(NavigableSet<IndexedIndividual> secondCoordSet, Map<Integer, Integer> rankToIndex, int[] ranks, int currRank) {
        for (Iterator<IndexedIndividual> it = secondCoordSet.iterator(); it.hasNext(); ) {
            final IndexedIndividual individual = it.next();
            if (ranks[individual.getIndex()] <= currRank) {
                it.remove();
                rankToIndex.remove(ranks[individual.getIndex()]);
            }
        }
    }

    /**
     * Adjust ranks of the points from {@code hSet} basing on the ranks of the points from {@code lSet}
     * basing on the first {@code k} + 1 coordinates
     *
     * @param pop   Lexicographically sorted population
     * @param ranks ranks[i] is the rank of individual pop[i]
     * @param k     Maximum comparable coordinate index
     * @param lSet  Lower set (its ranks are already calculated). Must be sorted.
     * @param hSet  Higher set (its ranks are to be updated). Must be sorted.
     * @param level Recursion level (used for logging)
     *
     * @return whether at least one individual has changed its rank
     */
    boolean ndHelperB(T[] pop, int[] ranks, int k, List<Integer> lSet, List<Integer> hSet, int level) {
        if (debugEnabled) {
            assert (pop.length == ranks.length);
        }

        if (traceToStdout) {
            logToStdout(level, ("NDHelperB. K = " + k + ", l = " + lSet + ", h = " + hSet));
        }

        if (lSet == null || lSet.isEmpty() || hSet == null || hSet.isEmpty()) {
            return false;
        } else if (lSet.size() == 1 || hSet.size() == 1) {
            boolean rankChanged = false;
            for (int h : hSet) {
                final double[] popH = extractObj(pop[h]);
                for (int l : lSet) {
                    final double[] popL = extractObj(pop[l]);
                    if (dominates(popL, popH, popL.length) < 0 && ranks[l] + 1 > ranks[h]) {
                        ranks[h] = ranks[l] + 1;
                        rankChanged = true;
                    }
                }
            }
            return rankChanged;
        } else if (k == 1) {
            return sweepB(pop, ranks, lSet, hSet);
        } else {
            double lMin = Double.POSITIVE_INFINITY;
            double lMax = Double.NEGATIVE_INFINITY;
            for (int l : lSet) {
                lMin = Math.min(extractObj(pop[l])[k], lMin);
                lMax = Math.max(extractObj(pop[l])[k], lMax);
            }

            double hMin = Double.POSITIVE_INFINITY;
            double hMax = Double.NEGATIVE_INFINITY;
            for (int h : hSet) {
                hMin = Math.min(extractObj(pop[h])[k], hMin);
                hMax = Math.max(extractObj(pop[h])[k], hMax);
            }

            if (lMax <= hMin) {
                return ndHelperB(pop, ranks, k - 1, lSet, hSet, level + 1);
            } else if (lMin <= hMax) {
                final double[] kth = new double[hSet.size() + lSet.size()];
                for (int i = 0; i < lSet.size(); ++i)
                    kth[i] = extractObj(pop[lSet.get(i)])[k];
                for (int i = 0; i < hSet.size(); ++i)
                    kth[lSet.size() + i] = extractObj(pop[hSet.get(i)])[k];
                final double median = quickSelect.getMedian(kth);

                final List<Integer> l1 = new ArrayList<>();
                final List<Integer> m1 = new ArrayList<>();
                final List<Integer> h1 = new ArrayList<>();
                split(pop, k, median, lSet, l1, m1, h1);
                final List<Integer> l1m1 = sortedMerge(l1, m1);

                final List<Integer> l2 = new ArrayList<>();
                final List<Integer> m2 = new ArrayList<>();
                final List<Integer> h2 = new ArrayList<>();
                split(pop, k, median, hSet, l2, m2, h2);

                boolean rankChanged = ndHelperB(pop, ranks, k, l1, l2, level + 1);
                rankChanged |= ndHelperB(pop, ranks, k - 1, l1, m2, level + 1);
                rankChanged |= ndHelperB(pop, ranks, k - 1, m1, m2, level + 1);
                rankChanged |= ndHelperB(pop, ranks, k - 1, l1m1, h2, level + 1);
                rankChanged |= ndHelperB(pop, ranks, k, h1, h2, level + 1);

                return rankChanged;
            }

            return false;
        }
    }

    /**
     * Adjust ranks of the points from {@code hSet} basing on the ranks of the points from {@code lSet}
     * basing on the first two coordinates
     *
     * @param pop   Lexicographically sorted population
     * @param ranks ranks[i] is the rank of individual pop[i]
     * @param lSet  Lower set (its ranks are already calculated). Must be sorted.
     * @param hSet  Higher set (its ranks are to be updated). Must be sorted.
     *
     * @return whether at least one individual has changed its rank
     */
    protected boolean sweepB(T[] pop, int[] ranks, List<Integer> lSet, List<Integer> hSet) {
        if (debugEnabled) {
            assert (pop.length == ranks.length);
        }

        final TreeSet<IndexedIndividual> secondCoordSet = new TreeSet<>();
        final Map<Integer, Integer> rankToIndex = new HashMap<>();

        int lIndex = 0;
        boolean rankChanged = false;
        for (int h : hSet) {
            final double[] popH = extractObj(pop[h]);
            while (lIndex < lSet.size() && lexCompare(extractObj(pop[lSet.get(lIndex)]), popH, 2) <= 0) {
                final int l = lSet.get(lIndex);
                final double[] popL = extractObj(pop[l]);
                if (!rankToIndex.containsKey(ranks[l]) || extractObj(pop[rankToIndex.get(ranks[l])])[1] > popL[1]) {
                    cleanupTSet(secondCoordSet.tailSet(new IndexedIndividual(popL, l), true), rankToIndex, ranks, ranks[l]);
                    rankToIndex.put(ranks[l], l);
                    secondCoordSet.add(new IndexedIndividual(popL, l));
                }
                lIndex++;
            }

            int r = Integer.MIN_VALUE;
            for (IndexedIndividual ii : secondCoordSet.headSet(new IndexedIndividual(popH, h), true)) {
                final int t = ii.getIndex();
                if (dominates(extractObj(pop[t]), popH, popH.length) < 0) {
                    r = Math.max(r, ranks[t]);
                }
            }

            if (r + 1 > ranks[h]) {
                ranks[h] = r + 1;
                rankChanged = true;
            }
        }

        return rankChanged;
    }

    /*
     * Utility methods
     */

    /**
     * Split the population into three parts around the median
     *
     * @param pop         Population
     * @param k           Objective to the population split on (array index!)
     * @param medianValue Value of the {@code k}-th objective to split the population on
     * @param workingSet  input
     * @param l           output: p_{@code k} < {@code medianValue}, where p is a member of {@code pop}
     * @param m           output: p_{@code k} = {@code medianValue}, where p is a member of {@code pop}
     * @param h           output: p_{@code k} > {@code medianValue}, where p is a member of {@code pop}
     */
    private void split(T[] pop,
                       int k,
                       double medianValue,
                       List<Integer> workingSet,
                       List<Integer> l,
                       List<Integer> m,
                       List<Integer> h) {
        assert (l != null && l.isEmpty());
        assert (m != null && m.isEmpty());
        assert (h != null && h.isEmpty());
        assert (workingSet != null && workingSet.size() <= pop.length);

        for (int i : workingSet) {
            final double[] ind = extractObj(pop[i]);
            if (ind[k] < medianValue)
                l.add(i);
            else if (ind[k] == medianValue)
                m.add(i);
            else
                h.add(i);
        }
    }

    /**
     * Perform lexicographical comparison
     *
     * @param d1  First individual
     * @param d2  Second individual
     * @param dim Number of comparable coordinates in each individual (not max. index!)
     * @return -1 if {@code d1} is lexicographically smaller than {@code d2}. 1 if larger. 0 if equal.
     */
    int lexCompare(double[] d1, double[] d2, int dim) {
        assert (d1.length >= dim && d2.length >= dim);

        for (int i = 0; i < dim; ++i) {
            if (d1[i] < d2[i])
                return -1;
            else if (d1[i] > d2[i])
                return 1;
        }
        return 0;
    }

    private List<Integer> sortedMerge(List<Integer> l1, List<Integer> l2) {
        final List<Integer> res = new ArrayList<>(l1.size() + l2.size());
        int l1Index = 0;
        int l2Index = 0;
        while (l1Index < l1.size() || l2Index < l2.size()) {
            if (l1Index == l1.size()) {
                res.add(l2.get(l2Index++));
            } else if (l2Index == l2.size()) {
                res.add(l1.get(l1Index++));
            } else if (l1.get(l1Index) <= l2.get(l2Index)) {
                res.add(l1.get(l1Index++));
            } else {
                res.add(l2.get(l2Index++));
            }
        }

        return res;
    }

    private void logToStdout(int level, Object message) {
        for (int i = 0; i < level; ++i)
            System.out.print("\t");
        System.out.println(message);
    }
}
