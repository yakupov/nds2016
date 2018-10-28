package ru.itmo.util;

import java.util.Objects;
import java.util.Random;

/**
 * Simple QS algorithm implementation
 */
public class QuickSelect {
    private final Random random = new Random();
    {
        random.setSeed(System.nanoTime());
    }

    public double getMedian(double[] values) {
        Objects.requireNonNull(values);
        return getKthElement(values, values.length / 2);
    }

    @SuppressWarnings("WeakerAccess") //RFU
    public double getKthElement(double[] values, int k) {
        if (values == null || k >= values.length) {
            throw new IllegalArgumentException("Null array or the k is too large");
        } else {
            return values[select(values, 0, values.length - 1, k)];
        }
    }

    /**
     * @param values Array of elements to select from
     * @param from   Start index
     * @param to     End index
     * @param k      k
     * @return {@code k}-th smallest element in the part of {@code values} from start index to end index
     */
    int select(double[] values, int from, int to, int k) {
        if (from == to)
            return from;

        final int nextPivotIndex = partition(values, from, to, calcPivot(values, from, to));
        if (k == nextPivotIndex)
            return k;
        else if (k < nextPivotIndex)
            return select(values, from, nextPivotIndex - 1, k);
        else
            return select(values, nextPivotIndex + 1, to, k);
    }

    private int partition(double[] values, int from, int to, int pivotIndex) {
        if (values == null || from >= values.length || to >= values.length) {
            throw new IllegalArgumentException("Null array or incorrect indices");
        } else {
            final double pivot = values[pivotIndex];
            swap(values, pivotIndex, to);
            int storeIndex = from;
            for (int i = from; i < to; ++i) {
                if (values[i] < pivot)
                    swap(values, i, storeIndex++);
            }
            swap(values, to, storeIndex);
            return storeIndex;
        }
    }

    void swap(double[] array, int i1, int i2) {
        if (array == null || i1 >= array.length || i2 >= array.length) {
            throw new IllegalArgumentException("Null array or incorrect indices");
        } else if (i1 != i2) {
            double t1 = array[i1];
            array[i1] = array[i2];
            array[i2] = t1;
        }
    }

    protected int calcPivot(double[] values, int from, int to) {
        return random.nextInt(to - from) + from;
    }
}
