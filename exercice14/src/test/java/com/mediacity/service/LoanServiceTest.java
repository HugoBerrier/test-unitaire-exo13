package com.mediacity.service;

import com.mediacity.exception.MemberSuspendedException;
import com.mediacity.exception.WorkUnavailableException;
import com.mediacity.model.Loan;
import com.mediacity.model.Member;
import com.mediacity.model.Work;
import com.mediacity.repository.LoanRepository;
import com.mediacity.repository.MemberRepository;
import com.mediacity.repository.SeriousDelayRepository;
import com.mediacity.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    private static final LocalDate LOAN_DATE = LocalDate.of(2026, 1, 10);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WorkRepository workRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private SeriousDelayRepository seriousDelayRepository;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private LoanService loanService;

    @Test
    void shouldCreateLoanWithDueDateIn21Days() {
        // Arrange
        when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice")));
        when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "1984")));
        when(loanRepository.findActiveByWorkId("W1")).thenReturn(Optional.empty());
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId("L1");
            return loan;
        });

        // Act
        Loan loan = loanService.createLoan("M1", "W1", LOAN_DATE);

        // Assert
        assertThat(loan.getMemberId()).isEqualTo("M1");
        assertThat(loan.getWorkId()).isEqualTo("W1");
        assertThat(loan.getDueDate()).isEqualTo(LOAN_DATE.plusDays(21));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldRejectLoanWhenWorkIsAlreadyBorrowed() {
        // Arrange
        when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice")));
        when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "1984")));
        when(loanRepository.findActiveByWorkId("W1")).thenReturn(Optional.of(new Loan()));

        // Act & Assert
        assertThatThrownBy(() -> loanService.createLoan("M1", "W1", LOAN_DATE))
                .isInstanceOf(WorkUnavailableException.class);
    }

    @Test
    void shouldRejectLoanWhenMemberIsSuspended() {
        // Arrange
        Member suspended = new Member("M1", "Alice");
        suspended.setSuspended(true);
        when(memberRepository.findById("M1")).thenReturn(Optional.of(suspended));

        // Act & Assert
        assertThatThrownBy(() -> loanService.createLoan("M1", "W1", LOAN_DATE))
                .isInstanceOf(MemberSuspendedException.class);
    }

    @Test
    void shouldCalculatePenaltyForLateReturn() {
        // Arrange
        Loan loan = activeLoan();
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));

        // Act
        Loan returned = loanService.returnLoan("L1", LOAN_DATE.plusDays(25));

        // Assert
        assertThat(returned.getPenalty()).isEqualByComparingTo(new BigDecimal("0.60"));
    }

    @Test
    void shouldNotApplyPenaltyWhenReturnedOnTime() {
        // Arrange
        Loan loan = activeLoan();
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));

        // Act
        Loan returned = loanService.returnLoan("L1", loan.getDueDate());

        // Assert
        assertThat(returned.getPenalty()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(seriousDelayRepository, never()).recordSeriousDelay(any(), any(Integer.class));
    }

    @Test
    void shouldRecordSeriousDelayWhenReturnIsVeryLate() {
        // Arrange
        Loan loan = activeLoan();
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));
        when(seriousDelayRepository.countForMemberAndYear("M1", 2026)).thenReturn(1);

        // Act
        loanService.returnLoan("L1", LOAN_DATE.plusDays(30));

        // Assert
        verify(seriousDelayRepository).recordSeriousDelay("M1", 2026);
    }

    @Test
    void shouldSuspendMemberAfterThreeSeriousDelaysInSameYear() {
        // Arrange
        Loan loan = activeLoan();
        Member member = new Member("M1", "Alice");
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));
        when(seriousDelayRepository.countForMemberAndYear("M1", 2026)).thenReturn(3);
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));

        // Act
        loanService.returnLoan("L1", LOAN_DATE.plusDays(30));

        // Assert
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        assertThat(captor.getValue().isSuspended()).isTrue();
    }

    @Test
    void shouldNotifyReservationServiceWhenWorkIsReturned() {
        // Arrange
        Loan loan = activeLoan();
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));

        // Act
        loanService.returnLoan("L1", loan.getDueDate());

        // Assert
        verify(reservationService).onWorkReturned("W1");
    }

    private Loan activeLoan() {
        Loan loan = new Loan();
        loan.setId("L1");
        loan.setMemberId("M1");
        loan.setWorkId("W1");
        loan.setLoanDate(LOAN_DATE);
        loan.setDueDate(LOAN_DATE.plusDays(21));
        return loan;
    }
}
