package com.mediacity.repository;

import com.mediacity.model.Work;

import java.util.Optional;

public interface WorkRepository {

    Optional<Work> findById(String id);

    Work save(Work work);

    void clear();
}
