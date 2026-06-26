package com.mediacity.bdd;

import com.mediacity.exception.MemberSuspendedException;
import com.mediacity.exception.WorkUnavailableException;
import com.mediacity.model.Member;
import com.mediacity.model.ReservationStatus;
import com.mediacity.model.Work;
import com.mediacity.service.LibraryContext;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationStepDefinitions {

    private LibraryContext context;
    private Exception lastError;

    @Before
    public void reset() {
        context = new LibraryContext();
        lastError = null;
    }

    @Etantdonné("l'adhérent {string} avec l'identifiant {string}")
    public void adherentAvecIdentifiant(String name, String id) {
        context.getMemberRepository().save(new Member(id, name));
    }

    @Etantdonné("l'ouvrage {string} avec l'identifiant {string}")
    public void ouvrageAvecIdentifiant(String title, String id) {
        context.getWorkRepository().save(new Work(id, title));
    }

    @Etantdonné("l'ouvrage {string} est emprunté par {string} depuis le {string}")
    public void ouvrageEmprunte(String workId, String memberId, String loanDate) {
        context.getLoanService().createLoan(memberId, workId, LocalDate.parse(loanDate));
    }

    @Etantdonné("l'adhérent {string} nommé {string} est suspendu")
    public void adherentSuspendu(String memberId, String name) {
        Member member = new Member(memberId, name);
        member.setSuspended(true);
        context.getMemberRepository().save(member);
    }

    @Quand("l'adhérent {string} nommé {string} réserve l'ouvrage {string} le {string}")
    public void reserverOuvrage(String memberId, String name, String workId, String dateTime) {
        context.getMemberRepository().save(new Member(memberId, name));
        context.getReservationService().reserve(memberId, workId, LocalDateTime.parse(dateTime));
    }

    @Quand("l'adhérent suspendu {string} tente de réserver l'ouvrage {string} le {string}")
    public void tentativeReservationSuspendu(String memberId, String workId, String dateTime) {
        try {
            context.getReservationService().reserve(memberId, workId, LocalDateTime.parse(dateTime));
        } catch (Exception ex) {
            lastError = ex;
        }
    }

    @Quand("l'ouvrage {string} est restitué le {string}")
    public void restituerOuvrage(String workId, String returnDate) {
        String loanId = context.getLoanRepository().findActiveByWorkId(workId)
                .orElseThrow()
                .getId();
        context.getLoanService().returnLoan(loanId, LocalDate.parse(returnDate));
    }

    @Quand("l'adhérent {string} tente d'emprunter l'ouvrage {string} le {string}")
    public void tentativeEmprunt(String memberId, String workId, String loanDate) {
        try {
            context.getLoanService().createLoan(memberId, workId, LocalDate.parse(loanDate));
        } catch (Exception ex) {
            lastError = ex;
        }
    }

    @Alors("la réservation est en attente pour l'ouvrage {string}")
    public void reservationEnAttente(String workId) {
        assertThat(context.getReservationService().getWaitingReservations(workId)).hasSize(1);
    }

    @Alors("il y a {int} réservations en attente pour l'ouvrage {string}")
    public void nombreReservationsEnAttente(int count, String workId) {
        assertThat(context.getReservationService().getWaitingReservations(workId)).hasSize(count);
    }

    @Alors("la première réservation pour l'ouvrage {string} est honorée")
    public void premiereReservationHonoree(String workId) {
        assertThat(context.getReservationRepository().findAll())
                .anyMatch(reservation -> workId.equals(reservation.getWorkId())
                        && reservation.getStatus() == ReservationStatus.FULFILLED);
    }

    @Alors("la réservation est refusée pour suspension")
    public void reservationRefuseePourSuspension() {
        assertThat(lastError).isInstanceOf(MemberSuspendedException.class);
    }

    @Alors("l'emprunt est refusé car l'ouvrage est indisponible")
    public void empruntRefuse() {
        assertThat(lastError).isInstanceOf(WorkUnavailableException.class);
    }
}
