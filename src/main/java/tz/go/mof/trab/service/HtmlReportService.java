package tz.go.mof.trab.service;

import tz.go.mof.trab.dto.report.HtmlReportResponseDto;
import tz.go.mof.trab.dto.report.ReportFilterDto;
import tz.go.mof.trab.utils.Response;

public interface HtmlReportService {
    Response<HtmlReportResponseDto> generateAppealReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateAppealsByRegionReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateJudgeWorkloadReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateCaseStatusSummaryReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateTaxTypeAnalysisReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateOverdueCasesReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generatePaymentReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateOutstandingBillsReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateRevenueSummaryReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateBillReconciliationReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateNoticeReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateSummonsReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateApplicationReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateFinancialYearComparisonReport(ReportFilterDto filter);
    Response<HtmlReportResponseDto> generateTopAppellantsReport(ReportFilterDto filter);
}
