package ru.itmo.iyakupov.ss;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.*;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;

import java.util.List;

/**
 * Steady-state modification of NSGA2, implemented using incremental NDS.
 * <p>
 * References:
 * <ol>
 * <li>Deb, K. et al.  "A Fast Elitist Multi-Objective Genetic Algorithm:
 * NSGA-II."  IEEE Transactions on Evolutionary Computation, 6:182-197,
 * 2000.
 * <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective
 * Evolutionary Algorithms for Long-Term Monitoring Design."  Advances in
 * Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class SSNSGAII extends AbstractAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {
    private final Variation variation;
    private final Initialization initialization;
    private final IPopulation population;

    private final DominanceComparator comparator = new ChainedComparator(
            new ParetoDominanceComparator(),
            new CrowdingComparator());

    private int expectedPopSize;

    public SSNSGAII(Problem problem, Variation variation, Initialization initialization, IPopulation population) {
        super(problem);
        this.variation = variation;
        this.initialization = initialization;
        this.population = population;
    }

    @Override
    public void iterate() {
        population.addSolution(generateOffspring());
        population.removeWorstSolution();

        int i;
        for (i = 0; i < 100 && population.size() < expectedPopSize; ++i) {
            population.addSolution(generateOffspring());
        }

        if (i == 100)
            terminated = true;
    }

    public Solution generateOffspring() {
        final List<Solution> mutationCandidates = population.getRandomSolutions(2 * variation.getArity());
        //PRNG.shuffle(mutationCandidates);

        final Solution[] parents = new Solution[variation.getArity()];
        for (int i = 0; i < mutationCandidates.size() - 1; i += 2) {
            parents[i / 2] = TournamentSelection.binaryTournament(
                    mutationCandidates.get(i),
                    mutationCandidates.get(i + 1),
                    comparator);
        }

        final Solution solution = variation.evolve(parents)[0];
        evaluate(solution);
        return solution;
    }

    @Override
    protected void initialize() {
        super.initialize();

        final Solution[] initialSolutions = initialization.initialize();
        evaluateAll(initialSolutions);
        for (Solution s: initialSolutions) {
            population.addSolution(s);
        }

        expectedPopSize = initialSolutions.length;
    }

    @Override
    public EpsilonBoxDominanceArchive getArchive() {
        throw new UnsupportedOperationException("Working with archive is not supported");
    }

    @Override
    public NondominatedSortingPopulation getPopulation() {
        throw new UnsupportedOperationException("Working with NondominatedPopulation is not supported");
    }

    @Override
    public NondominatedPopulation getResult() {
        throw new UnsupportedOperationException("Working with NondominatedPopulation is not supported");
    }
}
