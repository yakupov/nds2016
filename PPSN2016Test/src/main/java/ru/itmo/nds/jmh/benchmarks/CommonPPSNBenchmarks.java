package ru.itmo.nds.jmh.benchmarks;

import org.openjdk.jmh.annotations.*;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.Front;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.jmh.benchmarks.utils.PpsnTestData;
import ru.itmo.nds.layers_ppsn.impl.NonDominationLevel;
import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.util.RankedPopulation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 6)
@Measurement(iterations = 4)
@Fork(value = 2)
public abstract class CommonPPSNBenchmarks {
    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    private Map<Integer, PpsnTestData> preparedTestData;

    abstract FrontStorage loadFrontsFromResources() throws Exception;

    private DoublesGeneration getGeneration(FrontStorage frontStorage, int generationId) {
        return frontStorage.getRunConfigurations().iterator().next().getGenerations()
                .stream()
                .filter(gen -> gen.getId() == generationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Generation " + generationId + " not found in Store"));
    }

    @Setup(Level.Trial)
    public void prepareTestData() throws Exception {
        final FrontStorage frontStorage = loadFrontsFromResources();
        preparedTestData = new HashMap<>();

        for (int i = 0; i <= 90; i += 10) {
            final DoublesGeneration generation = getGeneration(frontStorage, i);
            final double[] nextAddend = generation.getNextAddend();
            final RankedPopulation rp = generation.getLexSortedRankedPop();

            final Population population = new Population();
            generation.getFronts().stream()
                    .sorted(Comparator.comparingInt(Front::getId))
                    .map(f -> {
                        final NonDominationLevel level = new NonDominationLevel();
                        level.getMembers().addAll(f.getFitnesses());
                        return level;
                    })
                    .forEach(level -> population.getLevels().add(level));

            preparedTestData.put(i, new PpsnTestData(nextAddend, rp, population));
        }
    }

    private int sortOneGeneration(int generationId, PPSN2014 sorter) {
        final PpsnTestData testData = Objects.requireNonNull(preparedTestData.get(generationId),
                "no cached test data for generation id " + generationId);
        final RankedPopulation res = sorter.performIncrementalNds(testData.getRankedPopulation().getPop(),
                testData.getRankedPopulation().getRanks(), testData.getNextAdddend());
        return res.getRanks().length;
    }

    private int sortUsingLevelPPSN(int generationId) {
        final PpsnTestData testData = Objects.requireNonNull(preparedTestData.get(generationId),
                "no cached test data for generation id " + generationId);
        final Population population = testData.getPopulation().copy();
        return population.addPoint(testData.getNextAdddend());
    }

    @Benchmark
    public int incPpsnTestGen0() {
        return sortOneGeneration(0, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen0() {
        return sortUsingLevelPPSN(0);
    }

    @Benchmark
    public int ppsn2014TestGen0() {
        return sortOneGeneration(0, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen10() {
        return sortOneGeneration(10, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen10() {
        return sortUsingLevelPPSN(10);
    }

    @Benchmark
    public int ppsn2014TestGen10() {
        return sortOneGeneration(10, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen20() {
        return sortOneGeneration(20, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen20() {
        return sortUsingLevelPPSN(20);
    }

    @Benchmark
    public int ppsn2014TestGen20() {
        return sortOneGeneration(20, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen30() {
        return sortOneGeneration(30, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen30() {
        return sortUsingLevelPPSN(30);
    }

    @Benchmark
    public int ppsn2014TestGen30() {
        return sortOneGeneration(30, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen40() {
        return sortOneGeneration(40, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen40() {
        return sortUsingLevelPPSN(40);
    }

    @Benchmark
    public int ppsn2014TestGen40() {
        return sortOneGeneration(40, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen50() {
        return sortOneGeneration(50, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen50() {
        return sortUsingLevelPPSN(50);
    }

    @Benchmark
    public int ppsn2014TestGen50() {
        return sortOneGeneration(50, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen60() {
        return sortOneGeneration(60, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen60() {
        return sortUsingLevelPPSN(60);
    }

    @Benchmark
    public int ppsn2014TestGen60() {
        return sortOneGeneration(60, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen70() {
        return sortOneGeneration(70, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen70() {
        return sortUsingLevelPPSN(70);
    }

    @Benchmark
    public int ppsn2014TestGen70() {
        return sortOneGeneration(70, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen80() {
        return sortOneGeneration(80, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen80() {
        return sortUsingLevelPPSN(80);
    }

    @Benchmark
    public int ppsn2014TestGen80() {
        return sortOneGeneration(80, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen90() {
        return sortOneGeneration(90, incrementalPPSN);
    }

    @Benchmark
    public int levelPpsnTestGen90() {
        return sortUsingLevelPPSN(90);
    }

    @Benchmark
    public int ppsn2014TestGen90() {
        return sortOneGeneration(90, ppsn2014);
    }

}
