package ru.itmo.nds.front_storage;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Instance of an experiment
 */
public class RunConfiguration {
    private LocalDateTime timestamp;
    private int numberOfIterations;
    private int sizeOfGeneration;
    private Integer percentOfGenerationsToStore;
    private Collection<DoublesGeneration> generations;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public int getSizeOfGeneration() {
        return sizeOfGeneration;
    }

    public void setSizeOfGeneration(int sizeOfGeneration) {
        this.sizeOfGeneration = sizeOfGeneration;
    }

    public Integer getPercentOfGenerationsToStore() {
        return percentOfGenerationsToStore;
    }

    public void setPercentOfGenerationsToStore(Integer percentOfGenerationsToStore) {
        this.percentOfGenerationsToStore = percentOfGenerationsToStore;
    }

    public Collection<DoublesGeneration> getGenerations() {
        return generations;
    }

    public void setGenerations(Collection<DoublesGeneration> generations) {
        this.generations = generations;
    }
}
