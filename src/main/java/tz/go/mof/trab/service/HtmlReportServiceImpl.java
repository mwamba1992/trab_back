package tz.go.mof.trab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import tz.go.mof.trab.dto.report.*;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.AppealsRepository;
import tz.go.mof.trab.repositories.ApplicationRegisterRepository;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.repositories.NoticeRepository;
import tz.go.mof.trab.repositories.PaymentRepository;
import tz.go.mof.trab.repositories.RegionRepository;
import tz.go.mof.trab.repositories.SummonsRepository;
import tz.go.mof.trab.utils.ExcelFileCreator;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HtmlReportServiceImpl implements HtmlReportService {

    private static final Logger logger = LoggerFactory.getLogger(HtmlReportServiceImpl.class);

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private String logoBase64;

    @PostConstruct
    public void init() {
        try {
            InputStream is = getClass().getResourceAsStream("/static/images/coat-of-arms.png");
            if (is != null) {
                byte[] imageBytes = org.apache.commons.io.IOUtils.toByteArray(is);
                logoBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
                is.close();
                logger.info("Coat of arms logo loaded successfully");
            } else {
                logoBase64 = "";
                logger.warn("Coat of arms logo not found");
            }
        } catch (Exception e) {
            logoBase64 = "";
            logger.error("Failed to load coat of arms logo", e);
        }
    }

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private AppealsRepository appealsRepository;

    @Autowired
    private ApplicationRegisterRepository applicationRegisterRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private SummonsRepository summonsRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ExcelFileCreator excelFileCreator;

    // ==================== APPEAL REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateAppealReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = null;
            Date dateTo = null;
            if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) {
                dateFrom = sdf.parse(filter.getDateFrom());
            }
            if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) {
                dateTo = sdf.parse(filter.getDateTo());
            }

            List<Appeals> appeals;
            if ("YES".equalsIgnoreCase(filter.getIsTribunal())) {
                appeals = appealsRepository.findByIsFilledTratTrue();
            } else if (dateFrom != null && dateTo != null) {
                appeals = appealsRepository.findByDateOfFillingBetween(dateFrom, dateTo);
            } else {
                appeals = new ArrayList<>();
                appealsRepository.findAll().forEach(appeals::add);
            }

            // Apply optional filters
            appeals = applyFilters(appeals, filter);

            if (appeals.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<EnhancedAppealReportDto> dtos = buildAppealDtos(appeals);
            BigDecimal totalTzs = dtos.stream().map(EnhancedAppealReportDto::getAmountTzs).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalUsd = dtos.stream().map(EnhancedAppealReportDto::getAmountUsd).reduce(BigDecimal.ZERO, BigDecimal::add);

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = (dateFrom != null && dateTo != null)
                    ? "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo)
                    : "All Records";
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateAppealExcel(dtos, totalTzs, totalUsd, dateRange);
            }

            // PDF generation
            Map<String, Object> variables = new HashMap<>();
            variables.put("reportTitle", "Appeals Report");
            variables.put("dateRange", dateRange);
            variables.put("filterDescription", buildFilterDescription(filter));
            variables.put("appeals", dtos);
            variables.put("totalCount", dtos.size());
            variables.put("totalTzs", totalTzs);
            variables.put("totalUsd", totalUsd);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/appeal-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "appeals-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating appeal report", e);
            return buildErrorResponse();
        }
    }

    // ==================== APPEALS BY REGION REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateAppealsByRegionReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = null;
            Date dateTo = null;
            if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) {
                dateFrom = sdf.parse(filter.getDateFrom());
            }
            if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) {
                dateTo = sdf.parse(filter.getDateTo());
            }

            List<Appeals> appeals;
            if ("YES".equalsIgnoreCase(filter.getIsTribunal())) {
                appeals = appealsRepository.findByIsFilledTratTrue();
            } else if (dateFrom != null && dateTo != null) {
                appeals = appealsRepository.findByDateOfFillingBetween(dateFrom, dateTo);
            } else {
                appeals = new ArrayList<>();
                appealsRepository.findAll().forEach(appeals::add);
            }

            appeals = applyFilters(appeals, filter);

            if (appeals.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Build region code -> region name map from DB
            Map<String, String> regionCodeToName = new LinkedHashMap<>();
            for (Region region : regionRepository.findAll()) {
                if (region.getCode() != null && region.getName() != null) {
                    regionCodeToName.put(region.getCode().toUpperCase(), region.getName().toUpperCase());
                }
            }

            List<EnhancedAppealReportDto> dtos = buildAppealDtos(appeals);

            // Group by region: extract region code from appealNo (format: "DSM.5/2024")
            Map<String, List<EnhancedAppealReportDto>> regionGroups = new LinkedHashMap<>();
            for (EnhancedAppealReportDto dto : dtos) {
                String regionName = extractRegionName(dto.getAppealNo(), regionCodeToName);
                regionGroups.computeIfAbsent(regionName, k -> new ArrayList<>()).add(dto);
            }

            // Calculate totals per region
            Map<String, Integer> regionCounts = new LinkedHashMap<>();
            Map<String, BigDecimal> regionTzsTotals = new LinkedHashMap<>();
            Map<String, BigDecimal> regionUsdTotals = new LinkedHashMap<>();
            BigDecimal totalTzs = BigDecimal.ZERO;
            BigDecimal totalUsd = BigDecimal.ZERO;

            for (Map.Entry<String, List<EnhancedAppealReportDto>> entry : regionGroups.entrySet()) {
                String key = entry.getKey();
                List<EnhancedAppealReportDto> list = entry.getValue();
                regionCounts.put(key, list.size());
                BigDecimal tzs = list.stream().map(EnhancedAppealReportDto::getAmountTzs).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal usd = list.stream().map(EnhancedAppealReportDto::getAmountUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
                regionTzsTotals.put(key, tzs);
                regionUsdTotals.put(key, usd);
                totalTzs = totalTzs.add(tzs);
                totalUsd = totalUsd.add(usd);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = (dateFrom != null && dateTo != null)
                    ? "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo)
                    : "All Records";
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateAppealsByRegionExcel(regionGroups, regionTzsTotals, regionUsdTotals, totalTzs, totalUsd, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("filterDescription", buildFilterDescription(filter));
            variables.put("regionGroups", regionGroups.entrySet());
            variables.put("regionCounts", regionCounts);
            variables.put("regionTzsTotals", regionTzsTotals);
            variables.put("regionUsdTotals", regionUsdTotals);
            variables.put("totalCount", dtos.size());
            variables.put("totalRegions", regionGroups.size());
            variables.put("totalTzs", totalTzs);
            variables.put("totalUsd", totalUsd);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/appeals-by-region-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "appeals-by-region-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating appeals by region report", e);
            return buildErrorResponse();
        }
    }

    private String extractRegionName(String appealNo, Map<String, String> regionCodeToName) {
        if (appealNo == null || !appealNo.contains(".")) {
            return "UNKNOWN REGION";
        }
        String code = appealNo.substring(0, appealNo.indexOf(".")).toUpperCase().trim();
        String name = regionCodeToName.get(code);
        return name != null ? name : code;
    }

    // ==================== JUDGE WORKLOAD REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateJudgeWorkloadReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = filter.getDateFrom();
            String endDateStr = filter.getDateTo();
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            List<Appeals> appeals = appealsRepository.findByDateOfFillingBetween(startDate, endDate);

            if (appeals.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group by judge name
            Map<String, List<Appeals>> byJudge = appeals.stream()
                    .collect(Collectors.groupingBy(this::resolveJudgeName));

            List<JudgeWorkloadReportDto> dtos = new ArrayList<>();
            int grandTotal = 0;

            for (Map.Entry<String, List<Appeals>> entry : byJudge.entrySet()) {
                JudgeWorkloadReportDto dto = new JudgeWorkloadReportDto();
                dto.setJudgeName(entry.getKey());
                List<Appeals> judgeAppeals = entry.getValue();
                dto.setTotalCases(judgeAppeals.size());

                int pending = 0, hearing = 0, concluded = 0, decided = 0;
                long totalDecisionDays = 0;
                int decidedWithDays = 0;
                long oldestDays = 0;

                for (Appeals a : judgeAppeals) {
                    if (a.getDecidedDate() != null) {
                        decided++;
                        long days = daysBetween(a.getDateOfFilling(), a.getDecidedDate());
                        totalDecisionDays += days;
                        decidedWithDays++;
                    } else if ("CONCLUDED".equalsIgnoreCase(a.getProcedingStatus())) {
                        concluded++;
                    } else if (a.getSummons() != null) {
                        hearing++;
                    } else {
                        pending++;
                    }

                    if (a.getDecidedDate() == null && a.getDateOfFilling() != null) {
                        long daysOpen = daysBetween(a.getDateOfFilling(), new Date());
                        if (daysOpen > oldestDays) {
                            oldestDays = daysOpen;
                        }
                    }
                }

                dto.setPendingCases(pending);
                dto.setHearingCases(hearing);
                dto.setConcludedCases(concluded);
                dto.setDecidedCases(decided);
                dto.setAvgDaysToDecision(decidedWithDays > 0 ? (double) totalDecisionDays / decidedWithDays : 0);
                dto.setOldestCaseDays(oldestDays);
                dtos.add(dto);
                grandTotal += judgeAppeals.size();
            }

            dtos.sort(Comparator.comparingInt(JudgeWorkloadReportDto::getTotalCases).reversed());

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(startDate) + " To " + displayFormat.format(endDate);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateJudgeWorkloadExcel(dtos, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("judges", dtos);
            variables.put("totalJudges", dtos.size());
            variables.put("totalCases", grandTotal);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/judge-workload-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "judge-workload-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating judge workload report", e);
            return buildErrorResponse();
        }
    }

    // ==================== CASE STATUS SUMMARY REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateCaseStatusSummaryReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = filter.getDateFrom();
            String endDateStr = filter.getDateTo();
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            List<Appeals> appeals = appealsRepository.findByDateOfFillingBetween(startDate, endDate);

            if (appeals.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group by status trend name
            Map<String, List<Appeals>> byStatus = appeals.stream()
                    .filter(a -> a.getStatusTrend() != null)
                    .collect(Collectors.groupingBy(a -> a.getStatusTrend().getAppealStatusTrendName()));

            int totalCases = appeals.size();
            List<CaseStatusSummaryDto> dtos = new ArrayList<>();

            for (Map.Entry<String, List<Appeals>> entry : byStatus.entrySet()) {
                CaseStatusSummaryDto dto = new CaseStatusSummaryDto();
                dto.setStatus(entry.getKey());
                dto.setCount(entry.getValue().size());
                dto.setPercentage(totalCases > 0 ? (double) entry.getValue().size() / totalCases * 100 : 0);

                // Calculate average days in status
                long totalDays = 0;
                int counted = 0;
                for (Appeals a : entry.getValue()) {
                    if (a.getDateOfFilling() != null) {
                        Date endDateForCalc = a.getDecidedDate() != null ? a.getDecidedDate() : new Date();
                        totalDays += daysBetween(a.getDateOfFilling(), endDateForCalc);
                        counted++;
                    }
                }
                dto.setAvgDaysInStatus(counted > 0 ? (double) totalDays / counted : 0);
                dtos.add(dto);
            }

            dtos.sort(Comparator.comparingInt(CaseStatusSummaryDto::getCount).reversed());

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(startDate) + " To " + displayFormat.format(endDate);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateCaseStatusExcel(dtos, totalCases, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("statuses", dtos);
            variables.put("totalCases", totalCases);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/case-status-summary-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "case-status-summary-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating case status summary report", e);
            return buildErrorResponse();
        }
    }

    // ==================== TAX TYPE ANALYSIS REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateTaxTypeAnalysisReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = filter.getDateFrom();
            String endDateStr = filter.getDateTo();
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            List<Appeals> appeals = appealsRepository.findByDateOfFillingBetween(startDate, endDate);
            Pageable pageable = PageRequest.of(0, 10000);
            List<ApplicationRegister> applications = applicationRegisterRepository
                    .findApplicationRegistersByDateOfFillingBetween(startDate, endDate, pageable).getContent();

            if (appeals.isEmpty() && applications.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group appeals by tax type
            Map<String, List<Appeals>> appealsByTax = appeals.stream()
                    .filter(a -> a.getTax() != null)
                    .collect(Collectors.groupingBy(a -> a.getTax().getTaxName().toUpperCase()));

            // Group applications by tax type
            Map<String, Long> appCountByTax = applications.stream()
                    .filter(a -> a.getTaxes() != null)
                    .collect(Collectors.groupingBy(a -> a.getTaxes().getTaxName().toUpperCase(), Collectors.counting()));

            // Merge all tax type names
            Set<String> allTaxTypes = new HashSet<>(appealsByTax.keySet());
            allTaxTypes.addAll(appCountByTax.keySet());

            List<TaxTypeAnalysisDto> dtos = new ArrayList<>();
            int grandTotalAppeals = 0, grandTotalApplications = 0, grandTotalCases = 0;
            int grandTotalPending = 0, grandTotalDecided = 0;
            BigDecimal grandTotalAmount = BigDecimal.ZERO;

            for (String taxType : allTaxTypes) {
                TaxTypeAnalysisDto dto = new TaxTypeAnalysisDto();
                dto.setTaxType(taxType);

                List<Appeals> taxAppeals = appealsByTax.getOrDefault(taxType, Collections.emptyList());
                int appealCount = taxAppeals.size();
                int appCount = appCountByTax.getOrDefault(taxType, 0L).intValue();

                int pending = 0, decided = 0;
                BigDecimal totalTzs = BigDecimal.ZERO;

                for (Appeals a : taxAppeals) {
                    if (a.getDecidedDate() != null) {
                        decided++;
                    } else {
                        pending++;
                    }
                    for (AppealAmount amt : a.getAppealAmount()) {
                        if (amt.getCurrency() != null && "TZS".equals(amt.getCurrency().getCurrencyShortName())) {
                            totalTzs = totalTzs.add(amt.getAmountOnDispute() != null ? amt.getAmountOnDispute() : BigDecimal.ZERO);
                        }
                    }
                }

                dto.setAppealCount(appealCount);
                dto.setApplicationCount(appCount);
                dto.setTotalCases(appealCount + appCount);
                dto.setPendingCases(pending);
                dto.setDecidedCases(decided);
                dto.setTotalAmountTzs(totalTzs);

                dtos.add(dto);

                grandTotalAppeals += appealCount;
                grandTotalApplications += appCount;
                grandTotalCases += appealCount + appCount;
                grandTotalPending += pending;
                grandTotalDecided += decided;
                grandTotalAmount = grandTotalAmount.add(totalTzs);
            }

            dtos.sort(Comparator.comparingInt(TaxTypeAnalysisDto::getTotalCases).reversed());

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(startDate) + " To " + displayFormat.format(endDate);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateTaxTypeExcel(dtos, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("taxes", dtos);
            variables.put("grandTotalAppeals", grandTotalAppeals);
            variables.put("grandTotalApplications", grandTotalApplications);
            variables.put("grandTotalCases", grandTotalCases);
            variables.put("grandTotalPending", grandTotalPending);
            variables.put("grandTotalDecided", grandTotalDecided);
            variables.put("grandTotalAmount", grandTotalAmount);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/tax-type-analysis-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "tax-type-analysis-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating tax type analysis report", e);
            return buildErrorResponse();
        }
    }

    // ==================== OVERDUE CASES REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateOverdueCasesReport(ReportFilterDto filter) {
        try {
            int minDays = filter.getMinDaysOpen() != null ? filter.getMinDaysOpen() : 90;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -minDays);
            Date cutoffDate = cal.getTime();

            List<Appeals> overdueAppeals = appealsRepository.findOverdueAppeals(cutoffDate);

            if (overdueAppeals.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<OverdueCaseDto> dtos = new ArrayList<>();
            int bucket90to180 = 0, bucket180to365 = 0, bucket1to2years = 0, bucket2plusYears = 0;

            for (Appeals a : overdueAppeals) {
                long daysOpen = daysBetween(a.getDateOfFilling(), new Date());
                if (daysOpen < minDays) continue;

                OverdueCaseDto dto = new OverdueCaseDto();
                dto.setAppealNo(a.getAppealNo());
                dto.setAppellant(a.getAppellantName() != null ? a.getAppellantName() : "-");
                dto.setRespondent("COMM GENERAL");
                dto.setTaxType(a.getTax() != null ? a.getTax().getTaxName().toUpperCase() : "-");
                dto.setFilingDate(a.getDateOfFilling());
                dto.setDaysOpen(daysOpen);
                dto.setAgingBucket(calculateAgingBucket(daysOpen));
                dto.setProgressStatus(a.getProcedingStatus() != null ? a.getProcedingStatus() : "PENDING");
                dto.setJudgeName(resolveJudgeName(a));

                dtos.add(dto);

                if (daysOpen < 180) bucket90to180++;
                else if (daysOpen < 365) bucket180to365++;
                else if (daysOpen < 730) bucket1to2years++;
                else bucket2plusYears++;
            }

            if (dtos.isEmpty()) {
                return buildNoRecordResponse();
            }

            dtos.sort(Comparator.comparingLong(OverdueCaseDto::getDaysOpen).reversed());

            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());
            String dateRange = "Cases open more than " + minDays + " days (as of " +
                    new SimpleDateFormat("dd MMMM yyyy").format(new Date()) + ")";

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateOverdueCasesExcel(dtos, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("cases", dtos);
            variables.put("totalOverdue", dtos.size());
            variables.put("bucket90to180", bucket90to180);
            variables.put("bucket180to365", bucket180to365);
            variables.put("bucket1to2years", bucket1to2years);
            variables.put("bucket2plusYears", bucket2plusYears);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/overdue-cases-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "overdue-cases-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating overdue cases report", e);
            return buildErrorResponse();
        }
    }

    // ==================== HELPER METHODS ====================

    private String renderTemplate(String templateName, Map<String, Object> variables) {
        variables.put("logoBase64", logoBase64);
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }

    private byte[] renderHtmlToPdf(String htmlContent) throws Exception {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.createPDF(outputStream);
        outputStream.close();
        return outputStream.toByteArray();
    }

    private List<Appeals> applyFilters(List<Appeals> appeals, ReportFilterDto filter) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return appeals.stream()
                .filter(a -> {
                    // Tax Category filter
                    if (filter.getTaxType() != null && !filter.getTaxType().isEmpty()) {
                        if (a.getTax() == null || !filter.getTaxType().equals(a.getTax().getId())) return false;
                    }
                    // Appeal Status Trend filter
                    if (filter.getStatusTrend() != null && !filter.getStatusTrend().isEmpty()) {
                        if (a.getStatusTrend() == null || !filter.getStatusTrend().equals(a.getStatusTrend().getId())) return false;
                    }
                    // Progress Status filter
                    if (filter.getProgressStatus() != null && !filter.getProgressStatus().isEmpty()) {
                        if (!filter.getProgressStatus().equalsIgnoreCase(a.getProcedingStatus())) return false;
                    }
                    // Financial Year filter
                    if (filter.getFinancialYear() != null && !filter.getFinancialYear().isEmpty()) {
                        if (a.getBillId() == null || !filter.getFinancialYear().equals(a.getBillId().getFinancialYear())) return false;
                    }
                    // Decision Date From filter
                    if (filter.getDateOfDecisionFrom() != null && !filter.getDateOfDecisionFrom().isEmpty()) {
                        try {
                            Date decisionFrom = sdf.parse(filter.getDateOfDecisionFrom());
                            if (a.getDecidedDate() == null || a.getDecidedDate().before(decisionFrom)) return false;
                        } catch (Exception ignored) {}
                    }
                    // Decision Date To filter
                    if (filter.getDateOfDecisionTo() != null && !filter.getDateOfDecisionTo().isEmpty()) {
                        try {
                            Date decisionTo = sdf.parse(filter.getDateOfDecisionTo());
                            if (a.getDecidedDate() == null || a.getDecidedDate().after(decisionTo)) return false;
                        } catch (Exception ignored) {}
                    }
                    // Region filter (region code is embedded in appeal number)
                    if (filter.getRegion() != null && !filter.getRegion().isEmpty()) {
                        if (a.getAppealNo() == null || !a.getAppealNo().toUpperCase().contains(filter.getRegion().toUpperCase())) return false;
                    }
                    // Won By filter
                    if (filter.getWonBy() != null && !filter.getWonBy().isEmpty()) {
                        if (a.getWonBy() == null || !filter.getWonBy().equalsIgnoreCase(a.getWonBy())) return false;
                    }
                    // Judge ID filter
                    if (filter.getJudgeId() != null && !filter.getJudgeId().isEmpty()) {
                        boolean matchesJudge = false;
                        if (a.getSummons() != null && a.getSummons().getJud() != null
                                && filter.getJudgeId().equals(a.getSummons().getJud().getId())) {
                            matchesJudge = true;
                        }
                        if (!matchesJudge) return false;
                    }
                    // Chair Person (Judge name) filter
                    if (filter.getChairPerson() != null && !filter.getChairPerson().isEmpty()) {
                        boolean matchesChair = false;
                        // Check summons judge name
                        if (a.getSummons() != null && a.getSummons().getJud() != null
                                && a.getSummons().getJud().getName() != null
                                && a.getSummons().getJud().getName().toUpperCase().contains(filter.getChairPerson().toUpperCase())) {
                            matchesChair = true;
                        }
                        // Check summons judge string field
                        if (!matchesChair && a.getSummons() != null && a.getSummons().getJudge() != null
                                && a.getSummons().getJudge().toUpperCase().contains(filter.getChairPerson().toUpperCase())) {
                            matchesChair = true;
                        }
                        // Check appeal decidedBy field
                        if (!matchesChair && a.getDecidedBy() != null
                                && a.getDecidedBy().toUpperCase().contains(filter.getChairPerson().toUpperCase())) {
                            matchesChair = true;
                        }
                        if (!matchesChair) return false;
                    }
                    // Hearing Stage filter
                    if (filter.getHearingStage() != null && !filter.getHearingStage().isEmpty()) {
                        if (a.getProcedingStatus() == null || !filter.getHearingStage().equalsIgnoreCase(a.getProcedingStatus())) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<EnhancedAppealReportDto> buildAppealDtos(List<Appeals> appeals) {
        List<EnhancedAppealReportDto> dtos = new ArrayList<>();
        int sn = 0;
        for (Appeals a : appeals) {
            sn++;
            EnhancedAppealReportDto dto = new EnhancedAppealReportDto();
            dto.setSerialNumber(sn);
            dto.setAppealNo(a.getAppealNo());
            dto.setAppellant(a.getAppellantName() != null ? a.getAppellantName() : "-");
            dto.setRespondent("COMM GENERAL");
            dto.setTaxType(a.getTax() != null ? a.getTax().getTaxName().toUpperCase() : "-");
            dto.setFilingDate(a.getDateOfFilling());
            dto.setDecisionDate(a.getDecidedDate());
            dto.setJudgeName(resolveJudgeName(a));

            // Days on trial
            if (a.getDateOfFilling() != null) {
                Date endDate = a.getDecidedDate() != null ? a.getDecidedDate() : new Date();
                dto.setDaysOnTrial(daysBetween(a.getDateOfFilling(), endDate));
            }

            // Amounts
            BigDecimal tzs = BigDecimal.ZERO;
            BigDecimal usd = BigDecimal.ZERO;
            if (a.getAppealAmount() != null) {
                for (AppealAmount amt : a.getAppealAmount()) {
                    if (amt.getCurrency() != null && amt.getAmountOnDispute() != null) {
                        if ("TZS".equals(amt.getCurrency().getCurrencyShortName())) {
                            tzs = tzs.add(amt.getAmountOnDispute());
                        } else if ("USD".equals(amt.getCurrency().getCurrencyShortName())) {
                            usd = usd.add(amt.getAmountOnDispute());
                        }
                    }
                }
            }
            dto.setAmountTzs(tzs);
            dto.setAmountUsd(usd);

            dto.setStatus(a.getStatusTrend() != null ? a.getStatusTrend().getAppealStatusTrendName() : "-");
            dto.setDecidedBy(resolveDecidedBy(a));
            dto.setRemarks(a.getSummaryOfDecree() != null ? a.getSummaryOfDecree().toUpperCase() : "NO REMARKS");

            dtos.add(dto);
        }
        return dtos;
    }

    private String resolveJudgeName(Appeals appeal) {
        if (appeal.getSummons() != null) {
            if (appeal.getSummons().getJud() != null && appeal.getSummons().getJud().getName() != null) {
                return appeal.getSummons().getJud().getName().toUpperCase();
            }
            if (appeal.getSummons().getJudge() != null) {
                return appeal.getSummons().getJudge().toUpperCase();
            }
        }
        if (appeal.getDecidedBy() != null) {
            return appeal.getDecidedBy().toUpperCase();
        }
        return "UNASSIGNED";
    }

    private String resolveDecidedBy(Appeals appeal) {
        // Check appeal entity first
        if (appeal.getDecidedBy() != null && !appeal.getDecidedBy().trim().isEmpty()) {
            return appeal.getDecidedBy().toUpperCase();
        }
        // Fall back to linked summons judge
        if (appeal.getSummons() != null) {
            if (appeal.getSummons().getJud() != null && appeal.getSummons().getJud().getName() != null) {
                return appeal.getSummons().getJud().getName().toUpperCase();
            }
            if (appeal.getSummons().getJudge() != null && !appeal.getSummons().getJudge().trim().isEmpty()) {
                return appeal.getSummons().getJudge().toUpperCase();
            }
        }
        return "-";
    }

    private String calculateAgingBucket(long daysOpen) {
        if (daysOpen < 180) return "90-180 days";
        if (daysOpen < 365) return "180-365 days";
        if (daysOpen < 730) return "1-2 years";
        return "2+ years";
    }

    private long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        long diffMs = end.getTime() - start.getTime();
        return TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);
    }

    private String buildFilterDescription(ReportFilterDto filter) {
        List<String> parts = new ArrayList<>();
        if (filter.getFinancialYear() != null && !filter.getFinancialYear().isEmpty()) {
            parts.add("Financial Year: " + filter.getFinancialYear());
        }
        if (filter.getProgressStatus() != null && !filter.getProgressStatus().isEmpty()) {
            parts.add("Progress: " + filter.getProgressStatus());
        }
        if ("YES".equalsIgnoreCase(filter.getIsTribunal())) {
            parts.add("Filed to Tribunal");
        }
        if (filter.getDateOfDecisionFrom() != null && !filter.getDateOfDecisionFrom().isEmpty()) {
            parts.add("Decision From: " + filter.getDateOfDecisionFrom());
        }
        if (filter.getDateOfDecisionTo() != null && !filter.getDateOfDecisionTo().isEmpty()) {
            parts.add("Decision To: " + filter.getDateOfDecisionTo());
        }
        if (filter.getRegion() != null && !filter.getRegion().isEmpty()) {
            parts.add("Region: " + filter.getRegion());
        }
        if (filter.getWonBy() != null && !filter.getWonBy().isEmpty()) {
            parts.add("Won By: " + filter.getWonBy());
        }
        if (filter.getChairPerson() != null && !filter.getChairPerson().isEmpty()) {
            parts.add("Chair Person: " + filter.getChairPerson());
        }
        if (filter.getHearingStage() != null && !filter.getHearingStage().isEmpty()) {
            parts.add("Hearing Stage: " + filter.getHearingStage());
        }
        return String.join(" | ", parts);
    }

    // ==================== EXCEL GENERATION ====================

    private Response<HtmlReportResponseDto> generateAppealsByRegionExcel(
            Map<String, List<EnhancedAppealReportDto>> regionGroups,
            Map<String, BigDecimal> regionTzsTotals, Map<String, BigDecimal> regionUsdTotals,
            BigDecimal totalTzs, BigDecimal totalUsd, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "APPEAL NO", "APPELLANT", "RESPONDENT", "TAX TYPE",
                    "FILING DATE", "DECISION DATE", "DAYS ON TRIAL", "AMOUNT (TZS)", "AMOUNT (USD)", "STATUS", "DECIDED BY", "REMARKS"};
            excelFileCreator.writeTableHeaderExcel("Appeals By Region",
                    "Tax Revenue Appeals Board (TRAB)\nAppeals Report - Grouped by Region\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;

            for (Map.Entry<String, List<EnhancedAppealReportDto>> entry : regionGroups.entrySet()) {
                // Region header row
                org.apache.poi.ss.usermodel.Row regionRow = excelFileCreator.sheet.createRow(startRow++);
                excelFileCreator.createCell(regionRow, 0, entry.getKey() + " (" + entry.getValue().size() + " Appeals)", style);
                startRow++;

                int sn = 0;
                for (EnhancedAppealReportDto dto : entry.getValue()) {
                    sn++;
                    org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                    int col = 0;
                    excelFileCreator.createCell(row, col++, sn, style);
                    excelFileCreator.createCell(row, col++, dto.getAppealNo(), style);
                    excelFileCreator.createCell(row, col++, dto.getAppellant(), style);
                    excelFileCreator.createCell(row, col++, dto.getRespondent(), style);
                    excelFileCreator.createCell(row, col++, dto.getTaxType(), style);
                    excelFileCreator.createCell(row, col++, dto.getFilingDate() != null ? outputFormat.format(dto.getFilingDate()) : "-", style);
                    excelFileCreator.createCell(row, col++, dto.getDecisionDate() != null ? outputFormat.format(dto.getDecisionDate()) : "-", style);
                    excelFileCreator.createCell(row, col++, dto.getDaysOnTrial(), style);
                    excelFileCreator.createCell(row, col++, dto.getAmountTzs(), style);
                    excelFileCreator.createCell(row, col++, dto.getAmountUsd(), style);
                    excelFileCreator.createCell(row, col++, dto.getStatus(), style);
                    excelFileCreator.createCell(row, col++, dto.getDecidedBy(), style);
                    excelFileCreator.createCell(row, col++, dto.getRemarks(), style);
                }

                // Subtotal row
                org.apache.poi.ss.usermodel.Row subRow = excelFileCreator.sheet.createRow(startRow++);
                excelFileCreator.createCell(subRow, 7, "Subtotal - " + entry.getKey(), style);
                excelFileCreator.createCell(subRow, 8, regionTzsTotals.get(entry.getKey()), style);
                excelFileCreator.createCell(subRow, 9, regionUsdTotals.get(entry.getKey()), style);
                startRow++;
            }

            // Grand total row
            org.apache.poi.ss.usermodel.Row totalRow = excelFileCreator.sheet.createRow(startRow);
            excelFileCreator.createCell(totalRow, 7, "GRAND TOTAL", style);
            excelFileCreator.createCell(totalRow, 8, totalTzs, style);
            excelFileCreator.createCell(totalRow, 9, totalUsd, style);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "appeals-by-region-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating appeals by region Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateAppealExcel(List<EnhancedAppealReportDto> dtos,
                                                  BigDecimal totalTzs, BigDecimal totalUsd, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "APPEAL NO", "APPELLANT", "RESPONDENT", "TAX TYPE",
                    "FILING DATE", "DECISION DATE", "DAYS ON TRIAL", "AMOUNT (TZS)", "AMOUNT (USD)", "STATUS", "DECIDED BY", "REMARKS"};
            excelFileCreator.writeTableHeaderExcel("Appeals Report",
                    "Tax Revenue Appeals Board (TRAB)\nAppeals Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;

            for (EnhancedAppealReportDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getAppealNo(), style);
                excelFileCreator.createCell(row, col++, dto.getAppellant(), style);
                excelFileCreator.createCell(row, col++, dto.getRespondent(), style);
                excelFileCreator.createCell(row, col++, dto.getTaxType(), style);
                excelFileCreator.createCell(row, col++, dto.getFilingDate() != null ? outputFormat.format(dto.getFilingDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getDecisionDate() != null ? outputFormat.format(dto.getDecisionDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getDaysOnTrial(), style);
                excelFileCreator.createCell(row, col++, dto.getAmountTzs(), style);
                excelFileCreator.createCell(row, col++, dto.getAmountUsd(), style);
                excelFileCreator.createCell(row, col++, dto.getStatus(), style);
                excelFileCreator.createCell(row, col++, dto.getDecidedBy(), style);
                excelFileCreator.createCell(row, col++, dto.getRemarks(), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "appeals-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating appeal Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateJudgeWorkloadExcel(List<JudgeWorkloadReportDto> dtos, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "JUDGE NAME", "TOTAL CASES", "PENDING", "HEARING",
                    "CONCLUDED", "DECIDED", "AVG DAYS TO DECISION", "OLDEST CASE (DAYS)"};
            excelFileCreator.writeTableHeaderExcel("Judge Workload",
                    "Tax Revenue Appeals Board (TRAB)\nJudge Workload Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            int startRow = 2;
            int sn = 0;

            for (JudgeWorkloadReportDto dto : dtos) {
                sn++;
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, sn, style);
                excelFileCreator.createCell(row, col++, dto.getJudgeName(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalCases(), style);
                excelFileCreator.createCell(row, col++, dto.getPendingCases(), style);
                excelFileCreator.createCell(row, col++, dto.getHearingCases(), style);
                excelFileCreator.createCell(row, col++, dto.getConcludedCases(), style);
                excelFileCreator.createCell(row, col++, dto.getDecidedCases(), style);
                excelFileCreator.createCell(row, col++, Math.round(dto.getAvgDaysToDecision()), style);
                excelFileCreator.createCell(row, col++, dto.getOldestCaseDays(), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "judge-workload-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating judge workload Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateCaseStatusExcel(List<CaseStatusSummaryDto> dtos, int totalCases, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "STATUS", "COUNT", "PERCENTAGE (%)", "AVG DAYS IN STATUS"};
            excelFileCreator.writeTableHeaderExcel("Case Status Summary",
                    "Tax Revenue Appeals Board (TRAB)\nCase Status Summary\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            int startRow = 2;
            int sn = 0;

            for (CaseStatusSummaryDto dto : dtos) {
                sn++;
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, sn, style);
                excelFileCreator.createCell(row, col++, dto.getStatus(), style);
                excelFileCreator.createCell(row, col++, dto.getCount(), style);
                excelFileCreator.createCell(row, col++, String.format("%.1f", dto.getPercentage()), style);
                excelFileCreator.createCell(row, col++, Math.round(dto.getAvgDaysInStatus()), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "case-status-summary-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating case status Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateTaxTypeExcel(List<TaxTypeAnalysisDto> dtos, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "TAX TYPE", "APPEALS", "APPLICATIONS", "TOTAL CASES",
                    "PENDING", "DECIDED", "TOTAL AMOUNT (TZS)"};
            excelFileCreator.writeTableHeaderExcel("Tax Type Analysis",
                    "Tax Revenue Appeals Board (TRAB)\nTax Type Analysis\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            int startRow = 2;
            int sn = 0;

            for (TaxTypeAnalysisDto dto : dtos) {
                sn++;
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, sn, style);
                excelFileCreator.createCell(row, col++, dto.getTaxType(), style);
                excelFileCreator.createCell(row, col++, dto.getAppealCount(), style);
                excelFileCreator.createCell(row, col++, dto.getApplicationCount(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalCases(), style);
                excelFileCreator.createCell(row, col++, dto.getPendingCases(), style);
                excelFileCreator.createCell(row, col++, dto.getDecidedCases(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalAmountTzs(), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "tax-type-analysis-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating tax type Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateOverdueCasesExcel(List<OverdueCaseDto> dtos, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "APPEAL NO", "APPELLANT", "RESPONDENT", "TAX TYPE",
                    "FILING DATE", "DAYS OPEN", "AGING BUCKET", "PROGRESS STATUS", "JUDGE"};
            excelFileCreator.writeTableHeaderExcel("Overdue Cases",
                    "Tax Revenue Appeals Board (TRAB)\nOverdue/Aging Cases Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;
            int sn = 0;

            for (OverdueCaseDto dto : dtos) {
                sn++;
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, sn, style);
                excelFileCreator.createCell(row, col++, dto.getAppealNo(), style);
                excelFileCreator.createCell(row, col++, dto.getAppellant(), style);
                excelFileCreator.createCell(row, col++, dto.getRespondent(), style);
                excelFileCreator.createCell(row, col++, dto.getTaxType(), style);
                excelFileCreator.createCell(row, col++, dto.getFilingDate() != null ? outputFormat.format(dto.getFilingDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getDaysOpen(), style);
                excelFileCreator.createCell(row, col++, dto.getAgingBucket(), style);
                excelFileCreator.createCell(row, col++, dto.getProgressStatus(), style);
                excelFileCreator.createCell(row, col++, dto.getJudgeName(), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "overdue-cases-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating overdue cases Excel report", e);
            return buildErrorResponse();
        }
    }

    // ==================== PAYMENT REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generatePaymentReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = sdf.parse(filter.getDateFrom());
            Date dateTo = sdf.parse(filter.getDateTo());

            List<Payment> payments = paymentRepository.findPaymentsByDateRange(dateFrom, dateTo);

            if (payments.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group by appType
            Map<String, List<PaymentReportDto>> paymentGroups = new LinkedHashMap<>();
            Map<String, BigDecimal> groupSubtotals = new LinkedHashMap<>();
            BigDecimal grandTotal = BigDecimal.ZERO;
            int sn = 0;

            Map<String, List<Payment>> grouped = new LinkedHashMap<>();
            for (Payment p : payments) {
                String appType = p.getBill() != null && p.getBill().getAppType() != null ? p.getBill().getAppType() : "OTHER";
                grouped.computeIfAbsent(appType, k -> new ArrayList<>()).add(p);
            }

            for (Map.Entry<String, List<Payment>> entry : grouped.entrySet()) {
                List<PaymentReportDto> dtos = new ArrayList<>();
                BigDecimal subtotal = BigDecimal.ZERO;
                for (Payment p : entry.getValue()) {
                    sn++;
                    PaymentReportDto dto = new PaymentReportDto();
                    dto.setSerialNumber(sn);
                    dto.setControlNumber(p.getPayCtrNum());
                    dto.setPspReceiptNumber(p.getTrxId());
                    dto.setPayerName(p.getPyrName() != null ? p.getPyrName() : "-");
                    dto.setPaymentDate(p.getTrxDtm());
                    dto.setPspName(p.getPspName() != null ? p.getPspName() : "-");
                    dto.setPaidAmount(p.getPaidAmt() != null ? p.getPaidAmt() : BigDecimal.ZERO);
                    dto.setAppType(entry.getKey());
                    dtos.add(dto);
                    subtotal = subtotal.add(dto.getPaidAmount());
                }
                paymentGroups.put(entry.getKey(), dtos);
                groupSubtotals.put(entry.getKey(), subtotal);
                grandTotal = grandTotal.add(subtotal);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generatePaymentExcel(paymentGroups, groupSubtotals, grandTotal, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("paymentGroups", paymentGroups);
            variables.put("groupSubtotals", groupSubtotals);
            variables.put("grandTotal", grandTotal);
            variables.put("totalPayments", sn);
            variables.put("totalGroups", paymentGroups.size());
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/payment-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "payment-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating payment report", e);
            return buildErrorResponse();
        }
    }

    // ==================== OUTSTANDING BILLS REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateOutstandingBillsReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = sdf.parse(filter.getDateFrom());
            Date dateTo = sdf.parse(filter.getDateTo());

            List<Bill> unpaidBills = billRepository.findByBillPayedFalseAndGeneratedDateBetween(dateFrom, dateTo);

            if (unpaidBills.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<OutstandingBillDto> dtos = new ArrayList<>();
            BigDecimal totalOutstanding = BigDecimal.ZERO;
            int bucket0to30 = 0, bucket31to60 = 0, bucket61to90 = 0, bucket90plus = 0;
            Date now = new Date();
            int sn = 0;

            for (Bill bill : unpaidBills) {
                sn++;
                OutstandingBillDto dto = new OutstandingBillDto();
                dto.setSerialNumber(sn);
                dto.setControlNumber(bill.getBillControlNumber() != null ? bill.getBillControlNumber() : "-");
                dto.setBillReference(bill.getBillReference() != null ? bill.getBillReference() : "-");
                dto.setAppType(bill.getAppType() != null ? bill.getAppType() : "-");
                dto.setPayerName(bill.getPayerName() != null ? bill.getPayerName() : "-");
                dto.setCurrency(bill.getCurrency() != null ? bill.getCurrency() : "TZS");
                dto.setBilledAmount(bill.getBilledAmount() != null ? bill.getBilledAmount() : BigDecimal.ZERO);
                dto.setGeneratedDate(bill.getGeneratedDate());
                dto.setExpiryDate(bill.getExpiryDate());

                long agingDays = bill.getGeneratedDate() != null ? daysBetween(bill.getGeneratedDate(), now) : 0;
                dto.setAgingDays(agingDays);
                dto.setAgingBucket(calculateBillAgingBucket(agingDays));

                if (agingDays <= 30) bucket0to30++;
                else if (agingDays <= 60) bucket31to60++;
                else if (agingDays <= 90) bucket61to90++;
                else bucket90plus++;

                totalOutstanding = totalOutstanding.add(dto.getBilledAmount());
                dtos.add(dto);
            }

            dtos.sort(Comparator.comparingLong(OutstandingBillDto::getAgingDays).reversed());

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateOutstandingBillsExcel(dtos, totalOutstanding, dateRange, bucket0to30, bucket31to60, bucket61to90, bucket90plus);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("bills", dtos);
            variables.put("totalBills", dtos.size());
            variables.put("totalOutstanding", totalOutstanding);
            variables.put("bucket0to30", bucket0to30);
            variables.put("bucket31to60", bucket31to60);
            variables.put("bucket61to90", bucket61to90);
            variables.put("bucket90plus", bucket90plus);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/outstanding-bills-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "outstanding-bills-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating outstanding bills report", e);
            return buildErrorResponse();
        }
    }

    // ==================== REVENUE SUMMARY REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateRevenueSummaryReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = sdf.parse(filter.getDateFrom());
            Date dateTo = sdf.parse(filter.getDateTo());

            List<Bill> allBills = billRepository.findByGeneratedDateBetween(dateFrom, dateTo);

            if (allBills.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group by appType
            Map<String, List<Bill>> byAppType = allBills.stream()
                    .collect(Collectors.groupingBy(b -> b.getAppType() != null ? b.getAppType() : "OTHER"));

            List<RevenueSummaryDto> dtos = new ArrayList<>();
            int grandTotalBills = 0, grandTotalPaid = 0;
            BigDecimal grandTotalBilled = BigDecimal.ZERO, grandTotalCollected = BigDecimal.ZERO;

            for (Map.Entry<String, List<Bill>> entry : byAppType.entrySet()) {
                RevenueSummaryDto dto = new RevenueSummaryDto();
                dto.setAppType(entry.getKey());

                List<Bill> bills = entry.getValue();
                int billCount = bills.size();
                int paidCount = 0;
                BigDecimal totalBilled = BigDecimal.ZERO;
                BigDecimal totalCollected = BigDecimal.ZERO;

                for (Bill bill : bills) {
                    totalBilled = totalBilled.add(bill.getBilledAmount() != null ? bill.getBilledAmount() : BigDecimal.ZERO);
                    if (bill.isBillPayed()) {
                        paidCount++;
                        totalCollected = totalCollected.add(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
                    }
                }

                dto.setBillCount(billCount);
                dto.setPaidCount(paidCount);
                dto.setTotalBilled(totalBilled);
                dto.setTotalCollected(totalCollected);
                dto.setCollectionRate(totalBilled.compareTo(BigDecimal.ZERO) > 0
                        ? totalCollected.multiply(BigDecimal.valueOf(100)).divide(totalBilled, 2, BigDecimal.ROUND_HALF_UP).doubleValue()
                        : 0);

                dtos.add(dto);
                grandTotalBills += billCount;
                grandTotalPaid += paidCount;
                grandTotalBilled = grandTotalBilled.add(totalBilled);
                grandTotalCollected = grandTotalCollected.add(totalCollected);
            }

            dtos.sort((a, b) -> b.getTotalBilled().compareTo(a.getTotalBilled()));

            double overallCollectionRate = grandTotalBilled.compareTo(BigDecimal.ZERO) > 0
                    ? grandTotalCollected.multiply(BigDecimal.valueOf(100)).divide(grandTotalBilled, 2, BigDecimal.ROUND_HALF_UP).doubleValue()
                    : 0;

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateRevenueSummaryExcel(dtos, grandTotalBills, grandTotalPaid, grandTotalBilled, grandTotalCollected, overallCollectionRate, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("revenues", dtos);
            variables.put("totalCategories", dtos.size());
            variables.put("grandTotalBills", grandTotalBills);
            variables.put("grandTotalPaid", grandTotalPaid);
            variables.put("grandTotalBilled", grandTotalBilled);
            variables.put("grandTotalCollected", grandTotalCollected);
            variables.put("overallCollectionRate", overallCollectionRate);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/revenue-summary-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "revenue-summary-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating revenue summary report", e);
            return buildErrorResponse();
        }
    }

    // ==================== BILL RECONCILIATION REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateBillReconciliationReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = sdf.parse(filter.getDateFrom());
            Date dateTo = sdf.parse(filter.getDateTo());

            List<Bill> allBills = billRepository.findByGeneratedDateBetween(dateFrom, dateTo);

            if (allBills.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<BillReconciliationDto> dtos = new ArrayList<>();
            BigDecimal totalBilled = BigDecimal.ZERO, totalPaid = BigDecimal.ZERO, totalVariance = BigDecimal.ZERO;
            int paidCount = 0, unpaidCount = 0, expiredCount = 0;
            Date now = new Date();
            int sn = 0;

            for (Bill bill : allBills) {
                sn++;
                BillReconciliationDto dto = new BillReconciliationDto();
                dto.setSerialNumber(sn);
                dto.setControlNumber(bill.getBillControlNumber() != null ? bill.getBillControlNumber() : "-");
                dto.setBillReference(bill.getBillReference() != null ? bill.getBillReference() : "-");
                dto.setAppType(bill.getAppType() != null ? bill.getAppType() : "-");
                dto.setPayerName(bill.getPayerName() != null ? bill.getPayerName() : "-");
                dto.setCurrency(bill.getCurrency() != null ? bill.getCurrency() : "TZS");

                BigDecimal billed = bill.getBilledAmount() != null ? bill.getBilledAmount() : BigDecimal.ZERO;
                BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
                BigDecimal variance = billed.subtract(paid);

                dto.setBilledAmount(billed);
                dto.setPaidAmount(paid);
                dto.setVariance(variance);
                dto.setGeneratedDate(bill.getGeneratedDate());

                // Determine status
                if (bill.isBillPayed()) {
                    dto.setStatus("Paid");
                    paidCount++;
                } else if (bill.getExpiryDate() != null && bill.getExpiryDate().before(now)) {
                    dto.setStatus("Expired");
                    expiredCount++;
                } else {
                    dto.setStatus("Unpaid");
                    unpaidCount++;
                }

                totalBilled = totalBilled.add(billed);
                totalPaid = totalPaid.add(paid);
                totalVariance = totalVariance.add(variance);
                dtos.add(dto);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo);
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateBillReconciliationExcel(dtos, totalBilled, totalPaid, totalVariance, paidCount, unpaidCount, expiredCount, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("reconciliations", dtos);
            variables.put("totalBills", dtos.size());
            variables.put("totalBilled", totalBilled);
            variables.put("totalPaid", totalPaid);
            variables.put("totalVariance", totalVariance);
            variables.put("paidCount", paidCount);
            variables.put("unpaidCount", unpaidCount);
            variables.put("expiredCount", expiredCount);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/bill-reconciliation-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "bill-reconciliation-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating bill reconciliation report", e);
            return buildErrorResponse();
        }
    }

    // ==================== FINANCE REPORT EXCEL GENERATORS ====================

    private Response<HtmlReportResponseDto> generatePaymentExcel(Map<String, List<PaymentReportDto>> paymentGroups,
                                                                  Map<String, BigDecimal> groupSubtotals,
                                                                  BigDecimal grandTotal, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "CONTROL NUMBER", "RECEIPT NO", "PAYER NAME", "PAYMENT DATE", "PSP NAME", "PAID AMOUNT", "APP TYPE"};
            excelFileCreator.writeTableHeaderExcel("Payment Report",
                    "Tax Revenue Appeals Board (TRAB)\nPayment Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;

            for (Map.Entry<String, List<PaymentReportDto>> entry : paymentGroups.entrySet()) {
                for (PaymentReportDto dto : entry.getValue()) {
                    org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                    int col = 0;
                    excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                    excelFileCreator.createCell(row, col++, dto.getControlNumber(), style);
                    excelFileCreator.createCell(row, col++, dto.getPspReceiptNumber(), style);
                    excelFileCreator.createCell(row, col++, dto.getPayerName(), style);
                    excelFileCreator.createCell(row, col++, dto.getPaymentDate() != null ? outputFormat.format(dto.getPaymentDate()) : "-", style);
                    excelFileCreator.createCell(row, col++, dto.getPspName(), style);
                    excelFileCreator.createCell(row, col++, dto.getPaidAmount(), style);
                    excelFileCreator.createCell(row, col++, dto.getAppType(), style);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "payment-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating payment Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateOutstandingBillsExcel(List<OutstandingBillDto> dtos,
                                                                          BigDecimal totalOutstanding, String dateRange,
                                                                          int bucket0to30, int bucket31to60, int bucket61to90, int bucket90plus) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "CONTROL NUMBER", "BILL REFERENCE", "APP TYPE", "PAYER NAME",
                    "CURRENCY", "BILLED AMOUNT", "GENERATED DATE", "EXPIRY DATE", "AGING (DAYS)", "AGING BUCKET"};
            excelFileCreator.writeTableHeaderExcel("Outstanding Bills",
                    "Tax Revenue Appeals Board (TRAB)\nOutstanding Bills Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;

            for (OutstandingBillDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getControlNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getBillReference(), style);
                excelFileCreator.createCell(row, col++, dto.getAppType(), style);
                excelFileCreator.createCell(row, col++, dto.getPayerName(), style);
                excelFileCreator.createCell(row, col++, dto.getCurrency(), style);
                excelFileCreator.createCell(row, col++, dto.getBilledAmount(), style);
                excelFileCreator.createCell(row, col++, dto.getGeneratedDate() != null ? outputFormat.format(dto.getGeneratedDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getExpiryDate() != null ? outputFormat.format(dto.getExpiryDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getAgingDays(), style);
                excelFileCreator.createCell(row, col++, dto.getAgingBucket(), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "outstanding-bills-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating outstanding bills Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateRevenueSummaryExcel(List<RevenueSummaryDto> dtos,
                                                                        int grandTotalBills, int grandTotalPaid,
                                                                        BigDecimal grandTotalBilled, BigDecimal grandTotalCollected,
                                                                        double overallCollectionRate, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "CATEGORY (APP TYPE)", "BILLS", "PAID", "TOTAL BILLED", "TOTAL COLLECTED", "COLLECTION RATE (%)"};
            excelFileCreator.writeTableHeaderExcel("Revenue Summary",
                    "Tax Revenue Appeals Board (TRAB)\nRevenue Summary Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            int startRow = 2;
            int sn = 0;

            for (RevenueSummaryDto dto : dtos) {
                sn++;
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, sn, style);
                excelFileCreator.createCell(row, col++, dto.getAppType(), style);
                excelFileCreator.createCell(row, col++, dto.getBillCount(), style);
                excelFileCreator.createCell(row, col++, dto.getPaidCount(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalBilled(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalCollected(), style);
                excelFileCreator.createCell(row, col++, String.format("%.1f", dto.getCollectionRate()), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "revenue-summary-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating revenue summary Excel report", e);
            return buildErrorResponse();
        }
    }

    private Response<HtmlReportResponseDto> generateBillReconciliationExcel(List<BillReconciliationDto> dtos,
                                                                            BigDecimal totalBilled, BigDecimal totalPaid,
                                                                            BigDecimal totalVariance, int paidCount,
                                                                            int unpaidCount, int expiredCount, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "CONTROL NUMBER", "BILL REFERENCE", "APP TYPE", "PAYER NAME",
                    "CURRENCY", "BILLED AMOUNT", "PAID AMOUNT", "VARIANCE", "GENERATED DATE", "STATUS"};
            excelFileCreator.writeTableHeaderExcel("Bill Reconciliation",
                    "Tax Revenue Appeals Board (TRAB)\nBill Reconciliation Report\n" + dateRange, headers);

            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;

            for (BillReconciliationDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getControlNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getBillReference(), style);
                excelFileCreator.createCell(row, col++, dto.getAppType(), style);
                excelFileCreator.createCell(row, col++, dto.getPayerName(), style);
                excelFileCreator.createCell(row, col++, dto.getCurrency(), style);
                excelFileCreator.createCell(row, col++, dto.getBilledAmount(), style);
                excelFileCreator.createCell(row, col++, dto.getPaidAmount(), style);
                excelFileCreator.createCell(row, col++, dto.getVariance(), style);
                excelFileCreator.createCell(row, col++, dto.getGeneratedDate() != null ? outputFormat.format(dto.getGeneratedDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getStatus(), style);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()),
                    CONTENT_TYPE_EXCEL, "bill-reconciliation-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            logger.error("Error generating bill reconciliation Excel report", e);
            return buildErrorResponse();
        }
    }

    private String calculateBillAgingBucket(long agingDays) {
        if (agingDays <= 30) return "0-30 days";
        if (agingDays <= 60) return "31-60 days";
        if (agingDays <= 90) return "61-90 days";
        return "90+ days";
    }

    // ==================== NOTICE REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateNoticeReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = null, dateTo = null;
            if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) dateFrom = sdf.parse(filter.getDateFrom());
            if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) dateTo = sdf.parse(filter.getDateTo());

            List<Notice> notices;
            if (dateFrom != null && dateTo != null) {
                Pageable pageable = PageRequest.of(0, 100000);
                notices = noticeRepository.findAllByLoggedAtBetween(dateFrom, dateTo, pageable).getContent();
            } else {
                notices = new ArrayList<>();
                noticeRepository.findAll().forEach(notices::add);
            }

            if (notices.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<NoticeReportDto> dtos = new ArrayList<>();
            int paidCount = 0, unpaidCount = 0;
            int sn = 0;

            for (Notice notice : notices) {
                sn++;
                NoticeReportDto dto = new NoticeReportDto();
                dto.setSerialNumber(sn);
                dto.setNoticeNo(notice.getNoticeNo() != null ? notice.getNoticeNo() : "-");
                dto.setAppellantName(notice.getAppelantName() != null ? notice.getAppelantName() : "-");
                dto.setDescription(notice.getDes() != null ? notice.getDes() : "-");
                dto.setNoticeDate(notice.getLoggedAt());

                if (notice.getBillId() != null) {
                    dto.setControlNumber(notice.getBillId().getBillControlNumber() != null ? notice.getBillId().getBillControlNumber() : "-");
                    dto.setPaymentStatus(notice.getBillId().isBillPayed() ? "Paid" : "Unpaid");
                    if (notice.getBillId().isBillPayed()) paidCount++; else unpaidCount++;
                } else {
                    dto.setControlNumber("-");
                    dto.setPaymentStatus("No Bill");
                    unpaidCount++;
                }

                dtos.add(dto);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = (dateFrom != null && dateTo != null)
                    ? "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo) : "All Records";
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateNoticeExcel(dtos, dateRange, paidCount, unpaidCount);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("notices", dtos);
            variables.put("totalNotices", dtos.size());
            variables.put("paidCount", paidCount);
            variables.put("unpaidCount", unpaidCount);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/notice-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "notice-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating notice report", e);
            return buildErrorResponse();
        }
    }

    // ==================== SUMMONS REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateSummonsReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = null, dateTo = null;
            if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) dateFrom = sdf.parse(filter.getDateFrom());
            if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) dateTo = sdf.parse(filter.getDateTo());

            List<Summons> summonsList;
            if (dateFrom != null && dateTo != null) {
                summonsList = summonsRepository.findSummonsBySummonStartDateBetween(dateFrom, dateTo);
            } else {
                summonsList = summonsRepository.findAllByOrderBySummonStartDateDesc();
            }

            if (summonsList.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<SummonsReportDto> dtos = new ArrayList<>();
            int sn = 0;

            for (Summons s : summonsList) {
                sn++;
                SummonsReportDto dto = new SummonsReportDto();
                dto.setSerialNumber(sn);
                dto.setSummonNo(s.getSummonNo() != null ? s.getSummonNo() : "-");
                dto.setStartDate(s.getSummonStartDate());
                dto.setEndDate(s.getSummonEndDate());
                dto.setJudgeName(s.getJud() != null && s.getJud().getName() != null ? s.getJud().getName().toUpperCase() : (s.getJudge() != null ? s.getJudge().toUpperCase() : "-"));
                dto.setMemberOne(s.getMemberOne() != null ? s.getMemberOne() : "-");
                dto.setMemberTwo(s.getMemberTwo() != null ? s.getMemberTwo() : "-");
                dto.setVenue(s.getVenue() != null ? s.getVenue() : "-");
                dto.setTime(s.getTime() != null ? s.getTime() : "-");
                dto.setLinkedCases(s.getAppList() != null ? s.getAppList() : "-");
                dto.setSummonType(s.getSummonType() != null ? s.getSummonType() : "-");
                dtos.add(dto);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = (dateFrom != null && dateTo != null)
                    ? "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo) : "All Records";
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateSummonsExcel(dtos, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("summonsList", dtos);
            variables.put("totalSummons", dtos.size());
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/summons-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "summons-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating summons report", e);
            return buildErrorResponse();
        }
    }

    // ==================== APPLICATION REGISTER REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateApplicationReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = null, dateTo = null;
            if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) dateFrom = sdf.parse(filter.getDateFrom());
            if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) dateTo = sdf.parse(filter.getDateTo());

            List<ApplicationRegister> applications;
            if (dateFrom != null && dateTo != null) {
                Pageable pageable = PageRequest.of(0, 100000);
                applications = applicationRegisterRepository.findApplicationRegistersByDateOfFillingBetween(dateFrom, dateTo, pageable).getContent();
            } else {
                applications = new ArrayList<>();
                applicationRegisterRepository.findAll().forEach(applications::add);
            }

            if (applications.isEmpty()) {
                return buildNoRecordResponse();
            }

            List<ApplicationReportDto> dtos = new ArrayList<>();
            int sn = 0;

            for (ApplicationRegister app : applications) {
                sn++;
                ApplicationReportDto dto = new ApplicationReportDto();
                dto.setSerialNumber(sn);
                dto.setApplicationNo(app.getApplicationNo() != null ? app.getApplicationNo() : "-");
                dto.setApplicantName(app.getApplicant() != null ?
                        ((app.getApplicant().getFirstName() != null ? app.getApplicant().getFirstName() : "") + " " +
                         (app.getApplicant().getLastName() != null ? app.getApplicant().getLastName() : "")).trim() : "-");
                dto.setRespondentName(app.getRespondent() != null && app.getRespondent().getName() != null ? app.getRespondent().getName() : "COMM GENERAL");
                dto.setTaxType(app.getTaxes() != null ? app.getTaxes().getTaxName().toUpperCase() : "-");
                dto.setFilingDate(app.getDateOfFilling());
                dto.setDecisionDate(app.getDateOfDecision());
                dto.setProgressStatus(app.getStatusTrend() != null ? app.getStatusTrend().getApplicationStatusTrendName() : "-");
                dtos.add(dto);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = (dateFrom != null && dateTo != null)
                    ? "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo) : "All Records";
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateApplicationExcel(dtos, dateRange);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("applications", dtos);
            variables.put("totalApplications", dtos.size());
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/application-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "application-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating application report", e);
            return buildErrorResponse();
        }
    }

    // ==================== FINANCIAL YEAR COMPARISON REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateFinancialYearComparisonReport(ReportFilterDto filter) {
        try {
            List<Appeals> allAppeals = new ArrayList<>();
            appealsRepository.findAll().forEach(allAppeals::add);

            List<ApplicationRegister> allApplications = new ArrayList<>();
            applicationRegisterRepository.findAll().forEach(allApplications::add);

            if (allAppeals.isEmpty() && allApplications.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group appeals by financial year (from bill)
            Map<String, List<Appeals>> appealsByFY = allAppeals.stream()
                    .collect(Collectors.groupingBy(a -> {
                        if (a.getBillId() != null && a.getBillId().getFinancialYear() != null) {
                            return a.getBillId().getFinancialYear();
                        }
                        if (a.getDateOfFilling() != null) {
                            return deriveFY(a.getDateOfFilling());
                        }
                        return "UNKNOWN";
                    }));

            // Group applications by financial year (derived from filing date)
            Map<String, List<ApplicationRegister>> appsByFY = allApplications.stream()
                    .collect(Collectors.groupingBy(a -> {
                        if (a.getDateOfFilling() != null) {
                            return deriveFY(a.getDateOfFilling());
                        }
                        return "UNKNOWN";
                    }));

            Set<String> allFYs = new TreeSet<>(Comparator.reverseOrder());
            allFYs.addAll(appealsByFY.keySet());
            allFYs.addAll(appsByFY.keySet());

            List<FinancialYearComparisonDto> dtos = new ArrayList<>();
            int grandTotalAppeals = 0, grandTotalApplications = 0, grandTotalCases = 0;
            int grandTotalDecided = 0, grandTotalPending = 0;
            int sn = 0;

            for (String fy : allFYs) {
                sn++;
                FinancialYearComparisonDto dto = new FinancialYearComparisonDto();
                dto.setSerialNumber(sn);
                dto.setFinancialYear(fy);

                List<Appeals> fyAppeals = appealsByFY.getOrDefault(fy, Collections.emptyList());
                List<ApplicationRegister> fyApps = appsByFY.getOrDefault(fy, Collections.emptyList());

                int appealsCount = fyAppeals.size();
                int appsCount = fyApps.size();
                int decided = (int) fyAppeals.stream().filter(a -> a.getDecidedDate() != null).count();
                int decidedApps = (int) fyApps.stream().filter(a -> a.getDateOfDecision() != null).count();

                dto.setAppealsCount(appealsCount);
                dto.setApplicationsCount(appsCount);
                dto.setTotalCases(appealsCount + appsCount);
                dto.setDecidedCount(decided + decidedApps);
                dto.setPendingCount((appealsCount + appsCount) - (decided + decidedApps));

                dtos.add(dto);
                grandTotalAppeals += appealsCount;
                grandTotalApplications += appsCount;
                grandTotalCases += appealsCount + appsCount;
                grandTotalDecided += decided + decidedApps;
                grandTotalPending += (appealsCount + appsCount) - (decided + decidedApps);
            }

            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateFinancialYearExcel(dtos, grandTotalAppeals, grandTotalApplications, grandTotalCases, grandTotalDecided, grandTotalPending);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("comparisons", dtos);
            variables.put("totalYears", dtos.size());
            variables.put("grandTotalAppeals", grandTotalAppeals);
            variables.put("grandTotalApplications", grandTotalApplications);
            variables.put("grandTotalCases", grandTotalCases);
            variables.put("grandTotalDecided", grandTotalDecided);
            variables.put("grandTotalPending", grandTotalPending);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/financial-year-comparison-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "financial-year-comparison-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating financial year comparison report", e);
            return buildErrorResponse();
        }
    }

    // ==================== TOP APPELLANTS REPORT ====================

    @Override
    public Response<HtmlReportResponseDto> generateTopAppellantsReport(ReportFilterDto filter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateFrom = null, dateTo = null;
            if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) dateFrom = sdf.parse(filter.getDateFrom());
            if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) dateTo = sdf.parse(filter.getDateTo());

            List<Appeals> appeals;
            if (dateFrom != null && dateTo != null) {
                appeals = appealsRepository.findByDateOfFillingBetween(dateFrom, dateTo);
            } else {
                appeals = new ArrayList<>();
                appealsRepository.findAll().forEach(appeals::add);
            }

            if (appeals.isEmpty()) {
                return buildNoRecordResponse();
            }

            // Group by appellant name
            Map<String, List<Appeals>> byAppellant = appeals.stream()
                    .filter(a -> a.getAppellantName() != null && !a.getAppellantName().isEmpty())
                    .collect(Collectors.groupingBy(a -> a.getAppellantName().toUpperCase()));

            List<TopAppellantDto> dtos = new ArrayList<>();

            for (Map.Entry<String, List<Appeals>> entry : byAppellant.entrySet()) {
                TopAppellantDto dto = new TopAppellantDto();
                dto.setAppellantName(entry.getKey());

                List<Appeals> appellantAppeals = entry.getValue();
                dto.setTotalCases(appellantAppeals.size());

                int decided = 0, pending = 0;
                Set<String> taxTypes = new HashSet<>();
                BigDecimal totalTzs = BigDecimal.ZERO;

                for (Appeals a : appellantAppeals) {
                    if (a.getDecidedDate() != null) decided++; else pending++;
                    if (a.getTax() != null) taxTypes.add(a.getTax().getTaxName().toUpperCase());
                    if (a.getAppealAmount() != null) {
                        for (AppealAmount amt : a.getAppealAmount()) {
                            if (amt.getCurrency() != null && "TZS".equals(amt.getCurrency().getCurrencyShortName()) && amt.getAmountOnDispute() != null) {
                                totalTzs = totalTzs.add(amt.getAmountOnDispute());
                            }
                        }
                    }
                }

                dto.setDecidedCount(decided);
                dto.setPendingCount(pending);
                dto.setTaxTypes(String.join(", ", taxTypes));
                dto.setTotalAmountTzs(totalTzs);
                dtos.add(dto);
            }

            dtos.sort(Comparator.comparingInt(TopAppellantDto::getTotalCases).reversed());

            int rank = 0;
            int totalCases = 0;
            for (TopAppellantDto dto : dtos) {
                rank++;
                dto.setRank(rank);
                totalCases += dto.getTotalCases();
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dateRange = (dateFrom != null && dateTo != null)
                    ? "From " + displayFormat.format(dateFrom) + " To " + displayFormat.format(dateTo) : "All Records";
            String generatedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

            if ("excel".equalsIgnoreCase(filter.getFormat())) {
                return generateTopAppellantsExcel(dtos, dateRange, totalCases);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("dateRange", dateRange);
            variables.put("appellants", dtos);
            variables.put("totalAppellants", dtos.size());
            variables.put("totalCases", totalCases);
            variables.put("generatedDate", generatedDate);

            String html = renderTemplate("reports/top-appellants-report", variables);
            byte[] pdfBytes = renderHtmlToPdf(html);
            return buildSuccessResponse(Base64.getEncoder().encodeToString(pdfBytes),
                    CONTENT_TYPE_PDF, "top-appellants-report-" + System.currentTimeMillis() + ".pdf");

        } catch (Exception e) {
            logger.error("Error generating top appellants report", e);
            return buildErrorResponse();
        }
    }

    // ==================== OPERATIONAL REPORT EXCEL GENERATORS ====================

    private Response<HtmlReportResponseDto> generateNoticeExcel(List<NoticeReportDto> dtos, String dateRange, int paidCount, int unpaidCount) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "NOTICE NO", "APPELLANT NAME", "DESCRIPTION", "NOTICE DATE", "CONTROL NUMBER", "PAYMENT STATUS"};
            excelFileCreator.writeTableHeaderExcel("Notice Report", "Tax Revenue Appeals Board (TRAB)\nNotice Report\n" + dateRange, headers);
            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;
            for (NoticeReportDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getNoticeNo(), style);
                excelFileCreator.createCell(row, col++, dto.getAppellantName(), style);
                excelFileCreator.createCell(row, col++, dto.getDescription(), style);
                excelFileCreator.createCell(row, col++, dto.getNoticeDate() != null ? outputFormat.format(dto.getNoticeDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getControlNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getPaymentStatus(), style);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()), CONTENT_TYPE_EXCEL, "notice-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) { logger.error("Error generating notice Excel", e); return buildErrorResponse(); }
    }

    private Response<HtmlReportResponseDto> generateSummonsExcel(List<SummonsReportDto> dtos, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "SUMMON NO", "START DATE", "END DATE", "JUDGE", "MEMBER 1", "MEMBER 2", "VENUE", "TIME", "LINKED CASES", "TYPE"};
            excelFileCreator.writeTableHeaderExcel("Summons Report", "Tax Revenue Appeals Board (TRAB)\nSummons Report\n" + dateRange, headers);
            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;
            for (SummonsReportDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getSummonNo(), style);
                excelFileCreator.createCell(row, col++, dto.getStartDate() != null ? outputFormat.format(dto.getStartDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getEndDate() != null ? outputFormat.format(dto.getEndDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getJudgeName(), style);
                excelFileCreator.createCell(row, col++, dto.getMemberOne(), style);
                excelFileCreator.createCell(row, col++, dto.getMemberTwo(), style);
                excelFileCreator.createCell(row, col++, dto.getVenue(), style);
                excelFileCreator.createCell(row, col++, dto.getTime(), style);
                excelFileCreator.createCell(row, col++, dto.getLinkedCases(), style);
                excelFileCreator.createCell(row, col++, dto.getSummonType(), style);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()), CONTENT_TYPE_EXCEL, "summons-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) { logger.error("Error generating summons Excel", e); return buildErrorResponse(); }
    }

    private Response<HtmlReportResponseDto> generateApplicationExcel(List<ApplicationReportDto> dtos, String dateRange) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "APPLICATION NO", "APPLICANT", "RESPONDENT", "TAX TYPE", "FILING DATE", "DECISION DATE", "PROGRESS STATUS"};
            excelFileCreator.writeTableHeaderExcel("Application Report", "Tax Revenue Appeals Board (TRAB)\nApplication Register Report\n" + dateRange, headers);
            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            int startRow = 2;
            for (ApplicationReportDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getApplicationNo(), style);
                excelFileCreator.createCell(row, col++, dto.getApplicantName(), style);
                excelFileCreator.createCell(row, col++, dto.getRespondentName(), style);
                excelFileCreator.createCell(row, col++, dto.getTaxType(), style);
                excelFileCreator.createCell(row, col++, dto.getFilingDate() != null ? outputFormat.format(dto.getFilingDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getDecisionDate() != null ? outputFormat.format(dto.getDecisionDate()) : "-", style);
                excelFileCreator.createCell(row, col++, dto.getProgressStatus(), style);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()), CONTENT_TYPE_EXCEL, "application-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) { logger.error("Error generating application Excel", e); return buildErrorResponse(); }
    }

    private Response<HtmlReportResponseDto> generateFinancialYearExcel(List<FinancialYearComparisonDto> dtos,
                                                                       int grandTotalAppeals, int grandTotalApplications,
                                                                       int grandTotalCases, int grandTotalDecided, int grandTotalPending) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"S/N", "FINANCIAL YEAR", "APPEALS", "APPLICATIONS", "TOTAL CASES", "DECIDED", "PENDING"};
            excelFileCreator.writeTableHeaderExcel("FY Comparison", "Tax Revenue Appeals Board (TRAB)\nFinancial Year Comparison Report", headers);
            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            int startRow = 2;
            for (FinancialYearComparisonDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getSerialNumber(), style);
                excelFileCreator.createCell(row, col++, dto.getFinancialYear(), style);
                excelFileCreator.createCell(row, col++, dto.getAppealsCount(), style);
                excelFileCreator.createCell(row, col++, dto.getApplicationsCount(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalCases(), style);
                excelFileCreator.createCell(row, col++, dto.getDecidedCount(), style);
                excelFileCreator.createCell(row, col++, dto.getPendingCount(), style);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()), CONTENT_TYPE_EXCEL, "financial-year-comparison-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) { logger.error("Error generating FY comparison Excel", e); return buildErrorResponse(); }
    }

    private Response<HtmlReportResponseDto> generateTopAppellantsExcel(List<TopAppellantDto> dtos, String dateRange, int totalCases) {
        try {
            excelFileCreator.newReportExcel();
            String[] headers = {"RANK", "APPELLANT NAME", "TOTAL CASES", "PENDING", "DECIDED", "TAX TYPES", "TOTAL AMOUNT (TZS)"};
            excelFileCreator.writeTableHeaderExcel("Top Appellants", "Tax Revenue Appeals Board (TRAB)\nTop Appellants Report\n" + dateRange, headers);
            org.apache.poi.ss.usermodel.CellStyle style = excelFileCreator.getFontContentExcel();
            int startRow = 2;
            for (TopAppellantDto dto : dtos) {
                org.apache.poi.ss.usermodel.Row row = excelFileCreator.sheet.createRow(startRow++);
                int col = 0;
                excelFileCreator.createCell(row, col++, dto.getRank(), style);
                excelFileCreator.createCell(row, col++, dto.getAppellantName(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalCases(), style);
                excelFileCreator.createCell(row, col++, dto.getPendingCount(), style);
                excelFileCreator.createCell(row, col++, dto.getDecidedCount(), style);
                excelFileCreator.createCell(row, col++, dto.getTaxTypes(), style);
                excelFileCreator.createCell(row, col++, dto.getTotalAmountTzs(), style);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            excelFileCreator.workbook.write(baos);
            excelFileCreator.workbook.close();
            return buildSuccessResponse(Base64.getEncoder().encodeToString(baos.toByteArray()), CONTENT_TYPE_EXCEL, "top-appellants-report-" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) { logger.error("Error generating top appellants Excel", e); return buildErrorResponse(); }
    }

    private String deriveFY(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH); // 0-based
        int year = cal.get(Calendar.YEAR);
        if (month >= 6) { // July onwards = current/next FY
            return year + "/" + (year + 1);
        } else {
            return (year - 1) + "/" + year;
        }
    }

    // ==================== RESPONSE BUILDERS ====================

    private Response<HtmlReportResponseDto> buildSuccessResponse(String base64Content, String contentType, String fileName) {
        Response<HtmlReportResponseDto> response = new Response<>();
        response.setDescription("Success");
        response.setData(new HtmlReportResponseDto(base64Content, contentType, fileName));
        response.setCode(ResponseCode.SUCCESS);
        response.setStatus(true);
        return response;
    }

    private Response<HtmlReportResponseDto> buildNoRecordResponse() {
        Response<HtmlReportResponseDto> response = new Response<>();
        response.setData(null);
        response.setCode(ResponseCode.NO_RECORD_FOUND);
        response.setDescription("No Records Found");
        response.setStatus(true);
        return response;
    }

    private Response<HtmlReportResponseDto> buildErrorResponse() {
        Response<HtmlReportResponseDto> response = new Response<>();
        response.setData(null);
        response.setCode(ResponseCode.FAILURE);
        response.setDescription("Report generation failed");
        response.setStatus(false);
        return response;
    }
}
