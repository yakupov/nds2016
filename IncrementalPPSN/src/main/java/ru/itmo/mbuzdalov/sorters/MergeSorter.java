package ru.itmo.mbuzdalov.sorters;


public class MergeSorter {
    private final int[] scratch;
    private int[] indices = null;
    private int secondIndex = -1;
    private double[][] reference = null;
    private int[] eqComp = null;

    public MergeSorter(int size) {
        this.scratch = new int[size];
    }

    public void lexSort(int[] indices, int from, int until, double[][] reference, int[] eqComp) {
        this.indices = indices;
        this.reference = reference;
        this.eqComp = eqComp;
        lexSortImpl(from, until, 0, 0);
        this.eqComp = null;
        this.reference = null;
        this.indices = null;
    }

    private int lexSortImpl(int from, int until, int currIndex, int compSoFar) {
        if (from + 1 < until) {
            secondIndex = currIndex;
            sortImpl(from, until);
            secondIndex = -1;

            if (currIndex + 1 == reference[0].length) {
                eqComp[indices[from]] = compSoFar;
                for (int i = from + 1; i < until; ++i) {
                    int prev = indices[i - 1], curr = indices[i];
                    if (reference[prev][currIndex] != reference[curr][currIndex]) {
                        ++compSoFar;
                    }
                    eqComp[curr] = compSoFar;
                }
                return compSoFar + 1;
            } else {
                int lastIndex = from;
                for (int i = from + 1; i < until; ++i) {
                    if (reference[indices[lastIndex]][currIndex] != reference[indices[i]][currIndex]) {
                        compSoFar = lexSortImpl(lastIndex, i, currIndex + 1, compSoFar);
                        lastIndex = i;
                    }
                }
                return lexSortImpl(lastIndex, until, currIndex + 1, compSoFar);
            }
        } else {
            eqComp[indices[from]] = compSoFar;
            return compSoFar + 1;
        }
    }

    public void sort(int[] indices, int from, int until, double[][] reference, int secondIndex) {
        this.indices = indices;
        this.reference = reference;
        this.secondIndex = secondIndex;
        sortImpl(from, until);
        this.indices = null;
        this.reference = null;
        this.secondIndex = -1;
    }

    private void sortImpl(int from, int until) {
        if (from + 1 < until) {
            int mid = (from + until) >>> 1;
            sortImpl(from, mid);
            sortImpl(mid, until);
            int i = from, j = mid, k = 0, kMax = until - from;
            while (k < kMax) {
                if (i == mid || j < until && reference[indices[j]][secondIndex] < reference[indices[i]][secondIndex]) {
                    scratch[k] = indices[j];
                    ++j;
                } else {
                    scratch[k] = indices[i];
                    ++i;
                }
                ++k;
            }
            System.arraycopy(scratch, 0, indices, from, kMax);
        }
    }
}
