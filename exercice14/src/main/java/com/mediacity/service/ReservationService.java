package com.mediacity.service;

import com.mediacity.exception.MemberNotFoundException;
import com.mediacity.exception.MemberSuspendedException;
import com.mediacity.exception.WorkAvailableException;
import com.mediacity.exception.WorkNotFoundException;
import com.mediacity.model.Member;
import com.mediacity.model.Reservation;
import com.mediacity.model.ReservationStatus;
import com.mediacity.repository.LoanRepository;
import com.mediacity.repository.MemberRepository;
import com.mediacity.repository.ReservationRepository;
import com.mediacity.repository.WorkRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {

    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(MemberRepository memberRepository,
                              WorkRepository workRepository,
                              LoanRepository loanRepository,
                              ReservationRepository reservationRepository) {
        this.memberRepository = memberRepository;
        this.workRepository = workRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
    }

    public Reservation reserve(String memberId, String workId, LocalDateTime createdAt) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.isSuspended()) {
            throw new MemberSuspendedException(memberId);
        }

        workRepository.findById(workId)
                .orElseThrow(() -> new WorkNotFoundException(workId));

        if (loanRepository.findActiveByWorkId(workId).isEmpty()) {
            throw new WorkAvailableException(workId);
        }

        Reservation reservation = new Reservation();
        reservation.setMemberId(memberId);
        reservation.setWorkId(workId);
        reservation.setCreatedAt(createdAt);
        reservation.setStatus(ReservationStatus.WAITING);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getWaitingReservations(String workId) {
        return reservationRepository.findWaitingByWorkId(workId);
    }

    public void onWorkReturned(String workId) {
        List<Reservation> waiting = reservationRepository.findWaitingByWorkId(workId);
        if (!waiting.isEmpty()) {
            Reservation first = waiting.get(0);
            first.setStatus(ReservationStatus.FULFILLED);
            reservationRepository.save(first);
        }
    }
}
