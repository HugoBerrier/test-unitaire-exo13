package com.mediacity.service;

import com.mediacity.repository.InMemoryLoanRepository;
import com.mediacity.repository.InMemoryMemberRepository;
import com.mediacity.repository.InMemoryReservationRepository;
import com.mediacity.repository.InMemorySeriousDelayRepository;
import com.mediacity.repository.InMemoryWorkRepository;
import com.mediacity.repository.LoanRepository;
import com.mediacity.repository.MemberRepository;
import com.mediacity.repository.ReservationRepository;
import com.mediacity.repository.SeriousDelayRepository;
import com.mediacity.repository.WorkRepository;

public class LibraryContext {

    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final SeriousDelayRepository seriousDelayRepository;
    private final ReservationService reservationService;
    private final LoanService loanService;

    public LibraryContext() {
        this.memberRepository = new InMemoryMemberRepository();
        this.workRepository = new InMemoryWorkRepository();
        this.loanRepository = new InMemoryLoanRepository();
        this.reservationRepository = new InMemoryReservationRepository();
        this.seriousDelayRepository = new InMemorySeriousDelayRepository();
        this.reservationService = new ReservationService(
                memberRepository, workRepository, loanRepository, reservationRepository);
        this.loanService = new LoanService(
                memberRepository, workRepository, loanRepository, seriousDelayRepository, reservationService);
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

    public WorkRepository getWorkRepository() {
        return workRepository;
    }

    public LoanRepository getLoanRepository() {
        return loanRepository;
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public SeriousDelayRepository getSeriousDelayRepository() {
        return seriousDelayRepository;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    public LoanService getLoanService() {
        return loanService;
    }

    public void clear() {
        memberRepository.clear();
        workRepository.clear();
        loanRepository.clear();
        reservationRepository.clear();
        seriousDelayRepository.clear();
    }
}
