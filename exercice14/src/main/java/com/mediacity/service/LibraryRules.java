package com.mediacity.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class LibraryRules {

    public static final int LOAN_DURATION_DAYS = 21;
    public static final BigDecimal PENALTY_PER_DAY = new BigDecimal("0.15");
    public static final int SERIOUS_DELAY_DAYS = 7;
    public static final int MAX_SERIOUS_DELAYS_PER_YEAR = 3;

    private LibraryRules() {
    }

    public static BigDecimal calculatePenalty(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
        return PENALTY_PER_DAY.multiply(BigDecimal.valueOf(daysLate));
    }

    public static boolean isSeriousDelay(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return false;
        }
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate >= SERIOUS_DELAY_DAYS;
    }
}
