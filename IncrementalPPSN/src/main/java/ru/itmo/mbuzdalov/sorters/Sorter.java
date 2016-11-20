package ru.itmo.mbuzdalov.sorters;

/**
 * A base class for all sorters.
 * A sorter supports two getter methods (for size and dimension)
 * and the method for actual sorting.
 */
public abstract class Sorter {
    protected final int size;
    protected final int dim;

    protected Sorter(int size, int dim) {
        this.size = size;
        this.dim = dim;
    }

    /**
     * Returns the size of the problem this sorter can handle.
     * @return the size of the problem.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the dimension of the problem this sorter can handle.
     * @return the dimension of the problem.
     */
    public int dimension() {
        return dim;
    }

    /**
     * Performs the non-dominated sorting of the given input array
     * and stores the results in the given output array.
     *
     * The input array should have the dimensions of exactly {#size()} * {#dimension()},
     * otherwise an IllegalArgumentException is thrown.
     *
     * The output array should have the dimension of exactly {#size()},
     * otherwise an IllegalArgumentException is thrown.
     *
     * The method does not change the {#input} array and fills the {#output} array by layer indices:
     * <code>i</code>th element of {#output} will be the layer index of the <code>i</code>th point from {#input}.
     * The layer 0 corresponds to the non-dominated layer of solutions, the layer 1 corresponds to solutions which
     * are dominated by solutions from layer 0 only, and so far.
     *
     * @param input - the array which is to be sorted.
     * @param output - the array which is filled with the front indices of the corresponding input elements.
     */
    public void sort(double[][] input, int[] output) {
        if (input.length != size) {
            throw new IllegalArgumentException(
                "Input size (" + input.length + ") does not match the sorter's size (" + size + ")"
            );
        }
        if (output.length != size) {
            throw new IllegalArgumentException(
                "Output size (" + output.length + ") does not match the sorter's size (" + size + ")"
            );
        }
        for (int i = 0; i < size; ++i) {
            if (input[i].length != dim) {
                throw new IllegalArgumentException(
                    "Input dimension at index " + i + " (" + input[i].length +
                            ") does not match the sorter's dimension (" + dim + ")"
                );
            }
        }
        sortImpl(input, output);
    }

    protected abstract void sortImpl(double[][] input, int[] output);

}
