package ru.itmo.nds;

import ru.itmo.iyakupov.MedianOfMediansQS;
import ru.itmo.iyakupov.QuickSelect;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@SuppressWarnings("UnnecessaryReturnStatement")
public class PPSN2014 {
    /**
     * Sorts population lexicographically (input array will be modified!) and returns array of ranks
     * (for the sorted population)
     *
     * @param population population
     * @return ranks
     */
    public int[] performNds(double[][] population) {
        if (population == null || population.length == 0)
            return new int[0];

        Arrays.sort(population, (o1, o2) -> {
            for (int i = 0; i < o1.length; ++i) {
                if (o1[i] < o2[i])
                    return -1;
                else if (o1[i] > o2[i])
                    return 1;
            }
            return 0;
        });

        final int k = population[0].length - 1;
        //System.out.println("Performing nds with K = " + k);

        final int[] ranks = new int[population.length];
        final List<Integer> workingSet = new ArrayList<>(population.length);
        for (int i = 0; i < population.length; ++i) {
            workingSet.add(i);
        }

        ndHelperA(population, ranks, k, workingSet, 0);
        return ranks;
    }

    //TODO: javadoc & cleanup unused 'logging' code

    private void ndHelperA(double[][] pop, int[] ranks, int k, List<Integer> workingSet, int level) {
        assert (pop.length == ranks.length);
        assert (workingSet == null || workingSet.size() <= pop.length);

//        printSpaces(level);
//        System.out.println("Enter NHA. K = " + k + ", workingSet = " + workingSet);
//        printSpaces(level);
//        System.out.println(" Ranks = " + Arrays.toString(ranks));

        if (workingSet == null || workingSet.size() < 2) {
            return;
        } else if (workingSet.size() == 2) {
            if (dominates(pop[workingSet.get(0)], pop[workingSet.get(1)], k + 1) < 0)
                ranks[workingSet.get(1)] = Math.max(ranks[workingSet.get(1)], ranks[workingSet.get(0)] + 1);
        } else if (k == 1) {
            sweepA(pop, ranks, workingSet);
        } else {
            Double kPrev = null;
            for (int index : workingSet) {
                if (kPrev != null && kPrev != pop[index][k]) {
                    final double[] kth = new double[workingSet.size()];
                    for (int i = 0; i < workingSet.size(); ++i) {
                        kth[i] = pop[workingSet.get(i)][k];
                    }
                    final double median = new QuickSelect().getMedian(kth);
                    //printSpaces(level);
                    //System.out.println(" Split by median " + median);

                    final List<Integer> l = new ArrayList<>();
                    final List<Integer> m = new ArrayList<>();
                    final List<Integer> h = new ArrayList<>();
                    split(pop, k, median, workingSet, l, m, h);

                    ndHelperA(pop, ranks, k, l, level + 1);
                    ndHelperB(pop, ranks, k - 1, l, m, level + 1);
                    ndHelperA(pop, ranks, k - 1, m, level + 1);

                    l.addAll(m);
                    ndHelperB(pop, ranks, k - 1, l, h, level + 1);

                    ndHelperA(pop, ranks, k, h, level + 1);

                    return;
                } else {
                    kPrev = pop[index][k];
                }
            }

            ndHelperA(pop, ranks, k - 1, workingSet, level + 1);
        }
    }

    /**
     * Assign ranks using the first two coordinates
     *
     * @param pop        population
     * @param ranks      ranks[i] is the rank of individual pop[i]
     * @param workingSet Indices of the population members that should be analyzed during the current run
     */
    private void sweepA(double[][] pop, int[] ranks, List<Integer> workingSet) {
        assert (pop.length == ranks.length);
        assert (workingSet.size() > 0 && workingSet.size() <= pop.length);

        List<Integer> tSet = new ArrayList<>();
        tSet.add(workingSet.get(0));

        for (int i = 1; i < workingSet.size(); ++i) {
            final int currIndex = workingSet.get(i);
            final double[] currIndividual = pop[currIndex];

            tSet.stream()
                    .filter(t -> pop[t][1] < currIndividual[1] ||
                            pop[t][1] == currIndividual[1] && pop[t][0] < currIndividual[0])
                    .mapToInt(t -> ranks[t])
                    .max()
                    .ifPresent(r -> ranks[currIndex] = Math.max(r + 1, ranks[currIndex]));

            final int currRank = ranks[currIndex];
            final List<Integer> newTSet = tSet.stream()
                    .filter(tIndex -> ranks[tIndex] != currRank)
                    .collect(Collectors.toList());
            newTSet.add(currIndex);
            tSet = newTSet;
        }
    }

    private void ndHelperB(double[][] pop, int[] ranks, int k, List<Integer> lSet, List<Integer> hSet, int level) {
        assert (pop.length == ranks.length);

//        printSpaces(level);
//        System.out.println("NHB. K = " + k + ", l = " + lSet + ", h = " + hSet);

        if (lSet == null || lSet.isEmpty() || hSet == null || hSet.isEmpty()) {
            return;
        } else if (lSet.size() == 1 || hSet.size() == 1) {
            for (int h : hSet) {
                //noinspection Convert2streamapi
                for (int l : lSet) {
                    if (dominates(pop[l], pop[h], k + 1) < 0)
                        ranks[h] = Math.max(ranks[h], ranks[l] + 1);
                }
            }
        } else if (k == 1) {
            sweepB(pop, ranks, lSet, hSet);
        } else {
            double lMin = Double.POSITIVE_INFINITY;
            double lMax = Double.NEGATIVE_INFINITY;
            for (int l : lSet) {
                lMin = Math.min(pop[l][k], lMin);
                lMax = Math.max(pop[l][k], lMax);
            }

            double hMin = Double.POSITIVE_INFINITY;
            double hMax = Double.NEGATIVE_INFINITY;
            for (int h : hSet) {
                hMin = Math.min(pop[h][k], hMin);
                hMax = Math.max(pop[h][k], hMax);
            }

            if (lMax <= hMin) {
                ndHelperB(pop, ranks, k - 1, lSet, hSet, level + 1);
            } else if (lMin <= hMax) {
                final double median = new QuickSelect().getMedian(
                        Stream.concat(lSet.stream(), hSet.stream())
                                .mapToDouble(index -> pop[index][k])
                                .toArray()
                );

                final List<Integer> l1 = new ArrayList<>();
                final List<Integer> m1 = new ArrayList<>();
                final List<Integer> h1 = new ArrayList<>();
                split(pop, k, median, lSet, l1, m1, h1);

                final List<Integer> l2 = new ArrayList<>();
                final List<Integer> m2 = new ArrayList<>();
                final List<Integer> h2 = new ArrayList<>();
                split(pop, k, median, hSet, l2, m2, h2);

                ndHelperB(pop, ranks, k, l1, l2, level + 1);
                ndHelperB(pop, ranks, k - 1, l1, m2, level + 1);
                ndHelperB(pop, ranks, k - 1, m1, m2, level + 1);

                l1.addAll(m1);
                ndHelperB(pop, ranks, k - 1, l1, h2, level + 1);

                ndHelperB(pop, ranks, k, h1, h2, level + 1);
            }
        }

    }

    private void sweepB(double[][] pop, int[] ranks, List<Integer> lSet, List<Integer> hSet) {
        assert (pop.length == ranks.length);

        List<Integer> tSet = new ArrayList<>();
        for (int h : hSet) {
            for (int l : lSet) {
                if (dominatesBy12(pop[l], pop[h]) < 0) {
                    final List<Integer> newTSet = new ArrayList<>();
                    boolean foundBetter = false;
                    for (int t : tSet) {
                        if (ranks[t] != ranks[l])
                            newTSet.add(t);
                        else if (ranks[t] == ranks[l] && pop[t][1] <= pop[l][1])
                            foundBetter = true;
                    }

                    if (!foundBetter) {
                        newTSet.add(l);
                        tSet = newTSet;
                    }
                }
            }

            int r = Integer.MIN_VALUE;
            for (int t : tSet) {
                if (dominatesBy12(pop[t], pop[h]) < 0) {
                    r = Math.max(r, ranks[t]);
                }
            }
            ranks[h] = Math.max(ranks[h], r + 1);
        }
    }

    /*
     * Utility methods
     */

    /**
     * Split the population into three parts around the median
     *
     * @param pop         Population
     * @param k           Objective to the population split on
     * @param medianValue Value of the {@code k}-th objective to split the population on
     * @param workingSet  input
     * @param l           output: p_{@code k} < {@code medianValue}, where p is a member of {@code pop}
     * @param m           output: p_{@code k} = {@code medianValue}, where p is a member of {@code pop}
     * @param h           output: p_{@code k} > {@code medianValue}, where p is a member of {@code pop}
     */
    private void split(double[][] pop,
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
            if (pop[i][k] < medianValue)
                l.add(i);
            else if (pop[i][k] == medianValue)
                m.add(i);
            else
                h.add(i);
        }
    }

    /**
     * Check the domination relation over the first two objectives.
     *
     * @param d1 First individual
     * @param d2 Second individual
     * @return -1 if {@code d1} dominates over {@code d2}. 1 if {@code d2} dominates over {@code d1}. 0 otherwise.
     */
    private int dominatesBy12(double[] d1, double[] d2) {
        return dominates(d1, d2, 2);
    }

    /**
     * Check the domination relation over the first K objectives.
     *
     * @param d1  First individual
     * @param d2  Second individual
     * @param dim Number of comparable coordinates in each individual (starting with zero!)
     *            In the most common notation k = {@code dim} + 1
     * @return -1 if {@code d1} dominates over {@code d2}. 1 if {@code d2} dominates over {@code d1}. 0 otherwise.
     */
    private int dominates(double[] d1, double[] d2, int dim) {
        return dominatesByFirstCoordinates(d1, d2, dim, 0, false, false);
    }

    /**
     * @param d1        First individual
     * @param d2        Second individual
     * @param dim       Number of comparable coordinates in each individual (starting with zero!)
     * @param currCoord Current comparable coordinate
     * @param d1less    At least one coordinate of d1 is less than corresponding coordinate of d2
     * @param d2less    At least one coordinate of d2 is less than corresponding coordinate of d1
     * @return -1 if {@code d1} dominates over {@code d2}. 1 if {@code d2} dominates over {@code d1}. 0 otherwise.
     */
    private int dominatesByFirstCoordinates(double[] d1, double[] d2, int dim, int currCoord, boolean d1less, boolean d2less) {
        assert (d1 != null && d1.length >= dim && d2 != null && d2.length >= dim);
        assert (currCoord < dim);

        if (d1[currCoord] < d2[currCoord]) {
            d1less = true;
        } else if (d1[currCoord] > d2[currCoord]) {
            d2less = true;
        }

        if (currCoord == dim - 1) {
            if (d1less && d2less || !d1less && !d2less)
                return 0;
            else if (d1less)
                return -1;
            else
                return 1;
        } else {
            return dominatesByFirstCoordinates(d1, d2, dim, currCoord + 1, d1less, d2less);
        }
    }

    private void printSpaces(int level) {
        for (int i = 0; i < level; ++i)
            System.out.print("\t");
    }
}
