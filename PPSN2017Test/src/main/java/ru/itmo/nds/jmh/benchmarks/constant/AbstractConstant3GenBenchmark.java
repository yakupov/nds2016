package ru.itmo.nds.jmh.benchmarks.constant;

import org.openjdk.jmh.annotations.Benchmark;

public abstract class AbstractConstant3GenBenchmark extends AbstractConstantBenchmark {
    AbstractConstant3GenBenchmark() {
        super(3);
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
