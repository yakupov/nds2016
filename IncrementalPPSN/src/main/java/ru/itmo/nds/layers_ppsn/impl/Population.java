package ru.itmo.nds.layers_ppsn.impl;

import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.layers_ppsn.IPopulation;
import ru.itmo.nds.util.RankedIndividual;
import ru.itmo.nds.util.RankedPopulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Population implements IPopulation {
    private final ArrayList<INonDominationLevel> nonDominationLevels = new ArrayList<>();

    private int lastNumberOfMovements = 0;
    private int lastSumOfMovements = 0;


    @Override
    public List<INonDominationLevel> getLevels() {
        return nonDominationLevels;
    }

    @Override
    public int determineRank(double[] point) {
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
    public int addPoint(double[] addend) {
        lastNumberOfMovements = 0;
        lastSumOfMovements = 0;

        final int rank = determineRank(addend);
        if (rank >= nonDominationLevels.size()) {
            final NonDominationLevel level = new NonDominationLevel();
            level.getMembers().add(addend);
            nonDominationLevels.add(level);
        } else {
            List<double[]> addends = Collections.singletonList(addend);
            int i = rank;
            int prevSize = -1;
            while (!addends.isEmpty() && i < nonDominationLevels.size()) {
                ++lastNumberOfMovements;
                lastSumOfMovements += addends.size();

                if (prevSize == addends.size()) { //Whole level was pushed
                    final NonDominationLevel level = new NonDominationLevel();
                    level.getMembers().addAll(addends);
                    nonDominationLevels.add(i, level);
                    return rank;
                }

                final INonDominationLevel level = nonDominationLevels.get(i);
                prevSize = level.getMembers().size();
                addends = level.addMembers(addends);
                i++;
            }
            if (!addends.isEmpty()) {
                final NonDominationLevel level = new NonDominationLevel();
                level.getMembers().addAll(addends);
                nonDominationLevels.add(level);
            }
        }

        return rank;
    }

    @Override
    public RankedPopulation toRankedPopulation() {
        final int popSize = nonDominationLevels.stream().mapToInt(level -> level.getMembers().size()).sum();
        final double[][] pop = new double[popSize][];
        final int[] ranks = new int[popSize];
        int j = 0;
        for (int i = 0; i < nonDominationLevels.size(); ++i) {
            for (double[] d: nonDominationLevels.get(i).getMembers()) {
                pop[j] = d;
                ranks[j] = i;
                j++;
            }
        }

        final int[] sortedRanks = RankedIndividual.sortRanksForLexSortedPopulation(ranks, pop);
        Arrays.sort(pop, (o1, o2) -> {
            for (int i = 0; i < o1.length; ++i) {
                if (o1[i] < o2[i])
                    return -1;
                else if (o1[i] > o2[i])
                    return 1;
            }
            return 0;
        });
        return new RankedPopulation(pop, sortedRanks);
    }

    /**
     * @return A copy of this population. All layers are also copied.
     */
    public Population copy() {
        final Population copy = new Population();
        for (INonDominationLevel level: nonDominationLevels) {
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
}
