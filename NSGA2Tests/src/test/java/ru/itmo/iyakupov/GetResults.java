package ru.itmo.iyakupov;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.DTLZ.*;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.front_storage.RunConfiguration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Obtain test data for our NDS
 */
public class GetResults {
    private void doTest(Problem problem, int runId) throws Exception {
        final int genSize = 10000;
        final int iterCount = 100;

        final String outFileName = problem.getName().toLowerCase() +
                "_dim" + problem.getNumberOfObjectives() +
                "_gen" + genSize +
                "_iter" + iterCount +
                "_dataset" + runId +
                ".json";

        final NSGAII nsga2 = NSGAIIMoeaRunner.newNSGAII(genSize, problem);

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
            for (int i = 0; i < iterCount; ++i) {
                if (i % 10 == 0) {
                    final DoublesGeneration generation = NSGAIIMoeaRunner.getCurrentPopulation(nsga2);
                    generation.setId(i);
                    generations.add(generation);
                }
                nsga2.step();
            }

            storage.serialize(fos);
        }
    }

    @Ignore
    @Test
    public void nsga2Test1() throws Exception {
//        doTest(new DTLZ1(3), 1);
//        doTest(new DTLZ1(3), 2);
//        doTest(new DTLZ1(3), 3);
//        doTest(new DTLZ2(3), 1);
//        doTest(new DTLZ2(3), 2);
//        doTest(new DTLZ2(3), 3);
//        doTest(new DTLZ3(3), 1);
//        doTest(new DTLZ3(3), 2);
//        doTest(new DTLZ3(3), 3);
//        doTest(new DTLZ4(3), 1);
//        doTest(new DTLZ4(3), 2);
//        doTest(new DTLZ4(3), 3);
        doTest(new DTLZ7(3), 1);
        doTest(new DTLZ7(3), 2);
        doTest(new DTLZ7(3), 3);
    }
}