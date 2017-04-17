package ru.itmo.iyakupov.ss;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.nds.PPSN2014;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.moeaframework.core.NondominatedSorting.CROWDING_ATTRIBUTE;

public class PPSN2014Population implements IPopulation {
    private final PPSN2014<Solution> sorter;
    private Solution[] pop;
    private int[] ranks;
    private final Comparator<Solution> comparator;

    public PPSN2014Population(PPSN2014<Solution> sorter, Solution[] pop, int[] ranks, Comparator<Solution> comparator) {
        this.sorter = sorter;
        this.pop = pop;
        this.ranks = ranks;
        this.comparator = comparator;
    }



    @Override
    public List<Solution> getBestSolutions(int count) {

    }

    /**
     * We assume that the population is sorted
     *
     * @return whether something was deleted
     */
    @Override
    public boolean removeWorstSolution() {
        if (pop.length == 0)
            return false;
        else {
            List<Integer> candidateIndices = new ArrayList<>();
            int maxRank = 0;
            for (int i = 0; i < pop.length; ++i) {
                if (ranks[i] == maxRank)
                    candidateIndices.add(i);
                else if (ranks[i] > maxRank) {
                    candidateIndices = new ArrayList<>();
                    candidateIndices.add(i);
                    maxRank = ranks[i];
                }
            }
            //updateCrowdingDistance(candidateIndices); //TODO: check

            int worstIndex = candidateIndices.get(0);
            for (int i = 1; i < candidateIndices.size(); ++i) {
                if (comparator.compare(pop[worstIndex], pop[candidateIndices.get(i)]) > 0)
                    worstIndex = i;
            }

            final Solution[] oldPop = pop;
            final int[] oldRanks = ranks;

            pop = new Solution[pop.length - 1];
            System.arraycopy(oldPop, 0, pop, 0, worstIndex);
            System.arraycopy(oldPop, worstIndex + 1, pop, worstIndex, pop.length - worstIndex - 1);

            ranks = new int[pop.length - 1];
            System.arraycopy(oldRanks, 0, ranks, 0, worstIndex);
            System.arraycopy(oldRanks, worstIndex + 1, ranks, worstIndex, pop.length - worstIndex - 1);
        }

    }

    @Override
    public boolean addSolution(Solution solution) {
        return false;
        //TODO
    }

    @Override
    public int size() {
        return pop.length;
    }

    @Override
    public Map<Integer, List<Solution>> getLayers() {
        return null;
        //TODO
    }

    private void updateCrowdingDistance(List<Integer> candidates) {
        final List<Solution> nonDuplicates = new ArrayList<>();
        final Set<Integer> toEvict = new HashSet<>();
        for (int candidate : candidates) {
            pop[candidate].setAttribute(CROWDING_ATTRIBUTE, 0.0);

            for (Solution nd: nonDuplicates) {
                if (IPopulation.distance(nd, pop[candidate]) < Settings.EPS) {
                    toEvict.add(candidate);
                    break;
                }
            }

            if (!toEvict.contains(candidate))
                nonDuplicates.add(pop[candidate]);
        }

        // then compute the crowding distance for the unique solutions
        int n = candidates.size();

        if (n < 3) {
            for (int candidate : candidates) {
                pop[candidate].setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);
            }
        } else {
            int numberOfObjectives = pop[0].getNumberOfObjectives();

            for (int i = 0; i < numberOfObjectives; i++) {
                final List<Solution> front = candidates.stream().map(cand -> pop[cand]).collect(Collectors.toList());
                front.sort(new ObjectiveComparator(i));

                double minObjective = front.get(0).getObjective(i);
                double maxObjective = front.get(n - 1).getObjective(i);

                front.get(0).setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);
                front.get(n - 1).setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);

                for (int j = 1; j < n - 1; j++) {
                    double distance = (Double)front.get(j).getAttribute(
                            CROWDING_ATTRIBUTE);
                    distance += (front.get(j + 1).getObjective(i) -
                            front.get(j - 1).getObjective(i))
                            / (maxObjective - minObjective);
                    front.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
                }
            }
        }

        //TODO: evict
    }

}
