package rlmf.java.budgetcontrol.application;

import rlmf.java.budgetcontrol.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OperationCommand(OperationType type, BigDecimal amount, String description, Long categoryId, LocalDate date) {
}
