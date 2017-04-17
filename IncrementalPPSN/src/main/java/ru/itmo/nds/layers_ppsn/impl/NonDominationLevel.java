package ru.itmo.nds.layers_ppsn.impl;

import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.util.RankedPopulation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.itmo.nds.util.ComparisonUtils.dominates;

public class NonDominationLevel<T> implements INonDominationLevel<T> {
    private ArrayList<T> members = new ArrayList<>();

    private final IncrementalPPSN<T> sorter;
    private final Function<T, double[]> objectivesExtractor;

    public NonDominationLevel(Function<T, double[]> objectivesExtractor) {
        this.objectivesExtractor = objectivesExtractor;
        this.sorter = new IncrementalPPSN<>(objectivesExtractor);
    }


    @Override
    public List<T> getMembers() {
        return members;
    }

    @Deprecated
    @Override
    public ArrayList<T> addMember(T addend) {
        final int[] ranks = new int[members.size()];
        @SuppressWarnings("unchecked") final T[] popArray = members.toArray((T[]) Array.newInstance(addend.getClass(), members.size()));
        final RankedPopulation<T> rp = sorter.performIncrementalNds(popArray, ranks, addend);
        final ArrayList<T> currLevel = new ArrayList<>(ranks.length + 1);
        final ArrayList<T> nextLevel = new ArrayList<>(ranks.length);
        for (int i = 0; i < rp.getPop().length; ++i) {
            if (rp.getRanks()[i] == 0)
                currLevel.add(rp.getPop()[i]);
            else
                nextLevel.add(rp.getPop()[i]);
        }
        members = currLevel;
        return nextLevel;
    }

    @Override
    public ArrayList<T> addMembers(List<T> addends) {
        final int[] ranks = new int[members.size()];
        final RankedPopulation<T> rp = sorter.addRankedMembers(members, ranks, addends, 0);
        final ArrayList<T> currLevel = new ArrayList<>(ranks.length + addends.size());
        final ArrayList<T> nextLevel = new ArrayList<>(ranks.length);
        for (int i = 0; i < rp.getPop().length; ++i) {
            if (rp.getRanks()[i] == 0)
                currLevel.add(rp.getPop()[i]);
            else
                nextLevel.add(rp.getPop()[i]);
        }
        members = currLevel;
        return nextLevel;
    }

    @Override
    public boolean dominatedByAnyPointOfThisLayer(T point) {
        final double[] pointObj = objectivesExtractor.apply(point);
        for (T member: members) {
            final double[] memberObj = objectivesExtractor.apply(member);
            if (memberObj[0] > pointObj[0])
                break;
            if (dominates(memberObj, pointObj, pointObj.length) < 0)
                return true;
        }
        return false;
    }

    @Override
    public NonDominationLevel<T> copy() {
        final NonDominationLevel<T> copy = new NonDominationLevel<>(objectivesExtractor);
        copy.getMembers().addAll(members);
        return copy;
    }

    @Override
    public String toString() {
        return "members=" + members.stream()
                .map(objectivesExtractor.andThen(Arrays::toString))
                .collect(Collectors.toList());
    }
}
