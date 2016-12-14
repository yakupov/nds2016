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
public class IncrementalPPSN_Gen10000_Dataset1 {
    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    private FrontStorage frontStorage;

    @Setup(Level.Trial)
    public void init() throws Exception {
        frontStorage = new FrontStorage();

        try (InputStream is = IncrementalPPSN_Gen10000_Dataset1.class
                .getResourceAsStream("ppsn/gen10000_iter100_dataset1.json")) {
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

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen0() {
        final DoublesGeneration generation = getGeneration(0);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen0() {
        final DoublesGeneration generation = getGeneration(0);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen10() {
        final DoublesGeneration generation = getGeneration(10);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen10() {
        final DoublesGeneration generation = getGeneration(10);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen20() {
        final DoublesGeneration generation = getGeneration(20);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen20() {
        final DoublesGeneration generation = getGeneration(20);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen30() {
        final DoublesGeneration generation = getGeneration(30);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen30() {
        final DoublesGeneration generation = getGeneration(30);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen40() {
        final DoublesGeneration generation = getGeneration(40);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen40() {
        final DoublesGeneration generation = getGeneration(40);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen50() {
        final DoublesGeneration generation = getGeneration(50);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen50() {
        final DoublesGeneration generation = getGeneration(50);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen60() {
        final DoublesGeneration generation = getGeneration(60);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen60() {
        final DoublesGeneration generation = getGeneration(60);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen70() {
        final DoublesGeneration generation = getGeneration(70);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen70() {
        final DoublesGeneration generation = getGeneration(70);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen80() {
        final DoublesGeneration generation = getGeneration(80);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen80() {
        final DoublesGeneration generation = getGeneration(80);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int incPpsnTestGen90() {
        final DoublesGeneration generation = getGeneration(90);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(value = 3)
    public int ppsn2014TestGen90() {
        final DoublesGeneration generation = getGeneration(90);
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }
}
