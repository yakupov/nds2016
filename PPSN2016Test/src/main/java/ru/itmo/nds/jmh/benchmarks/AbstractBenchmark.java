package ru.itmo.nds.jmh.benchmarks;

import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.jmh.benchmarks.utils.PpsnTestData;
import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.reference.ENLUSorter;
import ru.itmo.nds.util.RankedPopulation;

import java.util.*;

public abstract class AbstractBenchmark {
    private final PPSN2014 ppsn2014 = new PPSN2014();

    protected abstract Map<Integer, PpsnTestData> getPreparedTestData();

    protected DoublesGeneration getGeneration(FrontStorage frontStorage, int generationId) {
        return frontStorage.getRunConfigurations().iterator().next().getGenerations()
                .stream()
                .filter(gen -> gen.getId() == generationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Generation " + generationId + " not found in Store"));
    }

    protected int sortOneGeneration(int generationId, PPSN2014 sorter) {
        final PpsnTestData testData = Objects.requireNonNull(getPreparedTestData().get(generationId),
                "no cached test data for generation id " + generationId);
        final RankedPopulation res = sorter.performIncrementalNds(testData.getRankedPopulation().getPop(),
                testData.getRankedPopulation().getRanks(), testData.getNextAdddend());
        return res.getRanks().length;
    }

    int sortUsingLevelPPSN(int generationId, boolean debug) {
        final PpsnTestData testData = Objects.requireNonNull(getPreparedTestData().get(generationId),
                "no cached test data for generation id " + generationId);
        final Population population = testData.getPopulation().copy();
        final int rs = population.addPoint(testData.getNextAdddend());
        if (debug) {
            System.out.println("Stats for " + getClass().getSimpleName() + ", gen " + generationId + ":");
            System.out.println('\t' + population.getStats());
            //System.out.println('\t' + population.toString());
        }
        return rs;
    }

    protected int sortUsingLevelPPSN(int generationId) {
        return sortUsingLevelPPSN(generationId, false);
    }

    int sortUsingEnlu(int generationId, boolean validate) {
        final PpsnTestData testData = Objects.requireNonNull(getPreparedTestData().get(generationId),
                "no cached test data for generation id " + generationId);
        final Population population = testData.getPopulation();

        final Set<double[]> enluIndividuals = new HashSet<>();
        final List<Set<double[]>> enluLayers = new ArrayList<>(population.getLevels().size());
        for (INonDominationLevel nonDominationLevel: population.getLevels()) {
            final Set<double[]> enluLayer = new HashSet<>(nonDominationLevel.getMembers().size());
            for (double[] individual : nonDominationLevel.getMembers()) {
                enluLayer.add(individual);
                enluIndividuals.add(individual);
            }
            enluLayers.add(enluLayer);
        }

        final ENLUSorter sorter = new ENLUSorter(enluIndividuals, enluLayers);

        final int rs = sorter.addPoint(testData.getNextAdddend());
        if (validate) {
            sorter.validate();
        }
        return rs;
    }

    protected int sortUsingEnlu(int generationId) {
        return sortUsingEnlu(generationId, false);
    }

    protected int sortFullyUsingPpsn(int generationId) {
        final PpsnTestData testData = Objects.requireNonNull(getPreparedTestData().get(generationId),
                "no cached test data for generation id " + generationId);

        final double[][] oldPop = testData.getPopulation().toRankedPopulation().getPop();
        final double[][] newPop = Arrays.copyOf(oldPop, oldPop.length + 1);
        newPop[newPop.length - 1] = testData.getNextAdddend();

        final int[] ranks = ppsn2014.performNds(newPop);
        return ranks[0];
    }

}
