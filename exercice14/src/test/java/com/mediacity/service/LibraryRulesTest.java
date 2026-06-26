package com.mediacity.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LibraryRulesTest {

    @Test
    void shouldCalculatePenaltyPerLateDay() {
        LocalDate dueDate = LocalDate.of(2026, 1, 31);
        LocalDate returnDate = LocalDate.of(2026, 2, 5);

        assertThat(LibraryRules.calculatePenalty(dueDate, returnDate))
                .isEqualByComparingTo(new BigDecimal("0.75"));
    }

    @Test
    void shouldDetectSeriousDelayFromSevenDaysLate() {
        LocalDate dueDate = LocalDate.of(2026, 1, 31);
        LocalDate returnDate = LocalDate.of(2026, 2, 7);

        assertThat(LibraryRules.isSeriousDelay(dueDate, returnDate)).isTrue();
    }
}
