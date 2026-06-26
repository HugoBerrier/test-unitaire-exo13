package com.mediacity.service;

import com.mediacity.exception.MemberSuspendedException;
import com.mediacity.exception.WorkAvailableException;
import com.mediacity.model.Loan;
import com.mediacity.model.Member;
import com.mediacity.model.Reservation;
import com.mediacity.model.ReservationStatus;
import com.mediacity.model.Work;
import com.mediacity.repository.LoanRepository;
import com.mediacity.repository.MemberRepository;
import com.mediacity.repository.ReservationRepository;
import com.mediacity.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 3, 1, 10, 0);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WorkRepository workRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldReserveUnavailableWork() {
        // Arrange
        when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice")));
        when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "1984")));
        when(loanRepository.findActiveByWorkId("W1")).thenReturn(Optional.of(new Loan()));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId("R1");
            return reservation;
        });

        // Act
        Reservation reservation = reservationService.reserve("M1", "W1", NOW);

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
        assertThat(reservation.getMemberId()).isEqualTo("M1");
    }

    @Test
    void shouldRejectReservationWhenWorkIsAvailable() {
        // Arrange
        when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice")));
        when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "1984")));
        when(loanRepository.findActiveByWorkId("W1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserve("M1", "W1", NOW))
                .isInstanceOf(WorkAvailableException.class);
    }

    @Test
    void shouldRejectReservationWhenMemberIsSuspended() {
        // Arrange
        Member suspended = new Member("M1", "Alice");
        suspended.setSuspended(true);
        when(memberRepository.findById("M1")).thenReturn(Optional.of(suspended));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserve("M1", "W1", NOW))
                .isInstanceOf(MemberSuspendedException.class);
    }

    @Test
    void shouldFulfillFirstWaitingReservationOnReturn() {
        // Arrange
        Reservation first = waitingReservation("R1", "M2");
        when(reservationRepository.findWaitingByWorkId("W1")).thenReturn(List.of(first));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        reservationService.onWorkReturned("W1");

        // Assert
        assertThat(first.getStatus()).isEqualTo(ReservationStatus.FULFILLED);
        verify(reservationRepository).save(first);
    }

    private Reservation waitingReservation(String id, String memberId) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setWorkId("W1");
        reservation.setMemberId(memberId);
        reservation.setCreatedAt(NOW);
        reservation.setStatus(ReservationStatus.WAITING);
        return reservation;
    }
}
