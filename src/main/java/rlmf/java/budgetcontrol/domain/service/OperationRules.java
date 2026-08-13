package rlmf.java.budgetcontrol.domain.service;

import rlmf.java.budgetcontrol.domain.InvalidOperationException;
import rlmf.java.budgetcontrol.domain.model.Category;
import rlmf.java.budgetcontrol.domain.model.Operation;
import rlmf.java.budgetcontrol.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class OperationRules {

    private OperationRules() {
    }

    public static Operation createNew(OperationType type, BigDecimal amount, String description, Category category,
                                       LocalDate requestedDate, LocalDate today) {
        validateAmount(amount);
        return new Operation(null, type, amount, description, category, resolveDate(requestedDate, today));
    }

    public static Operation update(Operation existing, OperationType type, BigDecimal amount, String description, Category category,
                                    LocalDate requestedDate, LocalDate today) {
        validateAmount(amount);
        return new Operation(existing.id(), type, amount, description, category, resolveDate(requestedDate, today));
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidOperationException("amount must be greater than zero");
        }
    }

    private static LocalDate resolveDate(LocalDate requestedDate, LocalDate today) {
        LocalDate date = requestedDate != null ? requestedDate : today;
        if (date.isAfter(today)) {
            throw new InvalidOperationException("Operation date cannot be in the future");
        }
        return date;
    }
}
