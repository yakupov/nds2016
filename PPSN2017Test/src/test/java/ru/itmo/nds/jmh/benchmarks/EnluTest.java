package ru.itmo.nds.jmh.benchmarks;

import org.junit.Test;
import ru.itmo.nds.jmh.benchmarks.dtlz3d.IncrementalPPSN_DTLZ7_dim3_gs10000_it10000_ds2;

/**
 * UT fo ENLU
 */
public class EnluTest {
    @Test
    public void testDtlz7Ds2() throws Exception {
        final AbstractDtlzZdtBenchmark testClass = new IncrementalPPSN_DTLZ7_dim3_gs10000_it10000_ds2();
        testClass.prepareTestData();

        testClass.sortUsingEnlu(0, true);
//        testClass.sortUsingEnlu(10, true);
//        testClass.sortUsingEnlu(20, true);
//        testClass.sortUsingEnlu(30, true);
//        testClass.sortUsingEnlu(40, true);
        testClass.sortUsingEnlu(5000, true);
//        testClass.sortUsingEnlu(60, true);
//        testClass.sortUsingEnlu(70, true);
//        testClass.sortUsingEnlu(80, true);
        testClass.sortUsingEnlu(9000, true);
    }
}
