package ru.itmo.ppsn;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.RankedPopulation;

public class IncrementalStupidPerformanceTest {
    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    private final double[][] testPop1 = new double[][] {
            {-819.0, -917.0, -692.0}, {-786.0, -98.0, -268.0}, {-758.0, -2264.0, -655.0}, {-758.0, -515.0, -226.0},
            {-666.0, -666.0, -666.0}, {-666.0, -666.0, -666.0}, {-636.0, -321.0, -369.0}, {-571.0, -866.0, -524.0},
            {-555.0, -487.0, -980.0}, {-158.0, -517.0, -647.0}, {-43.0, -572.0, -418.0}, {-19.0, -547.0, -935.0},
            {300.0, 300.0, 300.0}, {300.0, 300.0, 300.0}
    };

    private final int[] testPop1Ranks = new int[] {0, 1, 0, 1, 1, 1, 2, 1, 0, 2, 2, 0, 3, 3};

    private final double[] newPoint1 = new double[] {-123, -456, -1};

    private void doTest(PPSN2014 sorter, double[][] pop, int[] ranks, double[] addend, int runs, int warmup) {
        double begin = 0;
        for (int cnt = 0; cnt < runs; ++cnt) {
            if (cnt == warmup)
                begin = System.nanoTime();

            final RankedPopulation rp14 = sorter.performIncrementalNds(pop, ranks, addend);
            Assert.assertNotNull(rp14.getPop());
            Assert.assertNotNull(rp14.getRanks());
        }

        System.out.println("time: " + (System.nanoTime() - begin));
    }


    @Ignore
    @Test
    public void testNew1() throws Exception {
        doTest(incrementalPPSN, testPop1, testPop1Ranks, newPoint1, 10000000, 1000000);
    }

    @Ignore
    @Test
    public void testOld1() throws Exception {
        doTest(ppsn2014, testPop1, testPop1Ranks, newPoint1, 10000000, 1000000);
    }
}
