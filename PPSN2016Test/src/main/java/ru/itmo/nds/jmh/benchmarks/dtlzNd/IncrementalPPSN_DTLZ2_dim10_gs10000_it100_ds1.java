package ru.itmo.nds.jmh.benchmarks.dtlzNd;

import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.jmh.benchmarks.AbstractDtlzZdtBenchmark;

import java.io.InputStream;
import java.util.Objects;

/**
 * Perform all the same tests as in {@link AbstractDtlzZdtBenchmark}
 * but on another dataset
 */
public class IncrementalPPSN_DTLZ2_dim10_gs10000_it100_ds1 extends AbstractDtlzZdtBenchmark {
    @Override
    protected FrontStorage loadFrontsFromResources() throws Exception {
        final FrontStorage frontStorage = new FrontStorage();
        try (InputStream is = IncrementalPPSN_DTLZ2_dim10_gs10000_it100_ds1.class
                .getResourceAsStream("dtlz2_dim10_gen10000_iter100_dataset1.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }
        return frontStorage;
    }
}
