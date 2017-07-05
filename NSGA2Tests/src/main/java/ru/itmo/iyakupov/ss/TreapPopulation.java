package ru.itmo.iyakupov.ss;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.iyakupov.ss.treap2015.Treap;
import ru.itmo.iyakupov.ss.treap2015.TreapPopulationImpl;

import java.util.*;

import static org.moeaframework.core.NondominatedSorting.CROWDING_ATTRIBUTE;

public class TreapPopulation implements IPopulation {
    private final TreapPopulationImpl impl;
    private final Comparator<Solution> comparator;

    public TreapPopulation() {
        this(new TreapPopulationImpl(), new CrowdingComparator());
    }

    private TreapPopulation(TreapPopulationImpl impl, Comparator<Solution> comparator) {
        this.impl = impl;
        this.comparator = comparator;
    }

    @Override
    public List<Solution> getRandomSolutions(int count) {
        final int[] levelOffsets = new int[impl.getRanks().size() + 1];
        int i = 0;
        for (Treap treap : impl.getRanks()) {
            levelOffsets[i + 1] = levelOffsets[i] + treap.size();
            ++i;
        }
        final int totalCount = levelOffsets[impl.getRanks().size()];

        final ArrayList<Solution> res = new ArrayList<>();
        final Random random = new Random(System.nanoTime());
        for (i = 0; i < count; ++i) {
            final int indexInPop = random.nextInt(totalCount);
            final Index index = IPopulation.binarySearchForBucket(levelOffsets, indexInPop);
            res.add(impl.getRanks().get(index.bucketIndex).getOrdered(index.indexInBucket).x);
        }

        return res;
    }

    @Override
    public List<Solution> getBestSolutions(int count) {
        throw new UnsupportedOperationException("Deprecated");
    }

    @Override
    public boolean removeWorstSolution() {
        final List<Treap> levels = impl.getRanks();
        if (levels.isEmpty())
            return false;

        final Treap lastLevel = levels.get(levels.size() - 1);
        Treap worst = lastLevel;
        final Queue<Treap> queue = new LinkedList<>();
        queue.add(lastLevel);
        while (!queue.isEmpty()) {
            final Treap t = queue.remove();
            if (comparator.compare(worst.x, t.x) > 0)
                worst = t;
            if (t.left != null)
                queue.add(t.left);
            if (t.right != null)
                queue.add(t.right);
        }

        final Treap merged = removeSolution(lastLevel, worst.x);
        levels.set(levels.size() - 1, merged);

        return true;
    }

    private Treap removeSolution(Treap level, Solution x) {
        final Treap.Treaps cut = level.splitX(x);
        Treap removeLeftChildFrom = cut.r;
        while (removeLeftChildFrom != null) {
            if (removeLeftChildFrom.left == null)
                break;
            else if (removeLeftChildFrom.left.left == null)
                removeLeftChildFrom.left = removeLeftChildFrom.left.right;
            else
                removeLeftChildFrom = removeLeftChildFrom.left;
        }

        return Treap.merge(cut.l, cut.r);
    }

    private static List<Solution> treapToList(Treap level) {
        final List<Solution> solutionList = new ArrayList<>();
        final Queue<Treap> queue = new LinkedList<>();
        queue.add(level);
        while (!queue.isEmpty()) {
            final Treap t = queue.remove();
            solutionList.add(t.x);
            if (t.left != null)
                queue.add(t.left);
            if (t.right != null)
                queue.add(t.right);
        }

        return solutionList;
    }

    @Override
    public boolean addSolution(Solution solution) {
        final int rank = impl.addPoint(solution);
        updateCrowdingDistance(rank);
        return true;
    }

    private void updateCrowdingDistance(int rank) {
        Treap level = impl.getRanks().get(rank);

        final List<Solution> nonDuplicates = new ArrayList<>(level.size());
        final Set<Solution> toEvict = new HashSet<>(level.size());

        final Queue<Treap> queue = new LinkedList<>();
        queue.add(level);
        while (!queue.isEmpty()) {
            final Treap t = queue.remove();
            if (t.left != null)
                queue.add(t.left);
            if (t.right != null)
                queue.add(t.right);

            final Solution solution = t.x;
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

        for (Solution solution : toEvict) {
            level = removeSolution(level, solution);
        }
        impl.getRanks().set(rank, level);

        // then compute the crowding distance for the unique solutions
        int n = level.size();

        if (n < 3) {
            queue.clear();
            queue.add(level);
            while (!queue.isEmpty()) {
                final Treap t = queue.remove();
                if (t.left != null)
                    queue.add(t.left);
                if (t.right != null)
                    queue.add(t.right);
                t.x.setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);
            }
        } else {
            final List<Solution> front = treapToList(level);
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

    @Override
    public int size() {
        return impl.size();
    }

    @Override
    public Map<Integer, List<Solution>> getLayers() {
        final Map<Integer, List<Solution>> map = new HashMap<>();
        for (int i = 0; i < impl.getRanks().size(); ++i) {
            map.put(i, treapToList(impl.getRanks().get(i)));
        }
        return map;
    }
}
