package ru.itmo.iyakupov;

import java.util.Arrays;

/**
 * QS using Median of Medians pivot selection strategy
 */
public class MedianOfMediansQS extends QuickSelect {
    @Override
    protected int calcPivot(final double[] values, final int from, final int to) {
        if (to - from < 5) {
            return partition5(values, from, to);
        } else {
            for (int i = from; i <= to; i += 5) {
                final int median5 = partition5(values, i, Math.min(i + 4, to));
                //System.out.println(from + " " + to + " " + median5 + " " + i);
                swap(values, median5, (int) (from + Math.floor((i - from) / 5)));
            }
        }
        return select(values, from, (int) (from + Math.ceil((to - from) / 5) - 1), from + (to - from)/10);
    }

    private int partition5(double[] values, int from, int to) {
        if (values == null || from >= values.length || to >= values.length) {
            throw new IllegalArgumentException("Null array or incorrect indices");
        } else {
            Arrays.sort(values, from, to);
            return (to - from) / 2 + from;
        }
    }
}
