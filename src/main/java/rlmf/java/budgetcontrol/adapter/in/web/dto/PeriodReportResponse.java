package rlmf.java.budgetcontrol.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import rlmf.java.budgetcontrol.domain.model.PeriodReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PeriodReportResponse(
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate startDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate endDate,

        BigDecimal totalIncome,
        BigDecimal totalPayments,
        BigDecimal balance,
        List<CategoryTotalResponse> categories
) {

    public static PeriodReportResponse from(PeriodReport report) {
        return new PeriodReportResponse(
                report.startDate(),
                report.endDate(),
                report.totalIncome(),
                report.totalPayments(),
                report.balance(),
                report.categories().stream().map(CategoryTotalResponse::from).toList()
        );
    }
}
