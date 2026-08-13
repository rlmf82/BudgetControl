package rlmf.java.budgetcontrol.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PeriodReport(LocalDate startDate, LocalDate endDate, BigDecimal totalIncome, BigDecimal totalPayments,
                            BigDecimal balance, List<CategoryTotal> categories) {
}
