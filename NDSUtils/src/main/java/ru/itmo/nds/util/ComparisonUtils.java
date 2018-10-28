package ru.itmo.nds.util;

/**
 * Utilities for comparison of points
 */
public class ComparisonUtils {
    /**
     * Check the domination relation over the first K objectives.
     *
     * @param d1  First individual
     * @param d2  Second individual
     * @param dim Number of comparable coordinates in each individual (not max. index!)
     *            In the most common max. compared index will be {@code dim} - 1
     * @return -1 if {@code d1} dominates over {@code d2}. 1 if {@code d2} dominates over {@code d1}. 0 otherwise.
     */
    public static int dominates(double[] d1, double[] d2, int dim) {
        return dominatesByFirstCoordinatesV2(d1, d2, dim);
        //return dominatesByFirstCoordinates(d1, d2, dim, 0, false, false);
    }

    /**
     * @param d1        First individual
     * @param d2        Second individual
     * @param dim       Number of comparable coordinates in each individual (not max. index!)
     * @param currCoord Current comparable coordinate
     * @param d1less    At least one coordinate of d1 is less than corresponding coordinate of d2
     * @param d2less    At least one coordinate of d2 is less than corresponding coordinate of d1
     * @return -1 if {@code d1} dominates over {@code d2}. 1 if {@code d2} dominates over {@code d1}. 0 otherwise.
     */
    private static int dominatesByFirstCoordinates(double[] d1, double[] d2, int dim, int currCoord, boolean d1less, boolean d2less) {
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

    private static int dominatesByFirstCoordinatesV2(double[] d1, double[] d2, int dim) {
        boolean d1less = false;
        boolean d2less = false;
        for (int currCoord = 0; currCoord < dim; ++currCoord) {
            if (d1[currCoord] < d2[currCoord]) {
                d1less = true;
            } else if (d1[currCoord] > d2[currCoord]) {
                d2less = true;
            }

            if (d1less && d2less) {
                return 0;
            }
        }

        if (d1less)
            return -1;
        else if (d2less)
            return 1;
        else
            return 0;
    }

}
