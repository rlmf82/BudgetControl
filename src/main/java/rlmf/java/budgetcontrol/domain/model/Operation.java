package rlmf.java.budgetcontrol.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Operation(Long id, OperationType type, BigDecimal amount, String description, Category category, LocalDate operationDate) {
}
