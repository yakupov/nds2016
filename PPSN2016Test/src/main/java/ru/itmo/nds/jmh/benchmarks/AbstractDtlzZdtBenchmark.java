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
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 6)
@Measurement(iterations = 4)
@Fork(value = 2)
public abstract class AbstractDtlzZdtBenchmark extends AbstractBenchmark {
    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    private Map<Integer, PpsnTestData> preparedTestData;

    @Override
    protected Map<Integer, PpsnTestData> getPreparedTestData() {
        return preparedTestData;
    }

    protected abstract FrontStorage loadFrontsFromResources() throws Exception;

    @SuppressWarnings("WeakerAccess")
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

            preparedTestData.put(i, new PpsnTestData(nextAddend, rp, population));
        }
    }

    @Benchmark
    public int enluGen0() {
        return sortUsingEnlu(0);
    }

    @Benchmark
    public int incPpsnFastSweepGen0() {
        return sortOneGeneration(0, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen0() {
        return sortOneGeneration(0, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen0() {
        return sortUsingLevelPPSN(0);
    }

    @Benchmark
    public int oldPpsnGen0() {
        return sortFullyUsingPpsn(0);
    }

    @Benchmark
    public int enluGen10() {
        return sortUsingEnlu(10);
    }

    @Benchmark
    public int incPpsnFastSweepGen10() {
        return sortOneGeneration(10, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen10() {
        return sortOneGeneration(10, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen10() {
        return sortUsingLevelPPSN(10);
    }

    @Benchmark
    public int oldPpsnGen10() {
        return sortFullyUsingPpsn(10);
    }

    @Benchmark
    public int enluGen20() {
        return sortUsingEnlu(20);
    }

    @Benchmark
    public int incPpsnFastSweepGen20() {
        return sortOneGeneration(20, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen20() {
        return sortOneGeneration(20, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen20() {
        return sortUsingLevelPPSN(20);
    }

    @Benchmark
    public int oldPpsnGen20() {
        return sortFullyUsingPpsn(20);
    }

    @Benchmark
    public int enluGen30() {
        return sortUsingEnlu(30);
    }

    @Benchmark
    public int incPpsnFastSweepGen30() {
        return sortOneGeneration(30, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen30() {
        return sortOneGeneration(30, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen30() {
        return sortUsingLevelPPSN(30);
    }

    @Benchmark
    public int oldPpsnGen30() {
        return sortFullyUsingPpsn(30);
    }

    @Benchmark
    public int enluGen40() {
        return sortUsingEnlu(40);
    }

    @Benchmark
    public int incPpsnFastSweepGen40() {
        return sortOneGeneration(40, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen40() {
        return sortOneGeneration(40, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen40() {
        return sortUsingLevelPPSN(40);
    }

    @Benchmark
    public int oldPpsnGen40() {
        return sortFullyUsingPpsn(40);
    }

    @Benchmark
    public int enluGen50() {
        return sortUsingEnlu(50);
    }

    @Benchmark
    public int incPpsnFastSweepGen50() {
        return sortOneGeneration(50, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen50() {
        return sortOneGeneration(50, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen50() {
        return sortUsingLevelPPSN(50);
    }

    @Benchmark
    public int oldPpsnGen50() {
        return sortFullyUsingPpsn(50);
    }

    @Benchmark
    public int enluGen60() {
        return sortUsingEnlu(60);
    }

    @Benchmark
    public int incPpsnFastSweepGen60() {
        return sortOneGeneration(60, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen60() {
        return sortOneGeneration(60, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen60() {
        return sortUsingLevelPPSN(60);
    }

    @Benchmark
    public int oldPpsnGen60() {
        return sortFullyUsingPpsn(60);
    }

    @Benchmark
    public int enluGen70() {
        return sortUsingEnlu(70);
    }

    @Benchmark
    public int incPpsnFastSweepGen70() {
        return sortOneGeneration(70, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen70() {
        return sortOneGeneration(70, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen70() {
        return sortUsingLevelPPSN(70);
    }

    @Benchmark
    public int oldPpsnGen70() {
        return sortFullyUsingPpsn(70);
    }

    @Benchmark
    public int enluGen80() {
        return sortUsingEnlu(80);
    }

    @Benchmark
    public int incPpsnFastSweepGen80() {
        return sortOneGeneration(80, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen80() {
        return sortOneGeneration(80, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen80() {
        return sortUsingLevelPPSN(80);
    }

    @Benchmark
    public int oldPpsnGen80() {
        return sortFullyUsingPpsn(80);
    }

    @Benchmark
    public int enluGen90() {
        return sortUsingEnlu(90);
    }

    @Benchmark
    public int incPpsnFastSweepGen90() {
        return sortOneGeneration(90, incrementalPPSN);
    }

    @Benchmark
    public int incPpsnGen90() {
        return sortOneGeneration(90, ppsn2014);
    }

    @Benchmark
    public int levelPpsnGen90() {
        return sortUsingLevelPPSN(90);
    }

    @Benchmark
    public int oldPpsnGen90() {
        return sortFullyUsingPpsn(90);
    }

}
