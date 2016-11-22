package ru.itmo.mbuzdalov;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiConsumer;

public class Hypercube {
    private Random rng = new Random(366239);

    public void callOn(int dim, int size, BiConsumer<double[][], int[]> whatToCall) {
        double[][] cube = genShuffledHypercube(dim, size);
        int[] sums = new int[cube.length];
        for (int i = 0; i < cube.length; ++i) {
            double sum = 0;
            for (int j = 0; j < dim; ++j) {
                sum += cube[i][j];
            }
            sums[i] = (int) Math.round(sum);
        }
        whatToCall.accept(cube, sums);
    }

    private double[][] genShuffledHypercube(int dim, int size) {
        double[][] cube = genHypercube(dim, size);
        Collections.shuffle(Arrays.asList(cube), rng);
        return cube;
    }

    private double[][] genHypercube(int dim, int size) {
        if (dim == 1) {
            double[][] rv = new double[size][1];
            for (int i = 0; i < size; ++i) {
                rv[i][0] = i;
            }
            return rv;
        } else {
            double[][] prev = genHypercube(dim - 1, size);
            double[][] rv = new double[prev.length * size][dim];
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < prev.length; ++j) {
                    int idx = j + prev.length * i;
                    rv[idx][dim - 1] = i;
                    System.arraycopy(prev[j], 0, rv[idx], 0, dim - 1);
                }
            }
            return rv;
        }
    }
}