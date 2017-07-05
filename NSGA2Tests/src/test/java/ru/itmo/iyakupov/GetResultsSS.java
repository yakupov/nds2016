package ru.itmo.iyakupov;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.DTLZ.DTLZ1;
import org.moeaframework.problem.ZDT.ZDT1;
import ru.itmo.iyakupov.ss.IPopulation;
import ru.itmo.iyakupov.ss.LevelPPSNPopulation;
import ru.itmo.iyakupov.ss.SSNSGAII;
import ru.itmo.iyakupov.ss.TreapPopulation;
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

public class GetResultsSS {
    private void doTest(Problem problem, int runId, IPopulation population) throws Exception {
        final int genSize = 500;
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
                if (i % 2000 == 0) {
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

    @Ignore
    @Test
    public void nsga2TestDtlz() throws Exception {
        doTest(new DTLZ1(3), 2, new LevelPPSNPopulation());
    }

    @Ignore
    @Test
    public void nsga2TestZdtTreap() throws Exception {
        doTest(new ZDT1(), 2, new TreapPopulation());
    }
}
