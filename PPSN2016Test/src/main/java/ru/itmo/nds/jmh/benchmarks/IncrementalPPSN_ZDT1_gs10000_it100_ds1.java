package ru.itmo.nds.jmh.benchmarks;

import org.openjdk.jmh.annotations.*;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.util.RankedPopulation;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 5)
@Fork(value = 3)
public class IncrementalPPSN_ZDT1_gs10000_it100_ds1 {
    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    FrontStorage frontStorage;

    @Setup(Level.Trial)
    public void init() throws Exception {
        frontStorage = new FrontStorage();

        try (InputStream is = IncrementalPPSN_ZDT1_gs10000_it100_ds1.class
                .getResourceAsStream("ppsn/zdt1_gen10000_iter100_dataset1.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }
    }

    private DoublesGeneration getGeneration(int generationId) {
        return frontStorage.getRunConfigurations().iterator().next().getGenerations()
                .stream()
                .filter(gen -> gen.getId() == generationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Generation " + generationId + " not found in Store"));
    }

    private int sortOneGeneration(int generationId, PPSN2014 sorter) {
        final DoublesGeneration generation = getGeneration(generationId);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = sorter.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    public int incPpsnTestGen0() {
        return sortOneGeneration(0, incrementalPPSN);
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
    public int ppsn2014TestGen10() {
        return sortOneGeneration(10, ppsn2014);
    }
/*
    @Benchmark
    public int incPpsnTestGen20() {
        return sortOneGeneration(20, incrementalPPSN);
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
    public int ppsn2014TestGen30() {
        return sortOneGeneration(30, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen40() {
        return sortOneGeneration(40, incrementalPPSN);
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
    public int ppsn2014TestGen50() {
        return sortOneGeneration(50, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen60() {
        return sortOneGeneration(60, incrementalPPSN);
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
    public int ppsn2014TestGen70() {
        return sortOneGeneration(70, ppsn2014);
    }

    @Benchmark
    public int incPpsnTestGen80() {
        return sortOneGeneration(80, incrementalPPSN);
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
    public int ppsn2014TestGen90() {
        return sortOneGeneration(90, ppsn2014);
    }
    */
}
