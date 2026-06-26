package com.mediacity.service;

import com.mediacity.exception.LoanAlreadyReturnedException;
import com.mediacity.exception.LoanNotFoundException;
import com.mediacity.exception.MemberNotFoundException;
import com.mediacity.exception.MemberSuspendedException;
import com.mediacity.exception.WorkNotFoundException;
import com.mediacity.exception.WorkUnavailableException;
import com.mediacity.model.Loan;
import com.mediacity.model.Member;
import com.mediacity.repository.LoanRepository;
import com.mediacity.repository.MemberRepository;
import com.mediacity.repository.SeriousDelayRepository;
import com.mediacity.repository.WorkRepository;

import java.time.LocalDate;

public class LoanService {

    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;
    private final LoanRepository loanRepository;
    private final SeriousDelayRepository seriousDelayRepository;
    private final ReservationService reservationService;

    public LoanService(MemberRepository memberRepository,
                       WorkRepository workRepository,
                       LoanRepository loanRepository,
                       SeriousDelayRepository seriousDelayRepository,
                       ReservationService reservationService) {
        this.memberRepository = memberRepository;
        this.workRepository = workRepository;
        this.loanRepository = loanRepository;
        this.seriousDelayRepository = seriousDelayRepository;
        this.reservationService = reservationService;
    }

    public Loan createLoan(String memberId, String workId, LocalDate loanDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.isSuspended()) {
            throw new MemberSuspendedException(memberId);
        }

        workRepository.findById(workId)
                .orElseThrow(() -> new WorkNotFoundException(workId));

        if (loanRepository.findActiveByWorkId(workId).isPresent()) {
            throw new WorkUnavailableException(workId);
        }

        Loan loan = new Loan();
        loan.setMemberId(memberId);
        loan.setWorkId(workId);
        loan.setLoanDate(loanDate);
        loan.setDueDate(loanDate.plusDays(LibraryRules.LOAN_DURATION_DAYS));
        return loanRepository.save(loan);
    }

    public Loan returnLoan(String loanId, LocalDate returnDate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        if (!loan.isActive()) {
            throw new LoanAlreadyReturnedException(loanId);
        }

        loan.setReturnDate(returnDate);
        loan.setPenalty(LibraryRules.calculatePenalty(loan.getDueDate(), returnDate));

        if (LibraryRules.isSeriousDelay(loan.getDueDate(), returnDate)) {
            int year = returnDate.getYear();
            seriousDelayRepository.recordSeriousDelay(loan.getMemberId(), year);
            int count = seriousDelayRepository.countForMemberAndYear(loan.getMemberId(), year);
            if (count >= LibraryRules.MAX_SERIOUS_DELAYS_PER_YEAR) {
                Member member = memberRepository.findById(loan.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException(loan.getMemberId()));
                member.setSuspended(true);
                memberRepository.save(member);
            }
        }

        loanRepository.save(loan);
        reservationService.onWorkReturned(loan.getWorkId());
        return loan;
    }
}
