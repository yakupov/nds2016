package ru.itmo.ppsn;

import org.junit.Ignore;
import org.junit.Test;
import ru.itmo.mbuzdalov.FasterNonDominatedSorting;
import ru.itmo.mbuzdalov.sorters.Sorter;
import ru.itmo.nds.PPSN2014;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class StupidPerformanceTest {
    private static final double[][] pop = new double[][]{
            {300, 300, 300}, {-666, -666, -666}, {-758, -2264, -655}, {-43, -572, -418}, {-158, -517, -647},
            {-636, -321, -369}, {-19, -547, -935}, {-571, -866, -524}, {-819, -917, -692}, {-555, -487, -980},
            {-758, -515, -226}, {-786, -98, -268}, {-666, -666, -666}, {300, 300, 300}
    };

    @Ignore
    @Test
    public void testNew1() throws Exception {
        double begin = 0;
        for (int cnt = 0; cnt < 1000000; ++cnt) {
            if (cnt == 10000)
                begin = System.nanoTime();

            final double[][] localPop = new double[pop.length][];
            System.arraycopy(pop, 0, localPop, 0, pop.length);

            final int[] ranks = new PPSN2014().performNds(localPop);
            ranks[ranks.length / 2] = ranks.length; //Hopefully it won't be optimized away
        }

        System.out.println("New time: " + (System.nanoTime() - begin));
    }

    @Ignore
    @Test
    public void testOld1() throws Exception {
        double begin = 0;
        for (int cnt = 0; cnt < 1000000; ++cnt) {
            if (cnt == 10000)
                begin = System.nanoTime();

            final double[][] localPop = new double[pop.length][];
            System.arraycopy(pop, 0, localPop, 0, pop.length);

            final Sorter sorter = FasterNonDominatedSorting.getSorter(pop.length, pop[0].length);
            final int[] ranks = new int[pop.length];
            sorter.sort(pop, ranks);
            ranks[ranks.length / 2] = ranks.length; //Hopefully it won't be optimized away
            localPop[ranks.length / 2][0] = 1.0;
        }

        System.out.println("Old time: " + (System.nanoTime() - begin));
    }
}
