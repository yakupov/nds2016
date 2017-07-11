package ru.itmo.iyakupov.nsga2nds;

import java.util.ArrayList;
import java.util.List;

/**
 * Old implementation of classic Deb's NDS from NSGA2.
 */
public class NSGAIINonDominatingSorter {
    public static List<List<Individual>> sort(List<Individual> pop) {
        final List<List<Individual>> fronts = new ArrayList<>();

        for (Individual individual: pop) {
            individual.nDominating = 0;
            individual.dominated = new ArrayList<>();
        }

        for (int i = 0; i < pop.size(); ++i) {
            for (int j = 0; j < pop.size(); ++j) {
                if (i != j) {
                    DomStatus domStatus = dominates(pop.get(i).fitnesses, pop.get(j).fitnesses);
                    if (domStatus == DomStatus.DOMINATES) {
                        pop.get(i).dominated.add(pop.get(j));
                    } else if (domStatus == DomStatus.DOMINATED) {
                        pop.get(i).nDominating++;
                    }
                }
            }

            //System.err.println(pop.get(i).nDominating);
            if (pop.get(i).nDominating == 0) {
                addToFront(fronts, 1, pop.get(i));
            }
        }

        int currFront = 0;
        while (currFront < fronts.size()) {
            List<Individual> newFront = new ArrayList<>();
            for (Individual fromCurrFront : fronts.get(currFront)) {
                for (Individual dominatedByCurr : fromCurrFront.dominated) {
                    dominatedByCurr.nDominating--;
                    if (dominatedByCurr.nDominating == 0) {
                        newFront.add(dominatedByCurr);
                    }
                }
            }
            if (newFront.size() > 0) {
                fronts.add(newFront);
                currFront++;
            } else
                return fronts;
        }

        return fronts;
    }

    /**
     * @param fronts     List of fronts
     * @param rank       Added individual's rank
     * @param individual Added individual
     */
    private static void addToFront(List<List<Individual>> fronts, int rank, Individual individual) {
        //System.err.println("ATF " + rank);
        final List<Individual> workingFront;
        if (rank <= fronts.size()) {
            workingFront = fronts.get(rank - 1);
        } else {
            workingFront = new ArrayList<>();
            fronts.add(workingFront);
        }
        workingFront.add(individual);
    }

    private static DomStatus dominates(double[] p1, double[] p2) {
        boolean lt = false;
        boolean gt = false;
        for (int i = 0; i < p1.length; ++i) {
            if (p1[i] > p2[i]) {
                gt = true;
            } else if (p1[i] < p2[i]) {
                lt = true;
            }
        }

        if (lt && !gt) { //p1 dominates p2
            return DomStatus.DOMINATES;
        } else if (!lt && gt) { //p1 dominates p2
            return DomStatus.DOMINATED;
        }

        return DomStatus.EQUALS;
    }
}
