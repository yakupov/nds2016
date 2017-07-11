package ru.itmo.iyakupov;

import org.junit.Test;
import org.moeaframework.core.Solution;
import ru.itmo.iyakupov.ss.treap2015.Treap;
import ru.itmo.iyakupov.ss.treap2015.TreapPopulationImpl;

import static org.junit.Assert.*;

public class TreapPopulationImplTest {
    @Test
    public void testTpiPointAddition() throws Exception {
        final TreapPopulationImpl tpi = new TreapPopulationImpl();
        tpi.addPoint(new Solution(new double[]{10, 10}));
        tpi.addPoint(new Solution(new double[]{5, 20}));
        tpi.addPoint(new Solution(new double[]{0, 30}));

        assertNotNull(tpi.getRanks());
        assertEquals(1, tpi.getRanks().size());

        Treap rank0 = tpi.getRanks().get(0);
        assertEquals(3, rank0.toList().size());

        tpi.addPoint(new Solution(new double[]{0, 0}));

        assertEquals(2, tpi.getRanks().size());

        rank0 = tpi.getRanks().get(0);
        assertEquals(1, rank0.toList().size());
        assertArrayEquals(new double[]{0, 0}, rank0.toList().get(0).getObjectives(), 0.0);

        Treap rank1 = tpi.getRanks().get(1);
        assertEquals(3, rank1.toList().size());
    }
}
