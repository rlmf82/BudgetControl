package rlmf.java.budgetcontrol.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rlmf.java.budgetcontrol.application.ports.in.ReportUseCase;
import rlmf.java.budgetcontrol.application.ports.out.OperationRepository;
import rlmf.java.budgetcontrol.domain.model.Operation;
import rlmf.java.budgetcontrol.domain.model.PeriodReport;
import rlmf.java.budgetcontrol.domain.model.YearlyReport;
import rlmf.java.budgetcontrol.domain.service.ReportRules;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService implements ReportUseCase {

    private final OperationRepository operationRepository;

    public ReportService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public PeriodReport monthlyReport(int year, int month) {
        LocalDate start = ReportRules.firstDayOfMonth(year, month);
        LocalDate end = ReportRules.lastDayOfMonth(start);
        return buildReport(start, end);
    }

    @Override
    public PeriodReport customRangeReport(LocalDate startDate, LocalDate endDate) {
        ReportRules.validateCustomRange(startDate, endDate);
        return buildReport(startDate, endDate);
    }

    @Override
    public YearlyReport yearlyReport(int year) {
        LocalDate today = LocalDate.now(OperationService.PORTUGAL_ZONE);
        int lastElapsedMonth = ReportRules.lastElapsedMonth(year, today);

        List<PeriodReport> months = new ArrayList<>();
        for (int month = 1; month <= lastElapsedMonth; month++) {
            months.add(monthlyReport(year, month));
        }

        return ReportRules.buildYearlyReport(year, months);
    }

    private PeriodReport buildReport(LocalDate startDate, LocalDate endDate) {
        List<Operation> operations = operationRepository.findByDateRange(startDate, endDate);
        return ReportRules.buildPeriodReport(startDate, endDate, operations);
    }
}
