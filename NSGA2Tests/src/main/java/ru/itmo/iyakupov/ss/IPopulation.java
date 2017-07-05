package ru.itmo.iyakupov.ss;


import org.moeaframework.core.Solution;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IPopulation {
    List<Solution> getRandomSolutions(int count);

    @Deprecated
    List<Solution> getBestSolutions(int count);

    boolean removeWorstSolution();

    boolean addSolution(Solution solution);

    int size();

    Map<Integer, List<Solution>> getLayers();

    static double distance(Solution s1, Solution s2) {
        double distance = 0.0;

        for (int i = 0; i < s1.getNumberOfObjectives(); i++) {
            distance += Math.pow(s1.getObjective(i) - s2.getObjective(i), 2.0);
        }

        distance = Math.sqrt(distance);
        return distance;
    }


    static Index binarySearchForBucket(int[] buckets, int value) {
        int l = 0;
        int r = buckets.length - 1;

        int bucketIndex = -1;
        while (true) {
            final int probe = (l + r) / 2;
            if (buckets[probe] <= value) {
                bucketIndex = probe;
                if (l == probe)
                    break;
                l = probe;
            } else {
                if (r == probe)
                    break;
                r = probe;
            }
        }

        return new Index(bucketIndex, value - buckets[bucketIndex]);
    }
}
