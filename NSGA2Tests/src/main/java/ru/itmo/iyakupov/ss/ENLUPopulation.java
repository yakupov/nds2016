package ru.itmo.iyakupov.ss;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.nds.util.ComparisonUtils;

import java.util.*;

import static org.moeaframework.core.NondominatedSorting.CROWDING_ATTRIBUTE;

public class ENLUPopulation implements IPopulation {
    private final Set<double[]> individuals;
    private final List<List<Solution>> ranks;
    private final Comparator<Solution> comparator;

    public ENLUPopulation() {
        this(new HashSet<>(), new ArrayList<>(), new CrowdingComparator());
    }

    private ENLUPopulation(Set<double[]> individuals, List<List<Solution>> ranks, Comparator<Solution> comparator) {
        this.individuals = individuals;
        this.ranks = ranks;
        this.comparator = comparator;
    }

    @Override
    public List<Solution> getRandomSolutions(int count) {
        final int[] levelOffsets = new int[ranks.size() + 1];
        int i = 0;
        for (List<Solution> level: ranks) {
            levelOffsets[i + 1] = levelOffsets[i] + level.size();
            ++i;
        }
        final int totalCount = levelOffsets[ranks.size()];

        final ArrayList<Solution> res = new ArrayList<>();
        final Random random = new Random(System.nanoTime());
        for (i = 0; i < count; ++i) {
            final int indexInPop = random.nextInt(totalCount);
            final Index index = IPopulation.binarySearchForBucket(levelOffsets, indexInPop);
            res.add(ranks.get(index.bucketIndex).get(index.indexInBucket));
        }

        return res;
    }

    @Override
    public boolean removeWorstSolution() {
        if (ranks.isEmpty())
            return false;
        final List<Solution> lastLevel = ranks.get(ranks.size() - 1);
        if (lastLevel == null || lastLevel.isEmpty())
            throw new IllegalStateException("Empty ND level");
        int worstIndex = 0;
        for (int i = 1; i < lastLevel.size(); ++i) {
            if (comparator.compare(lastLevel.get(worstIndex), lastLevel.get(i)) > 0)
                worstIndex = i;
        }
        lastLevel.remove(worstIndex);
        if (lastLevel.isEmpty())
            ranks.remove(ranks.size() - 1);

        return true;
    }

    @Override
    public boolean addSolution(Solution nInd) {
        if (individuals.contains(nInd.getObjectives())) {
            return false;
        } else {
            individuals.add(nInd.getObjectives());
        }

        for (int i = 0; i < ranks.size(); ++i) {
            boolean dominates, dominated, nd;
            dominates = dominated = nd = false;
            final Map<double[], Solution> dominatedSet = new HashMap<>();

            for (Solution ind: ranks.get(i)) {
                int domComparisonResult = ComparisonUtils.dominates(nInd.getObjectives(), ind.getObjectives(), nInd.getObjectives().length);
                //nInd.compareDom(ind);
                if (domComparisonResult == 0)
                    nd = true;
                else if (domComparisonResult > 0) {
                    dominated = true;
                    break;
                } else {
                    dominatedSet.put(ind.getObjectives(), ind);
                    dominates = true;
                }
            }

            if (dominated) {
                //noinspection UnnecessaryContinue
                continue;
            } else if (!nd && dominates) {
                final List<Solution> newRank = new ArrayList<>();
                ranks.add(i, newRank);
                newRank.add(nInd);
                updateCrowdingDistance(i);
                return true;
            } else {
                final List<Solution> updatedRank = new ArrayList<>();
                final Set<double[]> keySet = dominatedSet.keySet();
                for (Solution s: ranks.get(i)) {
                    if (!keySet.contains(s.getObjectives()))
                        updatedRank.add(s);
                }
                updatedRank.add(nInd);
                ranks.set(i, updatedRank);

                update(dominatedSet, i + 1);
                updateCrowdingDistance(i);
                return true;
            }
        }

        final List<Solution> newRank = new ArrayList<>();
        ranks.add(newRank);
        newRank.add(nInd);
        updateCrowdingDistance(ranks.size() - 1);
        return true;
    }

    private void updateCrowdingDistance(int rank) {
        final List<Solution> front = ranks.get(rank);

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


    private void update(Map<double[], Solution> dominatedSet, int i) {
        if (i >= ranks.size()) {
            ranks.add(new ArrayList<>(dominatedSet.values()));
        } else {
            final Map<double[], Solution> newDominatedSet = new HashMap<>();
            final Set<double[]> keySet = dominatedSet.keySet();
            for (double[] iNew : keySet) {
                for (Solution iOld : ranks.get(i)) {
                    if (ComparisonUtils.dominates(iNew, iOld.getObjectives(), iNew.length) < 0) {
                        newDominatedSet.put(iOld.getObjectives(), iOld);
                    }
                }

                final List<Solution> newRank = new ArrayList<>();
                final Set<double[]> newKeySet = newDominatedSet.keySet();
                for (Solution s : ranks.get(i)) {
                    if (!newKeySet.contains(s.getObjectives()))
                        newRank.add(s);
                }
                newRank.addAll(dominatedSet.values());
                ranks.set(i, newRank);
            }
            if (!newDominatedSet.isEmpty())
                update(newDominatedSet, i + 1);
        }
    }

    private int detRankOfExPoint(double[] point) {
        for (int i = 0; i < ranks.size(); ++i) {
            for (Solution solution : ranks.get(i)) {
                if (Arrays.equals(solution.getObjectives(), point))
                    return i;
            }
        }
        throw new RuntimeException("Point does not exist");
    }

    public void validate() {
        for (int i = 0; i < ranks.size(); ++i) {
            for (Solution ind : ranks.get(i)) {
                int rankCalcd = 0;
                double[] determinator = null;
                for (double[] compInd : individuals) {
                    if (compInd != ind.getObjectives() && ComparisonUtils.dominates(compInd, ind.getObjectives(), compInd.length) < 0) {
                        //if (compInd != ind && compInd.compareDom(ind) < 0) {
                        int compRank = detRankOfExPoint(compInd);
                        if (compRank + 1 > rankCalcd) {
                            rankCalcd = compRank + 1;
                            determinator = compInd;
                        }
                    }
                }
                if (rankCalcd != i)
                    throw new RuntimeException("Population is sorted incorrectly. Point = " + Arrays.toString(ind.getObjectives()) +
                            ", rk = " + i + ", should be = " + rankCalcd + ", determinator = " + Arrays.toString(determinator));

            }
        }
    }

    @Override
    public int size() {
        return individuals.size();
    }

    @Override
    public Map<Integer, List<Solution>> getLayers() {
        final Map<Integer, List<Solution>> res = new HashMap<>();
        for (int i = 0; i < ranks.size(); ++i) {
            res.put(i, new ArrayList<>(ranks.get(i)));
        }
        return res;
    }
}
