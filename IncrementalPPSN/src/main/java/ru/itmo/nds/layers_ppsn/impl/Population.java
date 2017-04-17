package ru.itmo.nds.layers_ppsn.impl;

import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.layers_ppsn.IPopulation;
import ru.itmo.nds.util.RankedIndividual;
import ru.itmo.nds.util.RankedPopulation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Population<T> implements IPopulation<T> {
    private final ArrayList<INonDominationLevel<T>> nonDominationLevels = new ArrayList<>();

    private int lastNumberOfMovements = 0;
    private int lastSumOfMovements = 0;

    private final Function<T, double[]> objectivesExtractor;

    public Population(Function<T, double[]> objectivesExtractor) {
        this.objectivesExtractor = objectivesExtractor;
    }

    @Override
    public List<INonDominationLevel<T>> getLevels() {
        return nonDominationLevels;
    }

    @Override
    public int determineRank(T point) {
        int l = 0;
        int r = nonDominationLevels.size() - 1;
        int lastNonDominating = r + 1;
        while (l <= r) {
            final int test = (l + r) / 2;
            if (!nonDominationLevels.get(test).dominatedByAnyPointOfThisLayer(point)) {
                lastNonDominating = test;
                r = test - 1;
            } else {
                l = test + 1;
            }
        }

        return lastNonDominating;
    }

    @Override
    public int addPoint(T addend) {
        lastNumberOfMovements = 0;
        lastSumOfMovements = 0;

        final int rank = determineRank(addend);
        if (rank >= nonDominationLevels.size()) {
            final NonDominationLevel<T> level = new NonDominationLevel<>(objectivesExtractor);
            level.getMembers().add(addend);
            nonDominationLevels.add(level);
        } else {
            List<T> addends = Collections.singletonList(addend);
            int i = rank;
            int prevSize = -1;
            while (!addends.isEmpty() && i < nonDominationLevels.size()) {
                ++lastNumberOfMovements;
                lastSumOfMovements += addends.size();

                if (prevSize == addends.size()) { //Whole level was pushed
                    final NonDominationLevel<T> level = new NonDominationLevel<>(objectivesExtractor);
                    level.getMembers().addAll(addends);
                    nonDominationLevels.add(i, level);
                    return rank;
                }

                final INonDominationLevel<T> level = nonDominationLevels.get(i);
                prevSize = level.getMembers().size();
                addends = level.addMembers(addends);
                i++;
            }
            if (!addends.isEmpty()) {
                final NonDominationLevel<T> level = new NonDominationLevel<>(objectivesExtractor);
                level.getMembers().addAll(addends);
                nonDominationLevels.add(level);
            }
        }

        return rank;
    }

    @Override
    public RankedPopulation<T> toRankedPopulation() {
        final int popSize = nonDominationLevels.stream().mapToInt(level -> level.getMembers().size()).sum();

        final T[] pop;
        final int[] sortedRanks;
        if (popSize > 0) {
            //noinspection unchecked
            pop = (T[]) Array.newInstance(nonDominationLevels.get(0).getMembers().get(0).getClass(), popSize);
            final int[] ranks = new int[popSize];
            int j = 0;
            for (int i = 0; i < nonDominationLevels.size(); ++i) {
                for (T d : nonDominationLevels.get(i).getMembers()) {
                    pop[j] = d;
                    ranks[j] = i;
                    j++;
                }
            }

            sortedRanks = RankedIndividual.sortRanksForLexSortedPopulation(ranks, pop, objectivesExtractor);

            Arrays.sort(pop, (o1, o2) -> {
                final double[] o1Obj = objectivesExtractor.apply(o1);
                final double[] o2Obj = objectivesExtractor.apply(o2);
                for (int i = 0; i < o1Obj.length; ++i) {
                    if (o1Obj[i] < o2Obj[i])
                        return -1;
                    else if (o1Obj[i] > o2Obj[i])
                        return 1;
                }
                return 0;
            });
        } else {
            pop = null;
            sortedRanks = null;
        }
        return new RankedPopulation<>(pop, sortedRanks);
    }

    /**
     * @return A copy of this population. All layers are also copied.
     */
    public Population copy() {
        final Population<T> copy = new Population<>(objectivesExtractor);
        for (INonDominationLevel<T> level: nonDominationLevels) {
            copy.getLevels().add(level.copy());
        }
        return copy;
    }

    public String getStats() {
        return "Population{" +
                "Number of times when point(s) were inserted into a ND level = " + lastNumberOfMovements +
                ", Total number of (re-)inserted points = " + lastSumOfMovements +
                '}';
    }

    @Override
    public String toString() {
        return "Population levels {" + nonDominationLevels + '}';
    }
}
