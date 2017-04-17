package ru.itmo.ppsn.playground;

import org.junit.Ignore;
import org.junit.Test;
import ru.itmo.nds.util.RankedIndividual;
import ru.itmo.util.MedianOfMediansQS;
import ru.itmo.nds.PPSN2014;
import ru.itmo.util.QuickSelect;
import ru.itmo.mbuzdalov.FasterNonDominatedSorting;
import ru.itmo.mbuzdalov.sorters.MergeSorter;
import ru.itmo.mbuzdalov.sorters.Sorter;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class Playground {
    private static final double[][] pop = new double[][]{
            {300, 300, 300}, {-666, -666, -666}, {-758, -2264, -655}, {-43, -572, -418}, {-158, -517, -647},
            {-636, -321, -369}, {-19, -547, -935}, {-571, -866, -524}, {-819, -917, -692}, {-555, -487, -980},
            {-758, -515, -226}, {-786, -98, -268}, {-666, -666, -666}, {300, 300, 300}
    };

    private static final double[][] pop2d = new double[][]{
            {300, 300}, {-666, -666}, {-758, -2264}, {-43, -572}, {-158, -517},
            {-636, -321}, {-19, -547}, {-571, -866}, {-819, -917}, {-555, -487},
            {-758, -515}, {-786, -98}, {-666, -666}, {300, 300}
    };

    private static final MergeSorter mergeSorter = new MergeSorter(pop.length);

    private void lexSort(double[][] lPop) {
        Arrays.sort(lPop, (o1, o2) -> {
            for (int i = 0; i < o1.length; ++i) {
                if (o1[i] < o2[i])
                    return -1;
                else if (o1[i] > o2[i])
                    return 1;
            }
            return 0;
        });
    }

    @Ignore
    @Test
    public void testPlayground1() throws Exception {
        final double[][] pop = new double[][]{
                {300, 300, 300}, {-666, -666, -666}, {-758, -2264, -655}, {-43, -572, -418}, {-158, -517, -647},
                {-636, -321, -369}, {-19, -547, -935}, {-571, -866, -524}, {-819, -917, -692}, {-555, -487, -980},
                {-758, -515, -226}, {-786, -98, -268}, {-666, -666, -666}, {300, 300, 300}
        };


        final int[] indices = new int[pop.length];
        for (int i = 0; i < indices.length; ++i) {
            indices[i] = i;
        }

        final int[] eqComp = new int[pop.length];

        mergeSorter.lexSort(indices, 0, pop.length, pop, eqComp);

        System.out.println("Indices: " + Arrays.toString(indices));
        System.out.println("EqComp: " + Arrays.toString(eqComp));
        System.out.println("Pop: ");
        for (double[] d : pop) {
            System.out.println("\t" + Arrays.toString(d));
        }

        System.out.println("Pop ind: ");
        for (int i : indices) {
            System.out.println("\t" + Arrays.toString(pop[i]));
        }

        lexSort(pop);

        System.out.println("lPop: ");
        for (double[] d : pop) {
            System.out.println("\t" + Arrays.toString(d));
        }
    }


    @Ignore
    @Test
    public void testMaxsLexSort() throws Exception {
        double begin = 0;
        for (int cnt = 0; cnt < 1000000; ++cnt) {
            if (cnt == 100000)
                begin = System.nanoTime();

            final double[][] copPop = new double[pop.length][];
            System.arraycopy(pop, 0, copPop, 0, pop.length);

            final int[] indices = new int[pop.length];
            for (int i = 0; i < indices.length; ++i) {
                indices[i] = i;
            }

            final int[] eqComp = new int[pop.length];
            mergeSorter.lexSort(indices, 0, pop.length, copPop, eqComp);
        }

        System.out.println("Old time: " + (System.nanoTime() - begin));
    }

    @Ignore
    @Test
    public void testCollectionsLexSort() throws Exception {
        double begin = 0;
        for (int cnt = 0; cnt < 1000000; ++cnt) {
            if (cnt == 100000)
                begin = System.nanoTime();

            final double[][] copPop = new double[pop.length][];
            System.arraycopy(pop, 0, copPop, 0, pop.length);

            lexSort(copPop);
        }

        System.out.println("Java time: " + (System.nanoTime() - begin));
    }

    @Ignore
    @Test
    public void testMedianCalculation() throws Exception {
        final Random random = new Random();
        random.setSeed(System.nanoTime());

        final double[] origTestData = new double[random.nextInt(50) + 10];
        for (int i = 0; i < origTestData.length; ++i) {
            origTestData[i] = Math.floor(random.nextDouble() * (random.nextInt(1000) - 500));
        }
        System.out.println("test data: " + Arrays.toString(origTestData));

        final double m1, m2, m3;

        double[] testData = new double[origTestData.length];
        System.arraycopy(origTestData, 0, testData, 0, testData.length);
        Arrays.sort(testData);
        m1 = testData[testData.length / 2];
        System.out.println(m1);

        testData = new double[origTestData.length];
        System.arraycopy(origTestData, 0, testData, 0, testData.length);
        m2 = new QuickSelect().getMedian(testData);
        System.out.println(m1);

        testData = new double[origTestData.length];
        System.arraycopy(origTestData, 0, testData, 0, testData.length);
        m3 = new MedianOfMediansQS().getMedian(testData);
        System.out.println(m3);

        assertEquals(m1, m2, 0.0);
        assertEquals(m1, m3, 0.0);
    }

    @Ignore
    @Test
    public void testRewrittenNDS() throws Exception {
        final double[][] copPop = new double[pop.length][];
        System.arraycopy(pop, 0, copPop, 0, pop.length);

        System.out.println("Before sort: ");
        for (double[] d : copPop) {
            System.out.println("\t" + Arrays.toString(d));
        }

        final int[] ranks = new PPSN2014<double[]>(d -> d).performNds(copPop);

        System.out.println("After sort: ");
        for (double[] d : copPop) {
            System.out.println("\t" + Arrays.toString(d));
        }

        System.out.println("Ranks: " + Arrays.toString(ranks));

        final Sorter maxSorter = FasterNonDominatedSorting.getSorter(pop.length, 3);
        final int[] ranksMax = new int[pop.length];
        maxSorter.sort(pop, ranksMax);
        System.out.println("Ranks: " + Arrays.toString(RankedIndividual.sortRanksForLexSortedPopulation(ranksMax, pop)));
    }

    @Ignore
    @Test
    public void testRewrittenNDS2d() throws Exception {
        final double[][] copPop = new double[pop2d.length][];
        System.arraycopy(pop2d, 0, copPop, 0, pop2d.length);

        System.out.println("Before sort: ");
        for (double[] d : copPop) {
            System.out.println("\t" + Arrays.toString(d));
        }

        System.setProperty(PPSN2014.ENABLE_PPSN_TRACE_PROPERTY, "true");
        final int[] ranks = new PPSN2014<double[]>(d -> d).performNds(copPop);

        System.out.println("After sort: ");
        for (double[] d : copPop) {
            System.out.println("\t" + Arrays.toString(d));
        }

        System.out.println("Ranks: " + Arrays.toString(ranks));
    }


}
