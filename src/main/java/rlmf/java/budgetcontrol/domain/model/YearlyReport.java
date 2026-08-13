package rlmf.java.budgetcontrol.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record YearlyReport(int year, BigDecimal totalIncome, BigDecimal totalPayments, BigDecimal balance, List<PeriodReport> months) {
}
