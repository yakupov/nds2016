package ru.itmo.nds.jmh.benchmarks;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import ru.itmo.nds.front_storage.FrontStorage;

import java.io.InputStream;
import java.util.Objects;

/**
 * Perform all the same tests as in {@link IncrementalPPSN_ZDT1_gs10000_it100_ds1}
 * but on another dataset
 */
public class IncrementalPPSN_ZDT6_gs10000_it100_ds1 extends IncrementalPPSN_ZDT1_gs10000_it100_ds1 {
    @Setup(Level.Trial)
    public void init() throws Exception {
        frontStorage = new FrontStorage();

        try (InputStream is = IncrementalPPSN_ZDT6_gs10000_it100_ds1.class
                .getResourceAsStream("ppsn/zdt6_gen10000_iter100_dataset1.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }
    }
}
