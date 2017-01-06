package ru.itmo.nds.layers_ppsn.impl;

import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.util.RankedPopulation;

import java.util.ArrayList;
import java.util.List;

import static ru.itmo.nds.util.ComparisonUtils.dominates;

public class NonDominationLevel implements INonDominationLevel {
    private final IncrementalPPSN sorter = new IncrementalPPSN();
    private ArrayList<double[]> members = new ArrayList<>();

    @Override
    public List<double[]> getMembers() {
        return members;
    }

    @Deprecated
    @Override
    public ArrayList<double[]> addMember(double[] addend) {
        final int[] ranks = new int[members.size()];
        final RankedPopulation rp = sorter.performIncrementalNds(members.toArray(new double[members.size()][]), ranks, addend);
        final ArrayList<double[]> currLevel = new ArrayList<>(ranks.length + 1);
        final ArrayList<double[]> nextLevel = new ArrayList<>(ranks.length + 1);
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
    public ArrayList<double[]> addMembers(List<double[]> addends) {
        final int[] ranks = new int[members.size()];
        final RankedPopulation rp = sorter.addRankedMembers(members, ranks, addends, 0);
        final ArrayList<double[]> currLevel = new ArrayList<>(ranks.length + 1);
        final ArrayList<double[]> nextLevel = new ArrayList<>(ranks.length + 1);
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
    public boolean dominatedByAnyPointOfThisLayer(double[] point) {
        for (double[] d: members) {
            if (d[0] > point[0])
                break;
            if (dominates(d, point, point.length) < 0)
                return true;
        }
        return false;
    }

    @Override
    public NonDominationLevel copy() {
        final NonDominationLevel copy = new NonDominationLevel();
        copy.getMembers().addAll(members);
        return copy;
    }
}
