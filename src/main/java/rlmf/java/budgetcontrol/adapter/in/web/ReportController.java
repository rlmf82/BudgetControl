package rlmf.java.budgetcontrol.adapter.in.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rlmf.java.budgetcontrol.adapter.in.web.dto.PeriodReportResponse;
import rlmf.java.budgetcontrol.adapter.in.web.dto.YearlyReportResponse;
import rlmf.java.budgetcontrol.application.ports.in.ReportUseCase;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportUseCase reportService;

    public ReportController(ReportUseCase reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public PeriodReportResponse monthly(@RequestParam int year, @RequestParam int month) {
        return PeriodReportResponse.from(reportService.monthlyReport(year, month));
    }

    @GetMapping("/custom")
    public PeriodReportResponse custom(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
        return PeriodReportResponse.from(reportService.customRangeReport(startDate, endDate));
    }

    @GetMapping("/yearly")
    public YearlyReportResponse yearly(@RequestParam int year) {
        return YearlyReportResponse.from(reportService.yearlyReport(year));
    }
}
