package ru.itmo.iyakupov.manual;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.DTLZ.*;
import ru.itmo.iyakupov.NSGAIIMoeaRunner;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
    public void generateHypercubes() throws Exception {
        generateHypercube(3);
        generateHypercube(5);
        generateHypercube(10);
        generateHypercube(20);
    }

    private void generateHypercube(final int dim) throws Exception {
        final int genSize = 10000;
        final int datasetCount = 3;

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

    @Ignore
    @Test
    public void generateTwoLayersONPoints() throws Exception {
        generateParallelHyperplanes(3);
        generateParallelHyperplanes(5);
        generateParallelHyperplanes(10);
        generateParallelHyperplanes(20);
    }

    private void generateParallelHyperplanes(final int dim) throws Exception {
        final int genSize = 10000;
        final int datasetCount = 3;

        final double offset1 = 5;
        final double offset2 = 10;

        final String outFileName = "twoLayers" +
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
                final DoublesGeneration generation = new DoublesGeneration();

                final Front<double[]> f0 = new Front<>();
                final List<double[]> fit0 = new ArrayList<>();
                for (int j = 0; j < genSize / 2; ++j) {
                    final double[] individual = new double[dim];
                    Arrays.setAll(individual, value -> Math.random());
                    final double sum = Arrays.stream(individual).sum();
                    final double rate = offset1 / sum;
                    fit0.add(Arrays.stream(individual).map(d -> d * rate).toArray());
                }
                fit0.sort((o1, o2) -> {
                    for (int i1 = 0; i1 < o1.length; ++i1) {
                        if (o1[i1] < o2[i1])
                            return -1;
                        else if (o1[i1] > o2[i1])
                            return 1;
                    }
                    return 0;
                });
                f0.setFitnesses(fit0);

                final Front<double[]> f1 = new Front<>();
                final List<double[]> fit1 = new ArrayList<>();
                for (int j = 0; j < genSize / 2; ++j) {
                    final double[] individual = new double[dim];
                    Arrays.setAll(individual, value -> Math.random());
                    final double sum = Arrays.stream(individual).sum();
                    final double rate = offset2 / sum;
                    fit1.add(Arrays.stream(individual).map(d -> d * rate).toArray());
                }
                fit1.sort((o1, o2) -> {
                    for (int i1 = 0; i1 < o1.length; ++i1) {
                        if (o1[i1] < o2[i1])
                            return -1;
                        else if (o1[i1] > o2[i1])
                            return 1;
                    }
                    return 0;
                });
                f1.setId(1);
                f1.setFitnesses(fit1);

                final List<Front<double[]>> fronts2 = new ArrayList<>();
                fronts2.add(f0);
                fronts2.add(f1);
                generation.setFronts(fronts2);

                final double[] individual = new double[dim];
                Arrays.fill(individual, Double.POSITIVE_INFINITY);
                for (double[] doubles : fit0) {
                    for (int k = 0; k < individual.length; ++k) {
                        individual[k] = Math.min(individual[k], doubles[k]);
                    }
                }
                individual[0] += 0.0001;

                generation.setNextAddend(individual);

                generation.setId(i);
                generations.add(generation);
            }

            storage.serialize(fos);
        }
    }

    @Ignore
    @Test
    public void generateONLayers() throws Exception {
        generateParallelLines(3);
        generateParallelLines(5);
        generateParallelLines(10);
        generateParallelLines(20);
    }

    private void generateParallelLines(final int dim) throws Exception {
        final int genSize = 10000;
        final int datasetCount = 1;

        final double offset1 = 5;
        final double offset2 = 10;

        final String outFileName = "twoLines" +
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
                final DoublesGeneration generation = new DoublesGeneration();
                final List<Front<double[]>> fronts2 = new ArrayList<>();

                for (int j = 0; j < genSize / 2; ++j) {
                    final Front<double[]> f0 = new Front<>();
                    final List<double[]> fit0 = new ArrayList<>();

                    final int jj = j;
                    final double[] individual1 = new double[dim];
                    Arrays.setAll(individual1, value -> (jj + 2) * 2);
                    individual1[0] = offset1;
                    fit0.add(individual1);

                    final double[] individual2 = new double[dim];
                    Arrays.setAll(individual2, value -> (jj + 1) * 2 - 1);
                    individual2[0] = offset2;
                    fit0.add(individual2);

                    f0.setFitnesses(fit0);
                    f0.setId(j);
                    fronts2.add(f0);
                }

                generation.setFronts(fronts2);

                final double[] individual = new double[dim];
                Arrays.fill(individual, 2.0);
                individual[0] = offset1;

                generation.setNextAddend(individual);

                generation.setId(i);
                generations.add(generation);
            }

            storage.serialize(fos);
        }
    }

    @Ignore
    @Test
    public void nsga2TestDtlz1() throws Exception {
        final int[] dimensions = new int[]{4, 6, 8, 10};
        final int nDatasets = 2;
        for (int dim : dimensions) {
            for (int i = 0; i < nDatasets; ++i) {
                doTest(new DTLZ1(dim), i);
            }
        }
    }

}
