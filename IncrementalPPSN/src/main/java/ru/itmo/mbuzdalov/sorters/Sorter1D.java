package ru.itmo.mbuzdalov.sorters;

/**
 * 1D sorter: do the sorting and uniquification.
 */
public final class Sorter1D extends Sorter {
    private final int[] indices;
    private final MergeSorter sorter;

    public Sorter1D(int size) {
        super(size, 1);
        indices = new int[size];
        sorter = new MergeSorter(size);
    }

    protected void sortImpl(double[][] input, int[] output) {
        for (int i = 0; i < size; ++i) {
            indices[i] = i;
        }
        sorter.sort(indices, 0, size, input, 0);
        output[indices[0]] = 0;
        for (int i = 1; i < size; ++i) {
            int prev = indices[i - 1], curr = indices[i];
            if (input[prev][0] == input[curr][0]) {
                output[curr] = output[prev];
            } else {
                output[curr] = output[prev] + 1;
            }
        }
    }
}
