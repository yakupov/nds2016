package ru.itmo.iyakupov.manual;

import com.google.gson.Gson;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.DTLZ.*;
import org.moeaframework.problem.ZDT.*;
import ru.itmo.iyakupov.NSGAIIMoeaRunner;
import ru.itmo.iyakupov.ss.SSNSGAII;
import ru.itmo.iyakupov.ss.pop.IPopulation;
import ru.itmo.iyakupov.ss.pop.LevelPPSNPopulation;
import ru.itmo.iyakupov.ss.pop.TreapPopulation;
import ru.itmo.nds.front_storage.DoublesAdditionProblem;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Ignore
public class GetResultsIncremental {
    private void doTest(Problem problem, int runId, IPopulation population) throws Exception {
        final int genSize = 5000;
        final int iterCount = 1000;

        final String outFileName = problem.getName().toLowerCase() +
                "_dim" + problem.getNumberOfObjectives() +
                "_initSize" + genSize +
                "_addendsCount" + iterCount +
                "_dataset" + runId +
                ".json";

        final SSNSGAII nsga2 = NSGAIIMoeaRunner.newSSNSGAII(genSize, problem, population);
        final List<double[]> addends = new ArrayList<>();
        nsga2.step(); //init
        final List<List<double[]>> levels = population.getLayers().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .map(lst -> lst.stream().map(Solution::getObjectives).sorted((d1, d2) -> {
                    for (int i = 0; i < d1.length; ++i) {
                        if (d1[i] < d2[i])
                            return -1;
                        else if (d1[i] > d2[i])
                            return 1;
                    }
                    return 0;
                }).collect(Collectors.toList()))
                .collect(Collectors.toList());
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(outFileName))) {
            for (int i = 0; i <= iterCount; ++i) {
                addends.add(nsga2.doIterate().getObjectives());
                if (nsga2.isTerminated()) {
                    System.out.println("Terminated (all duplicates) on iter " + i);
                    break;
                }
            }

            final DoublesAdditionProblem additionProblem = new DoublesAdditionProblem(levels, addends);
            writer.write(new Gson().toJson(additionProblem));
        }
    }

    @Test
    public void nsga2TestDtlz1() throws Exception {
        doTest(new DTLZ1(3), 1, new LevelPPSNPopulation());
        doTest(new DTLZ1(3), 2, new LevelPPSNPopulation());
        doTest(new DTLZ1(3), 3, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz1Dim4() throws Exception {
        doTest(new DTLZ1(4), 0, new LevelPPSNPopulation());
        doTest(new DTLZ1(4), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz1Dim6() throws Exception {
        doTest(new DTLZ1(6), 0, new LevelPPSNPopulation());
        doTest(new DTLZ1(6), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz1Dim8() throws Exception {
        doTest(new DTLZ1(8), 0, new LevelPPSNPopulation());
        doTest(new DTLZ1(8), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz1Dim10() throws Exception {
        doTest(new DTLZ1(10), 0, new LevelPPSNPopulation());
        doTest(new DTLZ1(10), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz2() throws Exception {
        doTest(new DTLZ2(3), 1, new LevelPPSNPopulation());
        doTest(new DTLZ2(3), 2, new LevelPPSNPopulation());
        doTest(new DTLZ2(3), 3, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz2Dim4() throws Exception {
        doTest(new DTLZ2(4), 0, new LevelPPSNPopulation());
        doTest(new DTLZ2(4), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz2Dim6() throws Exception {
        doTest(new DTLZ2(6), 0, new LevelPPSNPopulation());
        doTest(new DTLZ2(6), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz2Dim8() throws Exception {
        doTest(new DTLZ2(8), 0, new LevelPPSNPopulation());
        doTest(new DTLZ2(8), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz2Dim10() throws Exception {
        doTest(new DTLZ2(10), 0, new LevelPPSNPopulation());
        doTest(new DTLZ2(10), 1, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz3() throws Exception {
        doTest(new DTLZ3(3), 1, new LevelPPSNPopulation());
        doTest(new DTLZ3(3), 2, new LevelPPSNPopulation());
        doTest(new DTLZ3(3), 3, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz4() throws Exception {
        doTest(new DTLZ4(3), 1, new LevelPPSNPopulation());
        doTest(new DTLZ4(3), 2, new LevelPPSNPopulation());
        doTest(new DTLZ4(3), 3, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestDtlz7() throws Exception {
        doTest(new DTLZ7(3), 1, new LevelPPSNPopulation());
        doTest(new DTLZ7(3), 2, new LevelPPSNPopulation());
        doTest(new DTLZ7(3), 3, new LevelPPSNPopulation());
    }

    @Test
    public void nsga2TestZdt1() throws Exception {
        doTest(new ZDT1(), 1, new TreapPopulation());
        doTest(new ZDT1(), 2, new TreapPopulation());
        doTest(new ZDT1(), 3, new TreapPopulation());
    }

    @Test
    public void nsga2TestZdt2() throws Exception {
        doTest(new ZDT2(), 1, new TreapPopulation());
        doTest(new ZDT2(), 2, new TreapPopulation());
        doTest(new ZDT2(), 3, new TreapPopulation());
    }

    @Test
    public void nsga2TestZdt3() throws Exception {
        doTest(new ZDT3(), 1, new TreapPopulation());
        doTest(new ZDT3(), 2, new TreapPopulation());
        doTest(new ZDT3(), 3, new TreapPopulation());
    }

    @Test
    public void nsga2TestZdt4() throws Exception {
        doTest(new ZDT4(), 1, new TreapPopulation());
        doTest(new ZDT4(), 2, new TreapPopulation());
        doTest(new ZDT4(), 3, new TreapPopulation());
    }

    @Test
    public void nsga2TestZdt6() throws Exception {
        doTest(new ZDT6(), 1, new TreapPopulation());
        doTest(new ZDT6(), 2, new TreapPopulation());
        doTest(new ZDT6(), 3, new TreapPopulation());
    }
}
