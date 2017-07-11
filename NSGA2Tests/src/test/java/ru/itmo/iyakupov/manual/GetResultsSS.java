package ru.itmo.iyakupov.manual;

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
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.Front;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.front_storage.RunConfiguration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Ignore
public class GetResultsSS {
    private void doTest(Problem problem, int runId, IPopulation population) throws Exception {
        final int genSize = 10000;
        final int iterCount = 10000;

        final String outFileName = problem.getName().toLowerCase() +
                "_dim" + problem.getNumberOfObjectives() +
                "_gen" + genSize +
                "_iter" + iterCount +
                "_dataset" + runId +
                ".json";

        final SSNSGAII nsga2 = NSGAIIMoeaRunner.newSSNSGAII(genSize, problem, population);

        final FrontStorage storage = new FrontStorage();
        if (Files.exists(Paths.get(outFileName))) {
            try (FileInputStream fis = new FileInputStream(outFileName)) {
                storage.deserialize(fis);
            }
        }

        if (storage.getRunConfigurations() == null)
            storage.setRunConfigurations(new ArrayList<>());

        try (FileOutputStream fos = new FileOutputStream(outFileName)) {
            final RunConfiguration rc = new RunConfiguration();
            rc.setTimestamp(LocalDateTime.now());
            rc.setNumberOfIterations(iterCount);
            rc.setSizeOfGeneration(genSize);

            final ArrayList<DoublesGeneration> generations = new ArrayList<>();
            rc.setGenerations(generations);

            storage.getRunConfigurations().add(rc);

            nsga2.step(); //init
            for (int i = 0; i <= iterCount; ++i) {
                if (i % (iterCount / 10) == 0) {
                    final DoublesGeneration generation = new DoublesGeneration();
                    generation.setNextAddend(nsga2.generateOffspring().getObjectives());

                    generation.setFronts(population.getLayers().entrySet().stream().map(e -> {
                        final Front<double[]> f = new Front<>();
                        f.setId(e.getKey());
                        f.setFitnesses(e.getValue().stream().map(Solution::getObjectives).collect(Collectors.toList()));
                        return f;
                    }).collect(Collectors.toList()));

                    generation.setId(i);
                    generations.add(generation);
                }
                nsga2.step();

                if (nsga2.isTerminated()) {
                    System.out.println("Terminated (all duplicates) on iter " + i);
                    break;
                }
            }

            storage.serialize(fos);
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
