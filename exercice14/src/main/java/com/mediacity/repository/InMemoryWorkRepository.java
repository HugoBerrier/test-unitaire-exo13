package com.mediacity.repository;

import com.mediacity.model.Work;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryWorkRepository implements WorkRepository {

    private final Map<String, Work> storage = new HashMap<>();

    @Override
    public Optional<Work> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Work save(Work work) {
        storage.put(work.getId(), work);
        return work;
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
