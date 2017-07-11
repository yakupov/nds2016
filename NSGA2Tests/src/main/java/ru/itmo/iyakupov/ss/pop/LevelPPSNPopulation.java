package ru.itmo.iyakupov.ss.pop;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.layers_ppsn.impl.Population;
import ru.itmo.nds.util.ComparisonUtils;

import java.util.*;

import static org.moeaframework.core.NondominatedSorting.CROWDING_ATTRIBUTE;

public class LevelPPSNPopulation implements IPopulation {
    private final ru.itmo.nds.layers_ppsn.IPopulation<Solution> population;
    private final Comparator<Solution> comparator;

    public LevelPPSNPopulation() {
        this(new Population<>(Solution::getObjectives), new CrowdingComparator());
    }

    private LevelPPSNPopulation(ru.itmo.nds.layers_ppsn.IPopulation<Solution> population, Comparator<Solution> comparator) {
        this.population = population;
        this.comparator = comparator;
    }

    @Override
    public List<Solution> getRandomSolutions(int count) {
        final int[] levelOffsets = new int[population.getLevels().size() + 1];
        int i = 0;
        for (INonDominationLevel<Solution> level: population.getLevels()) {
            levelOffsets[i + 1] = levelOffsets[i] + level.getMembers().size();
            ++i;
        }
        final int totalCount = levelOffsets[population.getLevels().size()];

        final ArrayList<Solution> res = new ArrayList<>();
        final Random random = new Random(System.nanoTime());
        for (i = 0; i < count; ++i) {
            final int indexInPop = random.nextInt(totalCount);
            final Index index = IPopulation.binarySearchForBucket(levelOffsets, indexInPop);
            res.add(population.getLevels().get(index.bucketIndex).getMembers().get(index.indexInBucket));
        }

        return res;
    }

    @Override
    public boolean removeWorstSolution() {
        if (population.getLevels().isEmpty())
            return false;
        final INonDominationLevel<Solution> lastLevel = population.getLevels().get(population.getLevels().size() - 1);
        if (lastLevel.getMembers() == null || lastLevel.getMembers().isEmpty())
            throw new IllegalStateException("Empty ND level");
        int worstIndex = 0;
        for (int i = 1; i < lastLevel.getMembers().size(); ++i) {
            if (comparator.compare(lastLevel.getMembers().get(worstIndex), lastLevel.getMembers().get(i)) > 0)
                worstIndex = i;
        }
        lastLevel.getMembers().remove(worstIndex);
        if (lastLevel.getMembers().isEmpty())
            population.getLevels().remove(population.getLevels().size() - 1);

        return true;
    }

    @Override
    public boolean addSolution(Solution solution) {
        final int rank = population.addPoint(solution);

      //  validate();

        //System.out.println("Added to rank " + rank);
        updateCrowdingDistance(rank);

      //  validate();

        return true;
    }

    private void validate() {
        final Map<Integer, List<Solution>> ranks = getLayers();
        for (int i = 0; i < ranks.size(); ++i) {
            for (Solution ind : ranks.get(i)) {
                int rankCalcd = 0;
                Solution determinator = null;

                for (int j = 0; j < ranks.size(); ++j) {
                    for (Solution compInd: ranks.get(j)) {
                        if (ComparisonUtils.dominates(compInd.getObjectives(), ind.getObjectives(), compInd.getObjectives().length) < 0) {
                            if (j + 1 > rankCalcd) {
                                rankCalcd = j + 1;
                                determinator = compInd;
                            }
                        }
                    }
                }

                if (rankCalcd != i)
                    throw new RuntimeException("Population is sorted incorrectly. Point = " + Arrays.toString(ind.getObjectives()) +
                            ", rk = " + i + ", should be = " + rankCalcd + ", determinator = " + Arrays.toString(determinator.getObjectives()));

            }
        }
    }

    @Override
    public int size() {
        return population.getLevels().stream().mapToInt(l -> l.getMembers().size()).sum();
    }

    @Override
    public Map<Integer, List<Solution>> getLayers() {
        final Map<Integer, List<Solution>> res = new HashMap<>();
        for (int i = 0; i < population.getLevels().size(); ++i) {
            res.put(i, population.getLevels().get(i).getMembers());
        }
        return res;
    }


    private void updateCrowdingDistance(int rank) {
        final List<Solution> front = population.getLevels().get(rank).getMembers();

        // initially assign all crowding distances and remove duplicate solutions
        final List<Solution> nonDuplicates = new ArrayList<>(front.size());
        final Set<Solution> toEvict = new HashSet<>(front.size());
        for (Solution solution : front) {
            solution.setAttribute(CROWDING_ATTRIBUTE, 0.0);

            for (Solution nd: nonDuplicates) {
                if (IPopulation.distance(nd, solution) < Settings.EPS) {
                    toEvict.add(solution);
                    //System.out.println("toEvict " + Arrays.toString(solution.getObjectives()));
                    break;
                }
            }

            if (!toEvict.contains(solution))
                nonDuplicates.add(solution);
        }
        //TODO: faster
        front.removeAll(toEvict);

        // then compute the crowding distance for the unique solutions
        int n = front.size();

        if (n < 3) {
            for (Solution solution : front) {
                solution.setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);
            }
        } else {
            int numberOfObjectives = front.get(0).getNumberOfObjectives();

            for (int i = 0; i < numberOfObjectives; i++) {
                final List<Solution> frontCopy = new ArrayList<>(front.size());
                frontCopy.addAll(front);
                frontCopy.sort(new ObjectiveComparator(i));

                double minObjective = frontCopy.get(0).getObjective(i);
                double maxObjective = frontCopy.get(n - 1).getObjective(i);

                frontCopy.get(0).setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);
                frontCopy.get(n - 1).setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);

                for (int j = 1; j < n - 1; j++) {
                    double distance = (Double)frontCopy.get(j).getAttribute(
                            CROWDING_ATTRIBUTE);
                    distance += (frontCopy.get(j + 1).getObjective(i) -
                            frontCopy.get(j - 1).getObjective(i))
                            / (maxObjective - minObjective);
                    frontCopy.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
                }
            }
        }
    }
}
