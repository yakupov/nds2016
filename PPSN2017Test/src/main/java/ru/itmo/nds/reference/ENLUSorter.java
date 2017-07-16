package ru.itmo.nds.reference;

import ru.itmo.nds.util.ComparisonUtils;

import java.util.*;

/**
 * An implementation of the NDS algorithm, proposed in the following article:
 * Ke Li, Kalyanmoy Deb, Qingfu Zhang and Qiang Zhang, "Efficient Non-domination Level Update Method for
 * Steady-State Evolutionary Multi-objective Optimization", COIN Report Number 2015022
 */
public class ENLUSorter {
    private final Set<double[]> individuals;
    private final List<Set<double[]>> ranks;

    public ENLUSorter() {
        this(new HashSet<>(), new ArrayList<>());
    }

    public ENLUSorter(Set<double[]> individuals, List<Set<double[]>> ranks) {
        this.individuals = individuals;
        this.ranks = ranks;
    }

    public int addPoint(double[] nInd) {
        if (individuals.contains(nInd)) {
            return detRankOfExPoint(nInd);
        } else {
            individuals.add(nInd);
        }

        for (int i = 0; i < ranks.size(); ++i) {
            boolean dominates, dominated, nd;
            dominates = dominated = nd = false;
            final Set<double[]> dominatedSet = new HashSet<>();

            for (double[] ind: ranks.get(i)) {
                int domComparisonResult = ComparisonUtils.dominates(nInd, ind, nInd.length);
                //nInd.compareDom(ind);
                if (domComparisonResult == 0)
                    nd = true;
                else if (domComparisonResult > 0) {
                    dominated = true;
                    break;
                } else {
                    dominatedSet.add(ind);
                    //ranks.get(i).remove(ind);
                    dominates = true;
                }
            }

            if (dominated)
                //noinspection UnnecessaryContinue
                continue;
            else if (!nd && dominates) {
                final Set<double[]> newRank = new HashSet<>();
                ranks.add(i, newRank);
                newRank.add(nInd);
                return i;
            } else {
                ranks.get(i).removeAll(dominatedSet);
                ranks.get(i).add(nInd);
                update(dominatedSet, i + 1);
                return i;
            }
        }

        Set<double[]> newRank = new HashSet<>();
        ranks.add(newRank);
        newRank.add(nInd);
        return ranks.size() - 1;
    }

    private void update(Set<double[]> dominatedSet, int i) {
        while (true) {
            if (i >= ranks.size()) {
                ranks.add(dominatedSet);
            } else {
                final Set<double[]> newDominatedSet = new HashSet<>();
                for (double[] iNew : dominatedSet) {
                    final Set<double[]> lastDom = new HashSet<>();
                    for (double[] iOld : ranks.get(i)) {
                        if (ComparisonUtils.dominates(iNew, iOld, iNew.length) < 0) {
                            //if (iNew.compareDom(iOld) < 0) {
                            //ranks.get(i).remove(iOld);
                            newDominatedSet.add(iOld);
                            lastDom.add(iOld);
                        }
                    }
                    ranks.get(i).removeAll(lastDom);
                    ranks.get(i).add(iNew);
                }
                if (!newDominatedSet.isEmpty()) {
                    //update(newDominatedSet, i + 1);
                    ++i;
                    dominatedSet = newDominatedSet;
                } else
                    return;
            }
        }
    }


    private int detRankOfExPoint(double[] ind) {
        for (int i = 0; i < ranks.size(); ++i) {
            if (ranks.get(i).contains(ind))
                return i;
        }
        throw new RuntimeException("Point not exists");
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranks.size(); ++i) {
            sb.append(i);
            sb.append('\n');
            sb.append(ranks.get(i).toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    public void validate() {
        for (int i = 0; i < ranks.size(); ++i) {
            for (double[] ind : ranks.get(i)) {
                int rankCalcd = 0;
                double[] determinator = null;
                for (double[] compInd : individuals) {
                    if (compInd != ind && ComparisonUtils.dominates(compInd, ind, compInd.length) < 0) {
                    //if (compInd != ind && compInd.compareDom(ind) < 0) {
                        int compRank = detRankOfExPoint(compInd);
                        if (compRank + 1 > rankCalcd) {
                            rankCalcd = compRank + 1;
                            determinator = compInd;
                        }
                    }
                }
                if (rankCalcd != i)
                    throw new RuntimeException("Population is sorted incorrectly. Point = " + Arrays.toString(ind) +
                            ", rk = " + i + ", should be = " + rankCalcd + ", determinator = " + Arrays.toString(determinator));

            }
        }
    }
}
