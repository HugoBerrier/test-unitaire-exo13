package com.mediacity.repository;

import java.util.HashMap;
import java.util.Map;

public class InMemorySeriousDelayRepository implements SeriousDelayRepository {

    private final Map<String, Integer> storage = new HashMap<>();

    @Override
    public int countForMemberAndYear(String memberId, int year) {
        return storage.getOrDefault(key(memberId, year), 0);
    }

    @Override
    public void recordSeriousDelay(String memberId, int year) {
        String key = key(memberId, year);
        storage.put(key, storage.getOrDefault(key, 0) + 1);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    private String key(String memberId, int year) {
        return memberId + "-" + year;
    }
}
