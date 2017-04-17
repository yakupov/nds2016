package ru.itmo.nds.jmh.benchmarks;

import org.openjdk.jmh.annotations.*;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.Front;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.jmh.benchmarks.utils.PpsnTestData;
import ru.itmo.nds.layers_ppsn.impl.NonDominationLevel;
import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.reference.treap2015.Double2DIndividual;
import ru.itmo.nds.reference.treap2015.TreapPopulation;
import ru.itmo.nds.util.RankedPopulation;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractZdtBenchmark extends AbstractDtlzZdtBenchmark {
    private Map<Integer, PpsnTestData> preparedTestData;
    private FrontStorage frontStorage;

    @Override
    protected Map<Integer, PpsnTestData> getPreparedTestData() {
        return preparedTestData;
    }

    @SuppressWarnings("WeakerAccess")
    @Setup(Level.Invocation)
    @Override
    public void prepareTestData() throws Exception {
        if (frontStorage == null)
            frontStorage = loadFrontsFromResources();

        preparedTestData = new HashMap<>();

        for (int i = 0; i <= 90; i += 10) {
            final DoublesGeneration generation = getGeneration(frontStorage, i);
            final double[] nextAddend = generation.getNextAddend();
            final RankedPopulation<double[]> rp = generation.getLexSortedRankedPop();

            final Population<double[]> population = new Population<>(d -> d);
            generation.getFronts().stream()
                    .sorted(Comparator.comparingInt(Front::getId))
                    .map(f -> {
                        final NonDominationLevel<double[]> level = new NonDominationLevel<>(d -> d);
                        level.getMembers().addAll(f.getFitnesses());

                        (level.getMembers()).sort((o1, o2) -> {
                            for (int objIndex = 0; objIndex < o1.length; ++objIndex) {
                                if (o1[objIndex] < o2[objIndex])
                                    return -1;
                                else if (o1[objIndex] > o2[objIndex])
                                    return 1;
                            }
                            return 0;
                        });

                        return level;
                    })
                    .forEach(level -> population.getLevels().add(level));

            final Set<double[]> enluIndividuals = new HashSet<>();
            final List<Set<double[]>> enluLayers = generation.getFronts().stream()
                    .sorted(Comparator.comparingInt(Front::getId))
                    .map(f -> {
                        final Set<double[]> enluLayer = new HashSet<>();
                        enluLayer.addAll(f.getFitnesses());
                        enluIndividuals.addAll(f.getFitnesses());
                        return enluLayer;
                    })
                    .collect(Collectors.toList());

            final TreapPopulation treapPopulation = new TreapPopulation();
            for (Set<double[]> layer : enluLayers) {
                for (double[] ind: layer) {
                    treapPopulation.addPoint(new Double2DIndividual(ind));
                }
            }

            preparedTestData.put(i, new PpsnTestData(nextAddend, rp, population, treapPopulation, enluIndividuals, enluLayers));
        }
    }

    private int sortUsingTreap2015(int generationId, boolean validate) {
        final PpsnTestData testData = Objects.requireNonNull(getPreparedTestData().get(generationId),
                "no cached test data for generation id " + generationId);

        final TreapPopulation tp = testData.getTreapPopulation();
        tp.addPoint(new Double2DIndividual(testData.getNextAdddend()));

        if (validate)
            tp.validate();

        return tp.size();
    }

    private int sortUsingTreap2015(int generationId) {
        return sortUsingTreap2015(generationId, false);
    }

    @Benchmark
    public int treap2015Gen0() {
        return sortUsingTreap2015(0);
    }

    @Benchmark
    public int treap2015Gen10() {
        return sortUsingTreap2015(10);
    }

    @Benchmark
    public int treap2015Gen20() {
        return sortUsingTreap2015(20);
    }

    @Benchmark
    public int treap2015Gen30() {
        return sortUsingTreap2015(30);
    }

    @Benchmark
    public int treap2015Gen40() {
        return sortUsingTreap2015(40);
    }

    @Benchmark
    public int treap2015Gen50() {
        return sortUsingTreap2015(50);
    }

    @Benchmark
    public int treap2015Gen60() {
        return sortUsingTreap2015(60);
    }

    @Benchmark
    public int treap2015Gen70() {
        return sortUsingTreap2015(70);
    }

    @Benchmark
    public int treap2015Gen80() {
        return sortUsingTreap2015(80);
    }

    @Benchmark
    public int treap2015Gen90() {
        return sortUsingTreap2015(90);
    }
}
