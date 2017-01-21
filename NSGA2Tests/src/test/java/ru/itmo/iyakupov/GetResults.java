package ru.itmo.iyakupov;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.DTLZ.*;
import ru.itmo.iyakupov.nsga2nds.Individual;
import ru.itmo.iyakupov.nsga2nds.NSGAIINonDominatingSorter;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.Front;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.front_storage.RunConfiguration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Obtain test data for our NDS
 */
public class GetResults {
    private void doTest(Problem problem, int runId) throws Exception {
        final int genSize = 10000;
        final int iterCount = 100;

        final String outFileName = problem.getName().toLowerCase() +
                "_dim" + problem.getNumberOfObjectives() +
                "_gen" + genSize +
                "_iter" + iterCount +
                "_dataset" + runId +
                ".json";

        final NSGAII nsga2 = NSGAIIMoeaRunner.newNSGAII(genSize, problem);

        final FrontStorage storage = new FrontStorage();
        if (Files.exists(Paths.get(outFileName))) {
            try (FileInputStream fis = new FileInputStream(outFileName)) {
                storage.deserialize(fis);
            }
        }

        if (storage.getRunConfigurations() == null)
            storage.setRunConfigurations(new ArrayList<>());

        try (FileOutputStream fos = new FileOutputStream(outFileName)) {
            final RunConfiguration rc = new RunConfiguration();
            rc.setTimestamp(LocalDateTime.now());
            rc.setNumberOfIterations(iterCount);
            rc.setSizeOfGeneration(genSize);

            final ArrayList<DoublesGeneration> generations = new ArrayList<>();
            rc.setGenerations(generations);

            storage.getRunConfigurations().add(rc);

            nsga2.step(); //init
            for (int i = 0; i < iterCount; ++i) {
                if (i % 10 == 0) {
                    final DoublesGeneration generation = NSGAIIMoeaRunner.getCurrentPopulation(nsga2);
                    generation.setId(i);
                    generations.add(generation);
                }
                nsga2.step();
            }

            storage.serialize(fos);
        }
    }

    @Ignore
    @Test
    public void nsga2TestDtlz() throws Exception {
        doTest(new DTLZ1(3), 1);
        doTest(new DTLZ1(3), 2);
        doTest(new DTLZ1(3), 3);
        doTest(new DTLZ2(3), 1);
        doTest(new DTLZ2(3), 2);
        doTest(new DTLZ2(3), 3);
        doTest(new DTLZ3(3), 1);
        doTest(new DTLZ3(3), 2);
        doTest(new DTLZ3(3), 3);
        doTest(new DTLZ4(3), 1);
        doTest(new DTLZ4(3), 2);
        doTest(new DTLZ4(3), 3);
        doTest(new DTLZ7(3), 1);
        doTest(new DTLZ7(3), 2);
        doTest(new DTLZ7(3), 3);
    }

    @Ignore
    @Test
    public void generateRandomTestData() throws Exception {
        final int genSize = 10000;
        final int datasetCount = 3;
        final int dim = 3;

        final String outFileName = "uniform" +
                "_dim" + dim +
                "_gen" + genSize +
                ".json";

        final FrontStorage storage = new FrontStorage();
        if (Files.exists(Paths.get(outFileName))) {
            try (FileInputStream fis = new FileInputStream(outFileName)) {
                storage.deserialize(fis);
            }
        }

        if (storage.getRunConfigurations() == null)
            storage.setRunConfigurations(new ArrayList<>());

        try (FileOutputStream fos = new FileOutputStream(outFileName)) {
            final RunConfiguration rc = new RunConfiguration();
            rc.setTimestamp(LocalDateTime.now());
            rc.setNumberOfIterations(0);
            rc.setSizeOfGeneration(genSize);

            final ArrayList<DoublesGeneration> generations = new ArrayList<>();
            rc.setGenerations(generations);

            storage.getRunConfigurations().add(rc);

            for (int i = 0; i < datasetCount; ++i) {
                final List<Individual> pop = new ArrayList<>(genSize);
                for (int j = 0; j < genSize; ++j) {
                    final double[] individual = new double[dim];
                    Arrays.setAll(individual, value -> Math.random());
                    pop.add(new Individual(individual));
                }
                final List<List<Individual>> fronts1 = NSGAIINonDominatingSorter.sort(pop);

                final DoublesGeneration generation = new DoublesGeneration();
                final List<Front<double[]>> fronts2 = new ArrayList<>();
                for (int j = 0; j < fronts1.size(); ++j) {
                    final Front<double[]> front2 = new Front<>();
                    front2.setId(j);
                    front2.setFitnesses(fronts1.get(j).stream().map(Individual::getFitnesses).collect(Collectors.toList()));
                    fronts2.add(front2);
                }
                generation.setFronts(fronts2);

                final double[] individual = new double[dim];
                Arrays.setAll(individual, value -> Math.random());
                generation.setNextAddend(individual);

                generation.setId(i);
                generations.add(generation);
            }

            storage.serialize(fos);
        }
    }
}
