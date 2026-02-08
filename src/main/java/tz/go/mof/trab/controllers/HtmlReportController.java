package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.report.HtmlReportResponseDto;
import tz.go.mof.trab.dto.report.ReportFilterDto;
import tz.go.mof.trab.service.HtmlReportService;
import tz.go.mof.trab.utils.Response;

@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/reports")
public class HtmlReportController {

    @Autowired
    private HtmlReportService htmlReportService;

    @PostMapping("/appeals")
    @ResponseBody
    public Response<HtmlReportResponseDto> getAppealReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateAppealReport(filter);
    }

    @PostMapping("/appeals-by-region")
    @ResponseBody
    public Response<HtmlReportResponseDto> getAppealsByRegionReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateAppealsByRegionReport(filter);
    }

    @PostMapping("/judge-workload")
    @ResponseBody
    public Response<HtmlReportResponseDto> getJudgeWorkloadReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateJudgeWorkloadReport(filter);
    }

    @PostMapping("/case-status-summary")
    @ResponseBody
    public Response<HtmlReportResponseDto> getCaseStatusSummaryReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateCaseStatusSummaryReport(filter);
    }

    @PostMapping("/tax-type-analysis")
    @ResponseBody
    public Response<HtmlReportResponseDto> getTaxTypeAnalysisReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateTaxTypeAnalysisReport(filter);
    }

    @PostMapping("/overdue-cases")
    @ResponseBody
    public Response<HtmlReportResponseDto> getOverdueCasesReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateOverdueCasesReport(filter);
    }

    @PostMapping("/payments")
    @ResponseBody
    public Response<HtmlReportResponseDto> getPaymentReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generatePaymentReport(filter);
    }

    @PostMapping("/outstanding-bills")
    @ResponseBody
    public Response<HtmlReportResponseDto> getOutstandingBillsReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateOutstandingBillsReport(filter);
    }

    @PostMapping("/revenue-summary")
    @ResponseBody
    public Response<HtmlReportResponseDto> getRevenueSummaryReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateRevenueSummaryReport(filter);
    }

    @PostMapping("/bill-reconciliation")
    @ResponseBody
    public Response<HtmlReportResponseDto> getBillReconciliationReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateBillReconciliationReport(filter);
    }

    @PostMapping("/notices")
    @ResponseBody
    public Response<HtmlReportResponseDto> getNoticeReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateNoticeReport(filter);
    }

    @PostMapping("/summons")
    @ResponseBody
    public Response<HtmlReportResponseDto> getSummonsReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateSummonsReport(filter);
    }

    @PostMapping("/applications")
    @ResponseBody
    public Response<HtmlReportResponseDto> getApplicationReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateApplicationReport(filter);
    }

    @PostMapping("/financial-year-comparison")
    @ResponseBody
    public Response<HtmlReportResponseDto> getFinancialYearComparisonReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateFinancialYearComparisonReport(filter);
    }

    @PostMapping("/top-appellants")
    @ResponseBody
    public Response<HtmlReportResponseDto> getTopAppellantsReport(@RequestBody ReportFilterDto filter) {
        return htmlReportService.generateTopAppellantsReport(filter);
    }
}
