package ru.itmo.ppsn;

import org.junit.Assert;
import org.junit.Test;
import ru.itmo.nds.IncrementalPPSN;
import ru.itmo.nds.PPSN2014;
import ru.itmo.nds.RankedPopulation;

public class IncrementalCorrectnessTest {
    private final IncrementalPPSN incrementalPPSN = new IncrementalPPSN();
    private final PPSN2014 ppsn2014 = new PPSN2014();

    @Test
    public void test1() {
        final double[][] testData = new double[][] {
                {-819.0, -917.0, -692.0}, {-786.0, -98.0, -268.0}, {-758.0, -2264.0, -655.0}, {-758.0, -515.0, -226.0},
                {-666.0, -666.0, -666.0}, {-666.0, -666.0, -666.0}, {-636.0, -321.0, -369.0}, {-571.0, -866.0, -524.0},
                {-555.0, -487.0, -980.0}, {-158.0, -517.0, -647.0}, {-43.0, -572.0, -418.0}, {-19.0, -547.0, -935.0},
                {300.0, 300.0, 300.0}, {300.0, 300.0, 300.0}
        };

        final int[] ranks = new int[] {0, 1, 0, 1, 1, 1, 2, 1, 0, 2, 2, 0, 3, 3};

        final double[] newPoint = new double[] {-123, -456, -1};

        final double[][] newPop = new double[testData.length + 1][];
        System.arraycopy(testData, 0, newPop, 0, testData.length);
        newPop[testData.length] = newPoint;

        final int[] newRanks = ppsn2014.performNds(newPop);

        final RankedPopulation rp14 = ppsn2014.performIncrementalNds(testData, ranks, newPoint);
        Assert.assertArrayEquals(newPop, rp14.getPop());
        Assert.assertArrayEquals(newRanks, rp14.getRanks());

        final RankedPopulation rpInc = incrementalPPSN.performIncrementalNds(testData, ranks, newPoint);
        Assert.assertArrayEquals(newPop, rpInc.getPop());
        Assert.assertArrayEquals(newRanks, rpInc.getRanks());
    }
}
