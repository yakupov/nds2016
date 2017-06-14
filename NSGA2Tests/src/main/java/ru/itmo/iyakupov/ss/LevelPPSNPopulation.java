package ru.itmo.iyakupov.ss;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.nds.layers_ppsn.INonDominationLevel;
import ru.itmo.nds.layers_ppsn.impl.Population;

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

    private static class Index {
        int bucketIndex;
        int indexInBucket;

        Index(int bucketIndex, int indexInBucket) {
            this.bucketIndex = bucketIndex;
            this.indexInBucket = indexInBucket;
        }
    }

    private Index binarySearchForBucket(int[] buckets, int value) {
        int l = 0;
        int r = buckets.length - 1;

        int bucketIndex = -1;
        while (true) {
            final int probe = (l + r) / 2;
            if (buckets[probe] <= value) {
                bucketIndex = probe;
                if (l == probe)
                    break;
                l = probe;
            } else {
                if (r == probe)
                    break;
                r = probe;
            }
        }

        return new Index(bucketIndex, value - buckets[bucketIndex]);
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
            final Index index = binarySearchForBucket(levelOffsets, indexInPop);
            res.add(population.getLevels().get(index.bucketIndex).getMembers().get(index.indexInBucket));
        }

        return res;
    }

    @Override
    public List<Solution> getBestSolutions(int count) {
        final ArrayList<Solution> res = new ArrayList<>();

        for (INonDominationLevel<Solution> level: population.getLevels()) {
            final List<Solution> membersCopy = new ArrayList<>(level.getMembers().size());
            membersCopy.addAll(level.getMembers());
            PRNG.shuffle(membersCopy);
            for (Solution s: membersCopy) {
                if (res.size() >= count) return res;
                res.add(s);
            }
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

        //System.out.println("Added to rank " + rank);

        updateCrowdingDistance(rank);
        return true;
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
                    break;
                }
            }

            if (!toEvict.contains(solution))
                nonDuplicates.add(solution);
        }
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
                front.sort(new ObjectiveComparator(i));

                double minObjective = front.get(0).getObjective(i);
                double maxObjective = front.get(n - 1).getObjective(i);

                front.get(0).setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);
                front.get(n - 1).setAttribute(CROWDING_ATTRIBUTE,
                        Double.POSITIVE_INFINITY);

                for (int j = 1; j < n - 1; j++) {
                    double distance = (Double)front.get(j).getAttribute(
                            CROWDING_ATTRIBUTE);
                    distance += (front.get(j + 1).getObjective(i) -
                            front.get(j - 1).getObjective(i))
                            / (maxObjective - minObjective);
                    front.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
                }
            }
        }
    }


}
