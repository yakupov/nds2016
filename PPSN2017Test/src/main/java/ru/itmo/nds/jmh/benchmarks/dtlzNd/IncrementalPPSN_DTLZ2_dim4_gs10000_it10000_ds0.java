package ru.itmo.nds.jmh.benchmarks.dtlzNd;

import ru.itmo.nds.front_storage.FrontStorage;
import ru.itmo.nds.jmh.benchmarks.AbstractDtlzZdtBenchmark;

import java.io.InputStream;
import java.util.Objects;

/**
 * Perform all the same tests as in {@link AbstractDtlzZdtBenchmark}
 * but on another dataset
 */
public class IncrementalPPSN_DTLZ2_dim4_gs10000_it10000_ds0 extends AbstractDtlzZdtBenchmark {
    @Override
    protected FrontStorage loadFrontsFromResources() throws Exception {
        final FrontStorage frontStorage = new FrontStorage();
        try (InputStream is = IncrementalPPSN_DTLZ2_dim4_gs10000_it10000_ds0.class
                .getResourceAsStream("dtlz2_dim4_gen10000_iter10000_dataset0.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }
        return frontStorage;
    }
}
