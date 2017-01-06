package ru.itmo.nds.layers_ppsn;

import ru.itmo.nds.util.RankedPopulation;

import java.util.List;

/**
 * Population that supports incremental addition of members
 */
public interface IPopulation {
    List<INonDominationLevel> getLevels();

    /**
     * @param point Evaluated individual
     * @return Its rank
     */
    int determineRank(double[] point);

    /**
     * @param addend Individual to add
     * @return Its rank after addition
     */
    int addPoint(double[] addend);

    /**
     * For testing purposes. It converts this population to the format, used by the classic implementations of PPSN.
     * @return Array of individuals and array of their ranks.
     */
    RankedPopulation toRankedPopulation();
}
