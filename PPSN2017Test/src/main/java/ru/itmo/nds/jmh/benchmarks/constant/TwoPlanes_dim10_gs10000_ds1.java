package ru.itmo.nds.jmh.benchmarks.constant;

import ru.itmo.nds.front_storage.FrontStorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class TwoPlanes_dim10_gs10000_ds1 extends AbstractConstant3GenBenchmark {
    @Override
    protected FrontStorage loadFrontsFromResources() throws IOException {
        final FrontStorage frontStorage = new FrontStorage();
        try (InputStream is = TwoPlanes_dim10_gs10000_ds1.class
                .getResourceAsStream("twoLayers_dim10_gen10000.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }

        return frontStorage;
    }
}
