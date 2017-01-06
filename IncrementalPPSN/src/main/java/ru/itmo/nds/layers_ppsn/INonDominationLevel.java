package ru.itmo.nds.layers_ppsn;

import java.util.ArrayList;
import java.util.List;

/**
 * Non-domination level
 */
public interface INonDominationLevel {
    /**
     * @return Lexicographically sorted members of this layer
     */
    List<double[]> getMembers();

    /**
     * Add new point (assuming that its rank equals the rank of this level).
     *
     * @param addend New point
     * @return A set of evicted points that should be moved to the next level
     */
    ArrayList<double[]> addMember(double[] addend);

    /**
     * Add new points (assuming that their ranks equal the rank of this level).
     *
     * @param addends New points
     * @return A set of evicted points that should be moved to the next level
     */
    ArrayList<double[]> addMembers(List<double[]> addends);

    /**
     * @return true if {@code point} is dominated by any member of this layer
     */
    boolean dominatedByAnyPointOfThisLayer(double[] point);

    /**
     * @return Shallow copy of this layer
     */
    INonDominationLevel copy();
}
