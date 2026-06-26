package com.mediacity.repository;

import com.mediacity.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findWaitingByWorkId(String workId);

    Optional<Reservation> findById(String id);

    List<Reservation> findAll();

    void clear();
}
