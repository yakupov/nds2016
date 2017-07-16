package ru.itmo.nds.jmh.benchmarks.constant;

import org.openjdk.jmh.annotations.Benchmark;

public abstract class AbstractConstant1GenBenchmark extends AbstractConstantBenchmark {
    AbstractConstant1GenBenchmark() {
        super(1);
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
}
