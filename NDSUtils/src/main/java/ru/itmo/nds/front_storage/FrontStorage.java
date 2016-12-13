package ru.itmo.nds.front_storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Serializer/deserializer of run configurations
 */
public class FrontStorage {
    private final Type serializableType = new TypeToken<Collection<RunConfiguration>>(){}.getType();

    private Collection<RunConfiguration> runConfigurations;

    public Collection<RunConfiguration> getRunConfigurations() {
        return runConfigurations;
    }

    public void setRunConfigurations(Collection<RunConfiguration> runConfigurations) {
        this.runConfigurations = runConfigurations;
    }

    public void serialize(OutputStream os) throws IOException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os), 1000);
        final JsonWriter jsonWriter = new JsonWriter(bufferedWriter);
        jsonWriter.setIndent("  ");
        gson.toJson(runConfigurations, serializableType, jsonWriter);
        bufferedWriter.flush();
    }

    public void deserialize(InputStream is) throws IOException {
        final Gson gson = new Gson();
        runConfigurations = gson.fromJson(new BufferedReader(new InputStreamReader(is)), serializableType);
    }
}
