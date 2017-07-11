package ru.itmo.iyakupov.ss.pop;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.nds.util.ComparisonUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.moeaframework.core.NondominatedSorting.CROWDING_ATTRIBUTE;

public class ENLUPopulation implements IPopulation {
    private static class SolutionHolder {
        private final Solution solution;

        private SolutionHolder(Solution solution) {
            this.solution = solution;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final SolutionHolder that = (SolutionHolder) o;
            return Arrays.equals(solution.getObjectives(), that.solution.getObjectives());
        }

        @Override
        public int hashCode() {
            return solution != null ? Arrays.hashCode(solution.getObjectives()) : 0;
        }
    }

    private final Set<SolutionHolder> individuals;
    private final List<List<Solution>> ranks;
    private final Comparator<Solution> comparator;

    public ENLUPopulation() {
        this(new HashSet<>(), new ArrayList<>(), new CrowdingComparator());
    }

    private ENLUPopulation(Set<SolutionHolder> individuals, List<List<Solution>> ranks, Comparator<Solution> comparator) {
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
        final SolutionHolder nIndHolder = new SolutionHolder(nInd);
        if (individuals.contains(nIndHolder)) {
            return false;
        } else {
            individuals.add(nIndHolder);
        }

        for (int i = 0; i < ranks.size(); ++i) {
            boolean dominates, dominated, nd;
            dominates = dominated = nd = false;
            final Set<SolutionHolder> dominatedSet = new HashSet<>();

            for (Solution ind: ranks.get(i)) {
                final int domComparisonResult = ComparisonUtils.dominates(nInd.getObjectives(), ind.getObjectives(), nInd.getObjectives().length);
                //nInd.compareDom(ind);
                if (domComparisonResult == 0)
                    nd = true;
                else if (domComparisonResult > 0) {
                    dominated = true;
                    break;
                } else {
                    dominatedSet.add(new SolutionHolder(ind));
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
                if (!dominatedSet.isEmpty()) {
                    final List<Solution> updatedRank = new ArrayList<>();
                    for (Solution s : ranks.get(i)) {
                        if (!dominatedSet.contains(new SolutionHolder(s)))
                            updatedRank.add(s);
                    }
                    updatedRank.add(nInd);
                    ranks.set(i, updatedRank);
                    update(dominatedSet, i + 1);
                } else {
                    ranks.get(i).add(nInd);
                }
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

        //TODO: faster
        front.removeAll(toEvict);
        assert (!front.isEmpty());

        // then compute the crowding distance for the unique solutions
        final int n = front.size();

        if (n < 3) {
            for (Solution solution : front) {
                solution.setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);
            }
        } else {
            int numberOfObjectives = front.get(0).getNumberOfObjectives();

            for (int i = 0; i < numberOfObjectives; i++) {
                front.sort(new ObjectiveComparator(i));

                double minObjective = front.get(0).getObjective(i);
                double maxObjective = front.get(n - 1).getObjective(i);

                front.get(0).setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);
                front.get(n - 1).setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);

                for (int j = 1; j < n - 1; j++) {
                    double distance = (Double)front.get(j).getAttribute( CROWDING_ATTRIBUTE);
                    distance += (front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i)) / (maxObjective - minObjective);
                    front.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
                }
            }
        }
    }


    private void update(Set<SolutionHolder> dominatedSet, int i) {
        assert (!dominatedSet.isEmpty());

//        System.out.println("Enter update " +i + ", ds.size() " + dominatedSet.size());
//        new Exception().printStackTrace();
        if (i >= ranks.size()) {
            ranks.add(dominatedSet.stream().map(sh -> sh.solution).collect(Collectors.toList()));
        } else {
            final Set<SolutionHolder> newDominatedSet = new HashSet<>();
            for (SolutionHolder iNew : dominatedSet) {
                for (Solution iOld : ranks.get(i)) {
                    if (ComparisonUtils.dominates(iNew.solution.getObjectives(), iOld.getObjectives(), iNew.solution.getObjectives().length) < 0) {
                        newDominatedSet.add(new SolutionHolder(iOld));
                    }
                }

                final List<Solution> newRank = new ArrayList<>();
                for (Solution s : ranks.get(i)) {
                    if (!newDominatedSet.contains(new SolutionHolder(s)))
                        newRank.add(s);
                }
                newRank.add(iNew.solution);
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

    @SuppressWarnings("unused")
    public void validate() {
        for (int i = 0; i < ranks.size(); ++i) {
            for (Solution ind : ranks.get(i)) {
                int rankCalcd = 0;
                double[] determinator = null;
                for (SolutionHolder compInd : individuals) {
                    final double[] objectives = compInd.solution.getObjectives();
                    if (objectives != ind.getObjectives() && ComparisonUtils.dominates(objectives, ind.getObjectives(), objectives.length) < 0) {
                        //if (compInd != ind && compInd.compareDom(ind) < 0) {
                        int compRank = detRankOfExPoint(objectives);
                        if (compRank + 1 > rankCalcd) {
                            rankCalcd = compRank + 1;
                            determinator = objectives;
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
