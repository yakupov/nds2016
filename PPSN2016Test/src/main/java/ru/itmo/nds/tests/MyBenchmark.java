/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        try (FileInputStream fis = new FileInputStream("D:\\workspace\\2015-gecco-nsga-ii-ss\\test.json")) {
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
