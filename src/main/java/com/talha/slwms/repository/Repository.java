package com.talha.slwms.repository;

import java.util.*;
import java.util.function.Function;

public class Repository<T> {
    private final Map<String, T> storage = new HashMap<>();
    private final Function<T, String> idExtractor;

    public Repository(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    public void save(T item) {
        storage.put(idExtractor.apply(item), item);
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }


}
