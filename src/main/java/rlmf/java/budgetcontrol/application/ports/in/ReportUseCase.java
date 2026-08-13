package rlmf.java.budgetcontrol.application.ports.in;

import rlmf.java.budgetcontrol.domain.model.PeriodReport;
import rlmf.java.budgetcontrol.domain.model.YearlyReport;

import java.time.LocalDate;

public interface ReportUseCase {

    PeriodReport monthlyReport(int year, int month);

    PeriodReport customRangeReport(LocalDate startDate, LocalDate endDate);

    YearlyReport yearlyReport(int year);
}
