import org.junit.Ignore;
import org.junit.Test;
import ru.ifmo.steady.NSGA2;
import ru.ifmo.steady.Problem;
import ru.ifmo.steady.Solution;
import ru.ifmo.steady.inds.Storage;
import ru.ifmo.steady.problem.ZDT2;
import ru.ifmo.steady.problem.ZDT3;
import ru.ifmo.steady.problem.ZDT4;
import ru.ifmo.steady.problem.ZDT6;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Playground
 */
public class Test1 {

    @Ignore
    @Test
    public void nsga2Test1() throws Exception {
        doTest(new ZDT2(), 2);
        doTest(new ZDT2(), 3);
        doTest(new ZDT3(), 1);
        doTest(new ZDT3(), 2);
        doTest(new ZDT3(), 3);
        doTest(new ZDT4(), 1);
        doTest(new ZDT4(), 2);
        doTest(new ZDT4(), 3);
        doTest(new ZDT6(), 1);
        doTest(new ZDT6(), 2);
        doTest(new ZDT6(), 3);
    }

    private void doTest(Problem problem, int runId) throws Exception {
        final int genSize = 10000;
        final int iterCount = 100;

        final String outFileName = problem.getName().toLowerCase() +
                "_gen" + genSize +
                "_iter" + iterCount +
                "_dataset" + runId +
                ".json";

        final NSGA2 nsga2 = new NSGA2(problem, new Storage(), genSize,
                false, false, NSGA2.Variant.PureSteadyState);
        nsga2.initialize();

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

            for (int i = 0; i < iterCount; ++i) {
                if (i % 10 == 0) {
                    final DoublesGeneration generation = new DoublesGeneration();
                    final List<Front<double[]>> fronts = new ArrayList<>();
                    generation.setFronts(fronts);
                    generation.setId(i);
                    nsga2.getFronts().entrySet().forEach(e -> {
                        final Front<double[]> f = new Front<>();
                        f.setId(e.getKey());
                        f.setFitnesses(e.getValue().stream().map(solution -> {
                            final double[] res = new double[2];
                            res[0] = solution.getNormalizedX(0, 1);
                            res[1] = solution.getNormalizedY(0, 1);
                            return res;
                        }).collect(Collectors.toList()));
                        fronts.add(f);
                    });

                    final Solution nextSolution = nsga2.generateNewSolution();
                    final double[] nextAddend = new double[2];
                    nextAddend[0] = nextSolution.getNormalizedX(0, 1);
                    nextAddend[1] = nextSolution.getNormalizedY(0, 1);
                    generation.setNextAddend(nextAddend);

                    generations.add(generation);
                }
                nsga2.performIteration();
            }

            storage.serialize(fos);
        }
    }

}
