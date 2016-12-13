package ru.itmo.nds.tests;

import org.openjdk.jmh.annotations.*;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.util.RankedPopulation;

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MyBenchmark {
    private FrontStorage frontStorage;

    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    @Setup(Level.Trial)
    public void init() throws Exception {
        frontStorage = new FrontStorage();
        try (FileInputStream fis = new FileInputStream("D:\\workspace\\nds2016\\2015-gecco-nsga-ii-ss\\test.json")) {
            frontStorage.deserialize(fis);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int incPpsnTestGen0() {
        final DoublesGeneration generation = frontStorage.getRunConfigurations().iterator().next().getGenerations().iterator().next();
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = incrementalPPSN.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int ppsn2014TestGen0() {
        final DoublesGeneration generation = frontStorage.getRunConfigurations().iterator().next().getGenerations().iterator().next();
        final double[] nextAddend = generation.getNextAddend();
        final RankedPopulation rp = generation.getLexSortedRankedPop();

        final RankedPopulation res = ppsn2014.performIncrementalNds(rp.getPop(), rp.getRanks(), nextAddend);
        return res.getRanks().length;
    }
}
