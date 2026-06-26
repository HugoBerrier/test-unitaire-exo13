package com.mediacity.repository;

import com.mediacity.model.Reservation;
import com.mediacity.model.ReservationStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<String, Reservation> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(String.valueOf(idGenerator.getAndIncrement()));
        }
        storage.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public List<Reservation> findWaitingByWorkId(String workId) {
        return storage.values().stream()
                .filter(reservation -> workId.equals(reservation.getWorkId()))
                .filter(reservation -> reservation.getStatus() == ReservationStatus.WAITING)
                .sorted(Comparator.comparing(Reservation::getCreatedAt))
                .toList();
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
