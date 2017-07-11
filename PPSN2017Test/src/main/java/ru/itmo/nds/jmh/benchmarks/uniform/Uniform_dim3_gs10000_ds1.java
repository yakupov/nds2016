package ru.itmo.nds.jmh.benchmarks.uniform;

import org.openjdk.jmh.annotations.*;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.Front;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.jmh.benchmarks.AbstractBenchmark;
import ru.itmo.nds.jmh.benchmarks.utils.PpsnTestData;
import ru.itmo.nds.layers_ppsn.impl.NonDominationLevel;
import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.util.RankedPopulation;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 12)
@Measurement(iterations = 4)
@Fork(value = 2)
public class Uniform_dim3_gs10000_ds1 extends AbstractBenchmark {
    private final IncrementalPPSN<double[]> incrementalPPSN = new IncrementalPPSN<>(d -> d);
    private final PPSN2014<double[]> ppsn2014 = new PPSN2014<>(d -> d);

    private Map<Integer, PpsnTestData> preparedTestData;
    private FrontStorage frontStorage;

    @Override
    protected Map<Integer, PpsnTestData> getPreparedTestData() {
        return preparedTestData;
    }

    @SuppressWarnings("WeakerAccess")
    @Setup(Level.Invocation)
    public void prepareTestData() throws Exception {
        if (frontStorage == null) {
            frontStorage = new FrontStorage();
            try (InputStream is = Uniform_dim3_gs10000_ds1.class
                    .getResourceAsStream("uniform_dim3_gen10000_dataset1.json")) {
                Objects.requireNonNull(is, "Test data not found");
                frontStorage.deserialize(is);
            }
        }

        preparedTestData = new HashMap<>();

        for (int i = 0; i < 3; i++) {
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

            preparedTestData.put(i, new PpsnTestData(nextAddend, rp, population, null, enluIndividuals, enluLayers));        }
    }

    @Benchmark
    public int enluTestDataset0() {
        return sortUsingEnlu(0);
    }

    @Benchmark
    public int incPpsnFastSweepDataset0() {
        return sortOneGeneration(0, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnDataset0() {
        return sortOneGeneration(0, ppsn2014);
    }

    @Benchmark
    public int levelPpsnDataset0() {
        return sortUsingLevelPPSN(0);
    }

    @Benchmark
    public int oldPpsnDataset0() {
        return sortFullyUsingPpsn(0);
    }

    @Benchmark
    public int enluTestDataset1() {
        return sortUsingEnlu(1);
    }

    @Benchmark
    public int incPpsnFastSweepDataset1() {
        return sortOneGeneration(1, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnDataset1() {
        return sortOneGeneration(1, ppsn2014);
    }

    @Benchmark
    public int levelPpsnDataset1() {
        return sortUsingLevelPPSN(1);
    }

    @Benchmark
    public int oldPpsnDataset1() {
        return sortFullyUsingPpsn(1);
    }
    
    @Benchmark
    public int enluTestDataset2() {
        return sortUsingEnlu(2);
    }

    @Benchmark
    public int incPpsnFastSweepDataset2() {
        return sortOneGeneration(2, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnDataset2() {
        return sortOneGeneration(2, ppsn2014);
    }

    @Benchmark
    public int levelPpsnDataset2() {
        return sortUsingLevelPPSN(2);
    }

    @Benchmark
    public int oldPpsnDataset2() {
        return sortFullyUsingPpsn(2);
    }
}
