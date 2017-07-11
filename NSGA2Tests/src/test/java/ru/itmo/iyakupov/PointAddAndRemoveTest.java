package ru.itmo.iyakupov;

import org.junit.Test;
import org.moeaframework.core.Solution;
import ru.itmo.iyakupov.ss.pop.ENLUPopulation;
import ru.itmo.iyakupov.ss.pop.IPopulation;
import ru.itmo.iyakupov.ss.pop.LevelPPSNPopulation;
import ru.itmo.iyakupov.ss.pop.TreapPopulation;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class PointAddAndRemoveTest {
    @Test
    public void testTreaps() {
        doTest(TreapPopulation::new);
    }

    @Test
    public void testLevel() {
        doTest(LevelPPSNPopulation::new);
    }

    @Test
    public void testEnlu() {
        doTest(ENLUPopulation::new);
    }

    private void doTest(final Supplier<IPopulation> supplier) {
        final IPopulation pop = supplier.get();
        pop.addSolution(new Solution(new double[]{10, 10}));
        pop.addSolution(new Solution(new double[]{5, 20}));
        pop.addSolution(new Solution(new double[]{0, 30}));

        assertNotNull(pop.getLayers());
        assertEquals(1, pop.getLayers().size());

        List<Solution> rank0 = pop.getLayers().get(0);
        assertEquals(3, rank0.size());

        pop.addSolution(new Solution(new double[]{0, 0}));

        assertEquals(2, pop.getLayers().size());

        rank0 = pop.getLayers().get(0);
        assertEquals(1, rank0.size());
        assertArrayEquals(new double[]{0, 0}, rank0.get(0).getObjectives(), 0.0);

        List<Solution> rank1 = pop.getLayers().get(1);
        assertEquals(3, rank1.size());

        pop.removeWorstSolution();
        assertEquals(2, pop.getLayers().size());
        rank0 = pop.getLayers().get(0);
        assertEquals(1, rank0.size());
        rank1 = pop.getLayers().get(1);
        assertEquals(2, rank1.size());

        pop.removeWorstSolution();
        assertEquals(2, pop.getLayers().size());
        rank0 = pop.getLayers().get(0);
        assertEquals(1, rank0.size());
        rank1 = pop.getLayers().get(1);
        assertEquals(1, rank1.size());

        pop.removeWorstSolution();
        assertEquals(1, pop.getLayers().size());
        rank0 = pop.getLayers().get(0);
        assertEquals(1, rank0.size());

        pop.removeWorstSolution();
        assertEquals(0, pop.getLayers().size());
    }
}
