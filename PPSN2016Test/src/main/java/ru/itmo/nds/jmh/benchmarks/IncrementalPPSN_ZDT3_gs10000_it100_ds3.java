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
public class IncrementalPPSN_ZDT3_gs10000_it100_ds3 extends IncrementalPPSN_ZDT1_gs10000_it100_ds1 {
    @Setup(Level.Trial)
    public void init() throws Exception {
        frontStorage = new FrontStorage();

        try (InputStream is = IncrementalPPSN_ZDT3_gs10000_it100_ds3.class
                .getResourceAsStream("ppsn/zdt3_gen10000_iter100_dataset3.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }
    }
}
