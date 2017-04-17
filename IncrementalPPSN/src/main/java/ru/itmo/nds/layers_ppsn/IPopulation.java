package ru.itmo.nds.layers_ppsn;

import ru.itmo.nds.util.RankedPopulation;

import java.util.List;

/**
 * Population that supports incremental addition of members
 */
public interface IPopulation<T> {
    List<INonDominationLevel<T>> getLevels();

    /**
     * @param point Evaluated individual
     * @return Its rank
     */
    int determineRank(T point);

    /**
     * @param addend Individual to add
     * @return Its rank after addition
     */
    int addPoint(T addend);

    /**
     * For testing purposes. It converts this population to the format, used by the classic implementations of PPSN.
     * @return Array of individuals and array of their ranks.
     */
    RankedPopulation<T> toRankedPopulation();
}
