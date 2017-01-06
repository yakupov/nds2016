package ru.itmo.nds.jmh.benchmarks;

import ru.itmo.nds.front_storage.FrontStorage;

import java.io.InputStream;
import java.util.Objects;

/**
 * Perform all the same tests as in {@link CommonPPSNBenchmarks}
 * but on another dataset
 */
public class IncrementalPPSN_ZDT2_gs10000_it100_ds1 extends CommonPPSNBenchmarks {
    @Override
    FrontStorage loadFrontsFromResources() throws Exception {
        final FrontStorage frontStorage = new FrontStorage();
        try (InputStream is = IncrementalPPSN_ZDT2_gs10000_it100_ds1.class
                .getResourceAsStream("ppsn/zdt2_gen10000_iter100_dataset1.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }
        return frontStorage;
    }
}
