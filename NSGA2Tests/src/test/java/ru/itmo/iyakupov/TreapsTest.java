package ru.itmo.iyakupov;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Solution;
import ru.itmo.iyakupov.ss.treap2015.Treap;
import ru.itmo.iyakupov.ss.treap2015.TreapPopulationImpl;

public class TreapsTest {
    @Test
    @Ignore
    public void test() throws Exception {
        final TreapPopulationImpl tpi = new TreapPopulationImpl();
        tpi.addPoint(new Solution(new double[]{10, 10}));
        tpi.addPoint(new Solution(new double[]{5, 20}));
        tpi.addPoint(new Solution(new double[]{0, 30}));

        final Treap rank0 = tpi.getRanks().get(0);

        System.out.println(rank0);

        System.out.println(rank0.getOrdered(0));
        System.out.println(rank0.getOrdered(1));
        System.out.println(rank0.getOrdered(2));
        //System.out.println(rank0.getOrdered(3));

        final Treap rank0_1 = rank0.remove(new Solution(new double[]{5, 20}));
        rank0_1.verify();
        System.out.println(rank0_1);
    }
}
