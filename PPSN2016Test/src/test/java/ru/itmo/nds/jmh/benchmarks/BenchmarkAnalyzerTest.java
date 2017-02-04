package ru.itmo.nds.jmh.benchmarks;

import org.junit.Ignore;
import org.junit.Test;
import ru.itmo.nds.jmh.benchmarks.dtlz3d.IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2;
import ru.itmo.nds.jmh.benchmarks.dtlzNd.IncrementalPPSN_DTLZ2_dim10_gs10000_it100_ds1;

/**
 * Playground for obtaining debug data from benchmarks on a single run.
 */
public class BenchmarkAnalyzerTest {
    @Test
    @Ignore
    public void obtainStatisticsLevelPpsnDtlz7_1() throws Exception {
        final AbstractDtlzZdtBenchmark testClass = new IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2();
        testClass.prepareTestData();

        System.out.println(testClass.sortUsingLevelPPSN(0, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(40, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(50, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(60, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(70, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(80, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(90, true));
        System.out.println();
    }

    @Test
    //@Ignore
    public void obtainStatisticsLevelPpsnDtlz2_dim10() throws Exception {
        final AbstractDtlzZdtBenchmark testClass = new IncrementalPPSN_DTLZ2_dim10_gs10000_it100_ds1();
        testClass.prepareTestData();

        System.out.println(testClass.sortUsingLevelPPSN(0, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(30, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(60, true));
        System.out.println();
        System.out.println(testClass.sortUsingLevelPPSN(90, true));
        System.out.println();
    }


    @Test
    @Ignore
    public void spinLevelPpsnDtlz7_1() throws Exception {
        final AbstractDtlzZdtBenchmark testClass = new IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2();
        testClass.prepareTestData();

        for (int i = 0; i < 100000; ++i) {
            if (testClass.sortUsingLevelPPSN(10, false) == 42) {
                break;
            }
        }
    }

    @Test
    @Ignore
    public void spinLevelPpsnDtlz7_2() throws Exception {
        final AbstractDtlzZdtBenchmark testClass = new IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2();
        testClass.prepareTestData();

        for (int i = 0; i < 100000; ++i) {
            if (testClass.levelPpsnGen60() == 42) {
                break;
            }
        }
    }

    @Test
    @Ignore
    public void spinIncPpsnDtlz7_2() throws Exception {
        final AbstractDtlzZdtBenchmark testClass = new IncrementalPPSN_DTLZ7_dim3_gs10000_it100_ds2();
        testClass.prepareTestData();

        for (int i = 0; i < 100000; ++i) {
            if (testClass.incPpsnGen60() == 42) {
                break;
            }
        }
    }
}
