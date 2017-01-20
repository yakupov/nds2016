package ru.itmo.nds.jmh.benchmarks;

import org.junit.Test;

/**
 * UT fo ENLU
 */
public class EnluTest {
    @Test
    public void testDtlz7Ds2() throws Exception {
        final IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2 testClass = new IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2();
        testClass.prepareTestData();

        testClass.sortUsingEnlu(0, true);
//        testClass.sortUsingEnlu(10, true);
//        testClass.sortUsingEnlu(20, true);
//        testClass.sortUsingEnlu(30, true);
//        testClass.sortUsingEnlu(40, true);
        testClass.sortUsingEnlu(50, true);
//        testClass.sortUsingEnlu(60, true);
//        testClass.sortUsingEnlu(70, true);
//        testClass.sortUsingEnlu(80, true);
        testClass.sortUsingEnlu(90, true);
    }
}
