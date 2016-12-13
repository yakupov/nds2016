package ru.ifmo.steady.enlu;

import java.util.*;

import ru.ifmo.steady.*;
import ru.ifmo.steady.util.FastRandom;

/**
 * An implementation of the Deb's ENLU approach
 * which accomodates crowding distance.
 */
public class Storage extends SolutionStorage {
    public void add(Solution solution) {
        addImpl(solution);
    }

    public int getLayerCount() {
        return layers.size();
    }

    public Iterator<Solution> getLayer(int index) {
        if (index < 0 || index >= getLayerCount()) {
            throw new IllegalArgumentException("No such layer: " + index);
        }
        return layers.get(index).iterator();
    }

    public String getName() {
        return "ENLU";
    }

    public QueryResult getRandom() {
        if (size == 0) {
            throw new IllegalStateException("empty data structure");
        }
        return getKth(FastRandom.geneticThreadLocal().nextInt(size));
    }

    public QueryResult getKth(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index " + index + " size = " + size);
        }
        int layer = 0;
        while (index >= layers.get(layer).size()) {
            index -= layers.get(layer).size();
            ++layer;
        }
        List<Solution> layerList = layers.get(layer);
        Solution rv = layerList.get(index);
        if (index == 0 || index == layerList.size() - 1) {
            return new QueryResult(rv, Double.POSITIVE_INFINITY, layer);
        } else {
            double crowding = Solution.crowdingDistance(
                layerList.get(index - 1),
                layerList.get(index + 1),
                layerList.get(0),
                layerList.get(layerList.size() - 1),
                counter
            );
            return new QueryResult(rv, crowding, layer);
        }
    }

    public int size() {
        return size;
    }

    public Solution removeWorst() {
        return removeWorstImpl(1);
    }

    public void removeWorst(int count) {
        removeWorstImpl(count);
    }

    public void clear() {
        size = 0;
        layers.clear();
    }

    /* Implementation */

    private final List<List<Solution>> layers = new ArrayList<>();
    private int size = 0;

    private void addImpl(Solution solution) {
        List<Solution> incomparable = new ArrayList<>();
        List<Solution> dominatedBy = new ArrayList<>();
        ++size;
        for (int i = 0, il = layers.size(); i < il; ++i) {
            List<Solution> layer = layers.get(i);
            boolean isDominated = false;
            for (int j = 0, jl = layer.size(); j < jl; ++j) {
                Solution curr = layer.get(j);
                int cmpX = solution.compareX(curr, counter);
                int cmpY = solution.compareY(curr, counter);
                boolean localDominated = cmpX > 0 && cmpY >= 0 || cmpX >= 0 && cmpY > 0;
                boolean localDominates = cmpX < 0 && cmpY <= 0 || cmpX <= 0 && cmpY < 0;
                if (localDominates) {
                    dominatedBy.add(curr);
                } else if (localDominated) {
                    isDominated = true;
                    break;
                } else {
                    incomparable.add(curr);
                }
            }
            if (!isDominated) {
                if (incomparable.isEmpty()) {
                    List<Solution> newLayer = new ArrayList<>();
                    newLayer.add(solution);
                    layers.add(i, newLayer);
                } else {
                    layers.set(i, incomparable);
                    int position = 0;
                    while (position < incomparable.size() && solution.compareX(incomparable.get(position), counter) > 0) {
                        ++position;
                    }
                    incomparable.add(position, solution);
                    layer.clear();
                    push(i + 1, dominatedBy, layer, new ArrayList<>());
                }
                return;
            } else {
                incomparable.clear();
                dominatedBy.clear();
            }
        }
        List<Solution> newLayer = new ArrayList<>();
        newLayer.add(solution);
        layers.add(newLayer);
    }

    public void removeWorstDebCompatible(int count) {
        if (size < count) {
            throw new IllegalStateException("not enough elements to remove");
        }
        size -= count;
        while (layers.get(layers.size() - 1).size() <= count) {
            count -= layers.get(layers.size() - 1).size();
            layers.remove(layers.size() - 1);
        }
        if (count > 0) {
            List<Solution> lastLayer = layers.get(layers.size() - 1);
            int lls = lastLayer.size();
            double[] crowding = new double[lls];
            Integer[] indices = new Integer[lls];
            Solution min = lastLayer.get(0);
            Solution max = lastLayer.get(lls - 1);
            for (int i = 0; i < lls; ++i) {
                indices[i] = i;
                crowding[i] = Solution.crowdingDistance(
                    i == 0 ? null : lastLayer.get(i - 1),
                    i + 1 == lls ? null : lastLayer.get(i + 1),
                    min, max, counter
                );
            }
            Arrays.sort(indices, (l, r) -> Double.compare(crowding[r], crowding[l]));
            int remain = lls - count;
            Arrays.sort(indices, 0, remain);
            List<Solution> newContents = new ArrayList<>(remain);
            for (int i = 0; i < remain; ++i) {
                newContents.add(lastLayer.get(indices[i]));
            }
            layers.set(layers.size() - 1, newContents);
        }
    }

    private Solution removeWorstImpl(int count) {
        if (size < count) {
            throw new IllegalStateException("empty data structure");
        }
        size -= count;
        while (layers.get(layers.size() - 1).size() < count) {
            count -= layers.get(layers.size() - 1).size();
            layers.remove(layers.size() - 1);
        }
        List<Solution> lastLayer = layers.get(layers.size() - 1);
        if (lastLayer.size() < count) {
            throw new AssertionError();
        } else {
            Solution last = null;
            List<Integer> worst = new ArrayList<>();
            while (count-- > 0) {
                if (lastLayer.size() == 1) {
                    Solution rv = lastLayer.get(0);
                    layers.remove(layers.size() - 1);
                    return rv;
                }
                double worstCrowding = Double.POSITIVE_INFINITY;
                worst.clear();
                int lls = lastLayer.size();
                Solution min = lastLayer.get(0);
                Solution max = lastLayer.get(lls - 1);
                for (int i = 0; i < lls; ++i) {
                    double curr = Solution.crowdingDistance(
                        i == 0 ? null : lastLayer.get(i - 1),
                        i + 1 == lls ? null : lastLayer.get(i + 1),
                        min, max, counter
                    );
                    if (worstCrowding > curr) {
                        worstCrowding = curr;
                        worst.clear();
                    }
                    if (curr == worstCrowding) {
                        worst.add(i);
                    }
                }
                int index = worst.get(FastRandom.geneticThreadLocal().nextInt(worst.size()));
                last = lastLayer.remove(index);
            }
            return last;
        }
    }

    // the last two are scratch lists and are expected to be empty
    private void push(int layer, List<Solution> pushed,
                                 List<Solution> incomparable, List<Solution> dominated) {
        if (pushed.size() > 0) {
            if (layer == layers.size()) {
                layers.add(pushed);
            } else {
                List<Solution> currentLayer = layers.get(layer);
                Solution min = pushed.get(0);
                boolean pushedAdded = false;
                for (int i = 0, il = currentLayer.size(); i < il; ++i) {
                    boolean isDominated = false;
                    Solution si = currentLayer.get(i);
                    for (int j = 0, jl = pushed.size(); j < jl; ++j) {
                        Solution sj = pushed.get(j);
                        int cmpX = sj.compareX(si, counter);
                        int cmpY = sj.compareY(si, counter);
                        if (cmpX <= 0 && cmpY < 0 || cmpX < 0 && cmpY <= 0) {
                            isDominated = true;
                            break;
                        }
                    }
                    if (!isDominated) {
                        if (!pushedAdded && si.compareX(min, counter) > 0) {
                            incomparable.addAll(pushed);
                            pushedAdded = true;
                        }
                        incomparable.add(si);
                    } else {
                        dominated.add(si);
                    }
                }
                if (!pushedAdded) {
                    incomparable.addAll(pushed);
                }
                layers.set(layer, incomparable);
                pushed.clear();
                currentLayer.clear();
                push(layer + 1, dominated, pushed, currentLayer);
            }
        }
    }
}
