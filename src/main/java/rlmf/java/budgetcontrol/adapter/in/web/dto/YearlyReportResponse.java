package rlmf.java.budgetcontrol.adapter.in.web.dto;

import rlmf.java.budgetcontrol.domain.model.YearlyReport;

import java.math.BigDecimal;
import java.util.List;

public record YearlyReportResponse(
        int year,
        BigDecimal totalIncome,
        BigDecimal totalPayments,
        BigDecimal balance,
        List<PeriodReportResponse> months
) {

    public static YearlyReportResponse from(YearlyReport report) {
        return new YearlyReportResponse(
                report.year(),
                report.totalIncome(),
                report.totalPayments(),
                report.balance(),
                report.months().stream().map(PeriodReportResponse::from).toList()
        );
    }
}
