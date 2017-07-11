package ru.itmo.iyakupov.ss.pop;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import ru.itmo.iyakupov.ss.treap2015.Treap;
import ru.itmo.iyakupov.ss.treap2015.TreapPopulationImpl;

import java.util.*;

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
            //System.err.println("TREAP " + treap);
            levelOffsets[i + 1] = levelOffsets[i] + treap.size();
            ++i;
        }
        final int totalCount = levelOffsets[impl.getRanks().size()];

        final ArrayList<Solution> res = new ArrayList<>();
        final Random random = new Random(System.nanoTime());
        for (i = 0; i < count; ++i) {
            final int indexInPop = random.nextInt(totalCount);
            final Index index = IPopulation.binarySearchForBucket(levelOffsets, indexInPop);
            res.add(impl.getRanks().get(index.bucketIndex).getOrdered(index.indexInBucket));
        }

        return res;
    }

    @Override
    public boolean removeWorstSolution() {
        final List<Treap> levels = impl.getRanks();
        if (levels.isEmpty())
            return false;

        final Treap lastLevel = levels.get(levels.size() - 1);
        final List<Solution> list = lastLevel.toList();
        Solution worst = list.get(0);
        for (int i = 1; i < list.size(); ++i) {
            final Solution solution = list.get(i);
            if (comparator.compare(worst, solution) > 0)
                worst = solution;
        }

        final Treap merged = lastLevel.remove(worst);
        if (merged == null)
            impl.getRanks().remove(levels.size() - 1);
        else
            impl.getRanks().set(levels.size() - 1, merged);

        return true;
    }

    @Override
    public boolean addSolution(Solution solution) {
        final int rank = impl.addPoint(solution);
        updateCrowdingDistance(rank);
        return true;
    }

    private void updateCrowdingDistance(int rank) {
        Treap level = impl.getRanks().get(rank);
        level = level.updateCrowdingDistance();
        impl.getRanks().set(rank, level);
    }

    @Override
    public int size() {
        return impl.size();
    }

    @Override
    public Map<Integer, List<Solution>> getLayers() {
        final Map<Integer, List<Solution>> map = new HashMap<>();
        for (int i = 0; i < impl.getRanks().size(); ++i) {
            map.put(i, impl.getRanks().get(i).toList());
        }
        return map;
    }
}
