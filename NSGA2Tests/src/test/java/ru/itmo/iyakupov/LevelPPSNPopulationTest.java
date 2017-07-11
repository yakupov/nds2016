package ru.itmo.iyakupov;

import org.junit.Test;
import org.moeaframework.core.Solution;
import ru.itmo.iyakupov.ss.pop.IPopulation;
import ru.itmo.iyakupov.ss.pop.LevelPPSNPopulation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LevelPPSNPopulationTest {
    @Test
    public void test5() {
        final IPopulation pop = new LevelPPSNPopulation();

        pop.addSolution(new Solution(new double[]{0.1669424402868558, 0.41123196219828895, 17.98980401569634}));
        assertEquals(1, pop.getLayers().size());
        assertEquals(1, pop.getLayers().get(0).size());

        pop.addSolution(new Solution(new double[]{0.917634913762617, 0.9778742572218526, 16.9584650345564}));
        assertEquals(1, pop.getLayers().size());
        assertEquals(2, pop.getLayers().get(0).size());

        final double[] p3 = {0.8868046448171203, 0.5802605728140939, 18.793267306998885};
        pop.addSolution(new Solution(p3));
        assertEquals(2, pop.getLayers().size());
        assertEquals(2, pop.getLayers().get(0).size());
        assertEquals(1, pop.getLayers().get(1).size());
        assertArrayEquals(p3, pop.getLayers().get(1).get(0).getObjectives(), 0.0);

        pop.addSolution(new Solution(new double[]{0.40892166575913325, 0.026280324605388206, 21.255937437050655}));
        assertEquals(2, pop.getLayers().size());
        assertEquals(3, pop.getLayers().get(0).size());
        assertEquals(1, pop.getLayers().get(1).size());

        pop.addSolution(new Solution(new double[]{0.6305014841432228, 0.5990732774500678, 18.139060039219498}));
        assertEquals(2, pop.getLayers().size());
        assertEquals(3, pop.getLayers().get(0).size());
        assertEquals(2, pop.getLayers().get(1).size());


    }
}
