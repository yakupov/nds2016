package ru.itmo.nds.jmh.benchmarks.constant;

import ru.itmo.nds.front_storage.FrontStorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Hypercube_dim20_gs10000_ds1 extends AbstractConstant3GenBenchmark {
    @Override
    protected FrontStorage loadFrontsFromResources() throws IOException {
        final FrontStorage frontStorage = new FrontStorage();
        try (InputStream is = Hypercube_dim20_gs10000_ds1.class
                .getResourceAsStream("uniform_dim20_gen10000.json")) {
            Objects.requireNonNull(is, "Test data not found");
            frontStorage.deserialize(is);
        }

        return frontStorage;
    }
}
