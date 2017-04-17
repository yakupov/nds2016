package ru.itmo.iyakupov;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import ru.itmo.iyakupov.nsga2nds.Individual;
import ru.itmo.iyakupov.nsga2nds.NSGAIINonDominatingSorter;
import ru.itmo.iyakupov.ss.IPopulation;
import ru.itmo.iyakupov.ss.SSNSGAII;
import ru.itmo.nds.front_storage.DoublesGeneration;
import ru.itmo.nds.front_storage.Front;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Copied utility code from the MOEA framework.
 * Usage of standard MOEA classes is impossible, because we need to gather specific test data.
 */
public class NSGAIIMoeaRunner {

    /**
     * Returns a new {@link NSGAII} instance.
     *
     * @param populationSize population size
     * @param problem the problem
     * @return a new {@code NSGAII} instance
     */
    public static NSGAII newNSGAII(int populationSize, Problem problem) {
        return newNSGAII(populationSize, problem, false);
    }

    private static NSGAII newNSGAII(int populationSize, Problem problem, boolean withReplacement) {
        final Initialization initialization = new RandomInitialization(problem, populationSize);
        final NondominatedSortingPopulation population = new NondominatedSortingPopulation();

        final TournamentSelection selection;
        if (withReplacement) {
            selection = new TournamentSelection(2, new ChainedComparator(
                    new ParetoDominanceComparator(),
                    new CrowdingComparator()));
        } else {
            selection = null;
        }

        final Variation variation = getVariation(problem);

        return new NSGAII(problem, population, null, selection, variation, initialization);
    }

    public static SSNSGAII newSSNSGAII(int populationSize, Problem problem, IPopulation population) {
        final Initialization initialization = new RandomInitialization(problem, populationSize);
        final Variation variation = getVariation(problem);
        return new SSNSGAII(problem, variation, initialization, population);
    }

    public static Variation getVariation(Problem problem) {
        return OperatorFactory.getInstance().getVariation(null, new Properties(), problem);
    }

    public static DoublesGeneration getCurrentPopulation(NSGAII nsga2) {
        final DoublesGeneration doublesGeneration = new DoublesGeneration();
        doublesGeneration.setFronts(new ArrayList<>());

        final List<Individual> pop = new ArrayList<>(nsga2.getPopulation().size());
        for (Solution solution : nsga2.getPopulation()) {
            pop.add(new Individual(solution.getObjectives()));
        }

        final List<List<Individual>> fronts = NSGAIINonDominatingSorter.sort(pop);
        for (int j = 0; j < fronts.size(); ++j) {
            final Front<double[]> currentFront = new Front<>();
            currentFront.setId(j);
            currentFront.setFitnesses(fronts.get(j).stream().map(Individual::getFitnesses).collect(Collectors.toList()));
            doublesGeneration.getFronts().add(currentFront);

            if (j == 0) {
                Solution[] solutions = new Solution[2];
                final Iterator<Solution> ndSet = nsga2.getResult().iterator();
                solutions[0] = ndSet.next();
                solutions[1] = ndSet.next();

                solutions = getVariation(nsga2.getProblem()).evolve(solutions);
                nsga2.evaluateAll(solutions);
                doublesGeneration.setNextAddend(solutions[0].getObjectives());
            }
        }

        return doublesGeneration;
    }
}
