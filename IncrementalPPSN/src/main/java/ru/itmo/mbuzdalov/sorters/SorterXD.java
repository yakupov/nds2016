package ru.itmo.mbuzdalov.sorters;

import java.util.Arrays;
import java.util.Random;

/**
 * XD sorter: the general case.
 */
public final class SorterXD extends Sorter {
    private final int[] indices;
    private final int[] swap;
    private final int[] eqComp;
    private final MergeSorter sorter;

    private double[][] input;
    private int[] output;

    private int[] fenwickData;
    private double[] fenwickPivots;
    private int fenwickSize;

    private final Random random = new Random();

    private void fenwickInit(int from, int until) {
        for (int i = 0, j = from; j < until; ++i, ++j) {
            fenwickPivots[i] = input[indices[j]][1];
        }
        Arrays.sort(fenwickPivots, 0, until - from);
        int last = 0;
        for (int i = 1; i < until - from; ++i) {
            if (fenwickPivots[i] != fenwickPivots[last]) {
                fenwickPivots[++last] = fenwickPivots[i];
            }
        }
        fenwickSize = last + 1;
        Arrays.fill(fenwickData, 0, fenwickSize, -1);
    }

    private int fenwickIndex(double key) {
        int left = -1, right = fenwickSize;
        while (right - left > 1) {
            int mid = (left + right) >>> 1;
            if (fenwickPivots[mid] <= key) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private void fenwickSet(double key, int value) {
        int fwi = fenwickIndex(key);
        while (fwi < fenwickSize) {
            fenwickData[fwi] = Math.max(fenwickData[fwi], value);
            fwi |= fwi + 1;
        }
    }

    private int fenwickQuery(double key) {
        int fwi = fenwickIndex(key);
        if (fwi >= fenwickSize || fwi < 0) {
            return -1;
        } else {
            int rv = -1;
            while (fwi >= 0) {
                rv = Math.max(rv, fenwickData[fwi]);
                fwi = (fwi & (fwi + 1)) - 1;
            }
            return rv;
        }
    }

    public SorterXD(int size, int dim) {
        super(size, dim);
        indices = new int[size];
        eqComp = new int[size];
        swap = new int[size];
        fenwickData = new int[size];
        fenwickPivots = new double[size];
        sorter = new MergeSorter(size);
    }

    protected void sortImpl(double[][] input, int[] output) {
        for (int i = 0; i < size; ++i) {
            indices[i] = i;
        }
        Arrays.fill(output, 0);
        sorter.lexSort(indices, 0, size, input, eqComp);
        this.input = input;
        this.output = output;
        sort(0, size, dim - 1);
        this.input = null;
        this.output = null;
    }

    private void updateFront(int target, int source) {
        if (eqComp[target] == eqComp[source]) {
            output[target] = output[source];
        } else {
            output[target] = Math.max(output[target], output[source] + 1);
        }
    }

    private void sort2D(int from, int until) {
        fenwickInit(from, until);
        int curr = from;
        while (curr < until) {
            int currI = indices[curr];
            int next = curr + 1;
            while (next < until && eqComp[indices[next]] == eqComp[currI]) {
                ++next;
            }
            int result = Math.max(output[currI], fenwickQuery(input[currI][1]) + 1);
            for (int i = curr; i < next; ++i) {
                output[indices[i]] = result;
            }
            fenwickSet(input[currI][1], result);
            curr = next;
        }
    }

    private void sortHighByLow2D(int lFrom, int lUntil, int hFrom, int hUntil) {
        fenwickInit(lFrom, lUntil);
        int li = lFrom;
        for (int hi = hFrom; hi < hUntil; ++hi) {
            int currH = indices[hi];
            int eCurrH = eqComp[currH];
            while (li < lUntil && eqComp[indices[li]] < eCurrH) {
                int currL = indices[li++];
                fenwickSet(input[currL][1], output[currL]);
            }
            output[currH] = Math.max(output[currH], fenwickQuery(input[currH][1]) + 1);
        }
    }

    private double medianInSwap(int from, int until, int dimension) {
        int to = until - 1;
        int med = (from + until) >>> 1;
        while (from <= to) {
            double pivot = input[swap[from + random.nextInt(to - from + 1)]][dimension];
            int ff = from, tt = to;
            while (ff <= tt) {
                while (input[swap[ff]][dimension] < pivot) ++ff;
                while (input[swap[tt]][dimension] > pivot) --tt;
                if (ff <= tt) {
                    int tmp = swap[ff];
                    swap[ff] = swap[tt];
                    swap[tt] = tmp;
                    ++ff;
                    --tt;
                }
            }
            if (med <= tt) {
                to = tt;
            } else if (med >= ff) {
                from = ff;
            } else {
                return input[swap[med]][dimension];
            }
        }
        return input[swap[from]][dimension];
    }

    private int lessThan, equalTo, greaterThan;

    private void split3(int from, int until, int dimension, double median) {
        lessThan = equalTo = greaterThan = 0;
        for (int i = from; i < until; ++i) {
            int cmp = Double.compare(input[indices[i]][dimension], median);
            if (cmp < 0) {
                ++lessThan;
            } else if (cmp == 0) {
                ++equalTo;
            } else {
                ++greaterThan;
            }
        }
        int lessThanPtr = 0, equalToPtr = lessThan, greaterThanPtr = lessThan + equalTo;
        for (int i = from; i < until; ++i) {
            int cmp = Double.compare(input[indices[i]][dimension], median);
            if (cmp < 0) {
                swap[lessThanPtr++] = indices[i];
            } else if (cmp == 0) {
                swap[equalToPtr++] = indices[i];
            } else {
                swap[greaterThanPtr++] = indices[i];
            }
        }
        System.arraycopy(swap, 0, indices, from, until - from);
    }

    private void merge(int from, int mid, int until) {
        int p0 = from, p1 = mid;
        for (int i = from; i < until; ++i) {
            if (p0 == mid || p1 < until && eqComp[indices[p1]] < eqComp[indices[p0]]) {
                swap[i] = indices[p1++];
            } else {
                swap[i] = indices[p0++];
            }
        }
        System.arraycopy(swap, from, indices, from, until - from);
    }

    private void sortHighByLow(int lFrom, int lUntil, int hFrom, int hUntil, int dimension) {
        int lSize = lUntil - lFrom, hSize = hUntil - hFrom;
        if (lSize == 0 || hSize == 0) {
            return;
        }
        if (lSize == 1) {
            for (int hi = hFrom; hi < hUntil; ++hi) {
                if (dominatesEq(lFrom, hi, dimension)) {
                    updateFront(indices[hi], indices[lFrom]);
                }
            }
        } else if (hSize == 1) {
            for (int li = lFrom; li < lUntil; ++li) {
                if (dominatesEq(li, hFrom, dimension)) {
                    updateFront(indices[hFrom], indices[li]);
                }
            }
        } else if (dimension == 1) {
            sortHighByLow2D(lFrom, lUntil, hFrom, hUntil);
        } else {
            if (maxValue(lFrom, lUntil, dimension) <= minValue(hFrom, hUntil, dimension)) {
                sortHighByLow(lFrom, lUntil, hFrom, hUntil, dimension - 1);
            } else {
                System.arraycopy(indices, lFrom, swap, 0, lSize);
                System.arraycopy(indices, hFrom, swap, lSize, hSize);
                double median = medianInSwap(0, lSize + hSize, dimension);

                split3(lFrom, lUntil, dimension, median);
                int lMidL = lFrom + lessThan, lMidR = lMidL + equalTo;

                split3(hFrom, hUntil, dimension, median);
                int hMidL = hFrom + lessThan, hMidR = hMidL + equalTo;

                sortHighByLow(lFrom, lMidL, hFrom, hMidL, dimension);
                sortHighByLow(lFrom, lMidL, hMidL, hMidR, dimension - 1);
                sortHighByLow(lMidL, lMidR, hMidL, hMidR, dimension - 1);
                merge(lFrom, lMidL, lMidR);
                merge(hFrom, hMidL, hMidR);
                sortHighByLow(lFrom, lMidR, hMidR, hUntil, dimension - 1);
                sortHighByLow(lMidR, lUntil, hMidR, hUntil, dimension);
                merge(lFrom, lMidR, lUntil);
                merge(hFrom, hMidR, hUntil);
            }
        }
    }

    private void sort(int from, int until, int dimension) {
        int size = until - from;
        if (size == 2) {
            if (dominatesEq(from, from + 1, dimension)) {
                updateFront(indices[from + 1], indices[from]);
            }
        } else if (size > 2) {
            if (dimension == 1) {
                sort2D(from, until);
            } else {
                if (allValuesEqual(from, until, dimension)) {
                    sort(from, until, dimension - 1);
                } else {
                    System.arraycopy(indices, from, swap, from, size);
                    double median = medianInSwap(from, until, dimension);

                    split3(from, until, dimension, median);
                    int midL = from + lessThan, midH = midL + equalTo;

                    sort(from, midL, dimension);
                    sortHighByLow(from, midL, midL, midH, dimension - 1);
                    sort(midL, midH, dimension - 1);
                    merge(from, midL, midH);
                    sortHighByLow(from, midH, midH, until, dimension - 1);
                    sort(midH, until, dimension);
                    merge(from, midH, until);
                }
            }
        }
    }

    private boolean allValuesEqual(int from, int until, int k) {
        double value = input[indices[from]][k];
        for (int i = from + 1; i < until; ++i) {
            if (input[indices[i]][k] != value) {
                return false;
            }
        }
        return true;
    }

    private double minValue(int from, int until, int k) {
        double rv = Double.MAX_VALUE;
        for (int i = from; i < until; ++i) {
            rv = Math.min(rv, input[indices[i]][k]);
        }
        return rv;
    }

    private double maxValue(int from, int until, int k) {
        double rv = Double.MIN_VALUE;
        for (int i = from; i < until; ++i) {
            rv = Math.max(rv, input[indices[i]][k]);
        }
        return rv;
    }

    private boolean dominatesEq(int l, int r, int k) {
        int il = indices[l];
        int ir = indices[r];
        for (int i = 0; i <= k; ++i) {
            if (input[il][i] > input[ir][i]) {
                return false;
            }
        }
        return true;
    }
}
