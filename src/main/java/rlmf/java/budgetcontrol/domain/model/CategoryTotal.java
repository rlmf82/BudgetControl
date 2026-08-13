package rlmf.java.budgetcontrol.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record CategoryTotal(Category category, BigDecimal totalIncome, BigDecimal totalPayments, List<Operation> operations) {
}
