package rlmf.java.budgetcontrol.domain.service;

import rlmf.java.budgetcontrol.domain.InvalidOperationException;
import rlmf.java.budgetcontrol.domain.model.Category;
import rlmf.java.budgetcontrol.domain.model.CategoryTotal;
import rlmf.java.budgetcontrol.domain.model.Operation;
import rlmf.java.budgetcontrol.domain.model.OperationType;
import rlmf.java.budgetcontrol.domain.model.PeriodReport;
import rlmf.java.budgetcontrol.domain.model.YearlyReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReportRules {

    private ReportRules() {
    }

    public static LocalDate firstDayOfMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new InvalidOperationException("month must be between 1 and 12");
        }
        return LocalDate.of(year, month, 1);
    }

    public static LocalDate lastDayOfMonth(LocalDate firstDayOfMonth) {
        return firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
    }

    public static void validateCustomRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException("startDate must not be after endDate");
        }
        if (startDate.getYear() != endDate.getYear()) {
            throw new InvalidOperationException("A custom date range cannot cross a year boundary");
        }
    }

    public static int lastElapsedMonth(int year, LocalDate today) {
        if (year < today.getYear()) {
            return 12;
        }
        if (year == today.getYear()) {
            return today.getMonthValue();
        }
        return 0;
    }

    public static PeriodReport buildPeriodReport(LocalDate startDate, LocalDate endDate, List<Operation> operations) {
        Map<Long, List<Operation>> byCategory = new LinkedHashMap<>();
        for (Operation operation : operations) {
            byCategory.computeIfAbsent(operation.category().id(), id -> new ArrayList<>()).add(operation);
        }

        List<CategoryTotal> categories = byCategory.values().stream()
                .map(ReportRules::toCategoryTotal)
                .sorted(Comparator.comparing(categoryTotal -> categoryTotal.category().name()))
                .toList();

        BigDecimal totalIncome = sumByType(operations, OperationType.INCOME);
        BigDecimal totalPayments = sumByType(operations, OperationType.PAYMENT);
        BigDecimal balance = totalIncome.subtract(totalPayments);

        return new PeriodReport(startDate, endDate, totalIncome, totalPayments, balance, categories);
    }

    public static YearlyReport buildYearlyReport(int year, List<PeriodReport> months) {
        BigDecimal totalIncome = months.stream().map(PeriodReport::totalIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPayments = months.stream().map(PeriodReport::totalPayments).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = totalIncome.subtract(totalPayments);

        return new YearlyReport(year, totalIncome, totalPayments, balance, months);
    }

    private static CategoryTotal toCategoryTotal(List<Operation> categoryOperations) {
        Category category = categoryOperations.get(0).category();
        BigDecimal totalIncome = sumByType(categoryOperations, OperationType.INCOME);
        BigDecimal totalPayments = sumByType(categoryOperations, OperationType.PAYMENT);
        return new CategoryTotal(category, totalIncome, totalPayments, categoryOperations);
    }

    private static BigDecimal sumByType(List<Operation> operations, OperationType type) {
        return operations.stream()
                .filter(operation -> operation.type() == type)
                .map(Operation::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
