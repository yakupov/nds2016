package ru.itmo.mbuzdalov.sorters;

/**
 * 2D sorter: binary search on layer tails; should be faster than the general one.
 */
public final class Sorter2D extends Sorter {
    private final int[] indices;
    private final int[] eqComp;
    private final int[] frontTails;
    private final MergeSorter sorter;

    public Sorter2D(int size) {
        super(size, 2);
        indices = new int[size];
        eqComp = new int[size];
        frontTails = new int[size];
        sorter = new MergeSorter(size);
    }

    protected void sortImpl(double[][] input, int[] output) {
        for (int i = 0; i < size; ++i) {
            indices[i] = i;
        }
        sorter.lexSort(indices, 0, size, input, eqComp);
        output[indices[0]] = 0;
        frontTails[0] = indices[0];
        int nLayers = 1;
        for (int i = 1; i < size; ++i) {
            int curr = indices[i];
            double curr1 = input[curr][1];
            if (eqComp[curr] == eqComp[indices[i - 1]]) {
                output[curr] = output[indices[i - 1]];
            } else if (input[frontTails[0]][1] > curr1) {
                output[curr] = 0;
                frontTails[0] = curr;
            } else {
                int left = 0, right = nLayers;
                // left definitely dominates, right definitely not
                while (right - left > 1) {
                    int mid = (left + right) >>> 1;
                    if (input[frontTails[mid]][1] > curr1) {
                        right = mid;
                    } else {
                        left = mid;
                    }
                }
                if (right == nLayers) {
                    ++nLayers;
                }
                output[curr] = right;
                frontTails[right] = curr;
            }
        }
    }
}
