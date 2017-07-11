package ru.itmo.iyakupov.manual;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.problem.DTLZ.DTLZ1;
import ru.itmo.iyakupov.NSGAIIMoeaRunner;
import ru.itmo.iyakupov.nsga2nds.Individual;
import ru.itmo.iyakupov.nsga2nds.NSGAIINonDominatingSorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Playground {
    @Test
    @Ignore
    public void t1() throws Exception {
        NondominatedPopulation result = new Executor()
                .withProblem("DTLZ1_3")
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(1000)
                .distributeOnAllCores()
                .run();

//        List<NondominatedPopulation> multiRuns = new Executor()
//                .withProblem("ZDT1")
//                .withAlgorithm("NSGAII")
//                .withMaxEvaluations(1000)
//                .distributeOnAllCores()
//                .runSeeds(3);
//
        System.out.format("Obj1  Obj2%n");
        for (Solution solution : result) {
            System.out.println(Arrays.toString(solution.getObjectives()));
//            System.out.format("%.5f\t%.5f%n", solution.getObjective(0),
//                    solution.getObjective(1));
        }
    }

    @Test
    @Ignore
    public void t2() throws Exception {
        final Problem problem = new DTLZ1(3);
        final NSGAII nsga2 = NSGAIIMoeaRunner.newNSGAII(10, problem);


        for (int i = 0; i < 3; ++i) {
            System.out.println("Before i = " + i);
            System.out.println(nsga2.isInitialized());

            System.out.println("Res: ");
            for (Solution solution : nsga2.getResult()) {
                System.out.println(Arrays.toString(solution.getObjectives()));
            }

            System.out.println("pop: ");
            final List<Individual> pop = new ArrayList<>();
            for (Solution solution : nsga2.getPopulation()) {
                System.out.println(Arrays.toString(solution.getObjectives()));
                pop.add(new Individual(solution.getObjectives()));
            }

            if (pop.size() > 0) {
                final List<List<Individual>> fronts = NSGAIINonDominatingSorter.sort(pop);
                for (int j = 0; j < fronts.size(); ++j) {
                    System.out.println("Front " + j);
                    for (Individual individual : fronts.get(j)) {
                        System.out.println(Arrays.toString(individual.getFitnesses()));
                    }
                }

                final Solution[] solutions = new Solution[2];
//                final Random random = new Random(System.nanoTime());
//                final List<Individual> front0 = fronts.get(0);
//                final int index0 = random.nextInt(front0.size());
//                int index1 = index0;
//                if (front0.size() > 1 && index1 == index0)
//                    index1 = random.nextInt(front0.size());

                final Iterator<Solution> ndSet = nsga2.getResult().iterator();
                solutions[0] = ndSet.next();
                solutions[1] = ndSet.next();

//                solutions[0] = new Solution(front0.get(index0).getFitnesses());
//                solutions[1] = new Solution(front0.get(index1).getFitnesses());
//
                final Variation variation = NSGAIIMoeaRunner.getVariation(problem);
                System.out.println("Arity: " + variation.getArity());
                final Solution[] res = variation.evolve(solutions);
//
//                System.out.println(variation);
//                Field f = CompoundVariation.class.getDeclaredField("operators");
//                f.setAccessible(true);
//                final List<Variation> x = (List<Variation>) f.get(variation);
//                System.out.println(x);
//
//                PM pm = (PM) x.get(1);
//                Field prob = PM.class.getDeclaredField("probability");
//                prob.setAccessible(true);
//                prob.setDouble(pm, 1.0);
//
//
//                System.out.println(((SBX)x.get(0)).getProbability());
//                System.out.println(((PM)x.get(1)).getProbability());
//                System.out.println(solutions[0].getVariable(0));
//                System.out.println(Arrays.toString(pm.evolve(solutions)[0].getObjectives()));
//                System.out.println(solutions[0].getVariable(0));

                nsga2.evaluateAll(res);

                System.out.println(res.length);
                System.out.println(Arrays.toString(solutions[0].getObjectives()));
                System.out.println(Arrays.toString(solutions[1].getObjectives()));
                System.out.println(Arrays.toString(res[0].getObjectives()));
                System.out.println(Arrays.toString(res[1].getObjectives()));
            }

            nsga2.step();
        }

    }
}
