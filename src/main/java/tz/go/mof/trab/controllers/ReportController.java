package tz.go.mof.trab.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.dto.bill.BillSummaryReportDto;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.dto.report.*;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.service.ReportsGeneratorService;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping("/api")
public class ReportController {


    public Response<String> response = new Response<String>();

    @Autowired
    private ReportsGeneratorService reportsGeneratorService;

    @Value("${tz.go.tarula.termis.report-path}")
    private String REPORT_DESIGN_PATH;

    @Value("${tz.go.tarula.termis.report-img}")
    private String REPORT_IMG;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private GlobalMethods globalMethods;

    @Autowired
    private BillItemRepository billItemRepository;

    @Autowired
    private AppealsRepository appealsRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ApplicationRegisterRepository applicationRegisterRepository;


    private static final Logger reportLogger = Logger.getLogger("trab.payment.request");

    Locale currentLocale = LocaleContextHolder.getLocale();

    @GetMapping("/format/{format}/payment/payment-id/{paymentId}")
    @ResponseBody
    public Response<String> paymentReceipt(@PathVariable("paymentId") String paymentId) {
        Payment payment = paymentRepository.findById(paymentId).get();
        ReportResponseDto reportResponseDto = new ReportResponseDto();
        try {

            if (payment == null) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("");
                response.setStatus(true);
                return response;
            }

            PaymentReportDto reportDto = globalMethods.getPaymentReportDto(payment);

            List<PaymentReportDto> paymentReport = new ArrayList<PaymentReportDto>();
            paymentReport.add(reportDto);

            File file = new File(REPORT_DESIGN_PATH + "payment_item_sub_report.jrxml");
            final JasperReport paymentSubreport = JasperCompileManager.compileReport(new FileInputStream(file));

            List<BillItems> billItems = billItemRepository.getBillItemOfTheSameBill(payment.getBill().getBillId());
            List<BillItemsReportDto> paymentItemsReportDtos = new ArrayList<>();

            billItems.forEach(item -> {
                BillItemsReportDto dto = new BillItemsReportDto();
                dto.setGovernmentFinancialStatisticsDescription(item.getBillItemDescription());
                dto.setItemBilledAmount(item.getBillItemAmount().toString());
                dto.setGovernmentFinancialStatisticsCode(item.getBillItemRef());
                paymentItemsReportDtos.add(dto);

            });


            JRDataSource reportDataSource = new JRBeanCollectionDataSource(paymentReport);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);
            parameters.put("paymentSubreport", paymentSubreport);
            parameters.put("billItems", paymentItemsReportDtos);

            File paymentReportTemplate = new File(REPORT_DESIGN_PATH + "payment.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(paymentReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, reportDataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, REPORT_DESIGN_PATH + "payment_receipt.pdf");


            File fileToRead = new File(REPORT_DESIGN_PATH + "payment_receipt.pdf");
            byte[] fileContent = FileUtils.readFileToByteArray(fileToRead);
            String contentToSend = Base64.getEncoder().encodeToString(fileContent);

            reportLogger.info("############  Genarating Report For ###########");

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;
        } catch (Exception e) {

            e.printStackTrace();
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
            return response;
        }
    }

    @GetMapping("/format/{format}/bill/bill-id/{billId}")
    @ResponseBody
    public Response<String> billInvoice(@PathVariable("format") String format, @PathVariable("billId") String billId) {

        // TODO Auto-generated method stub
        ReportResponseDto reportResponseDto = new ReportResponseDto();
        Bill bill = billRepository.selectOneBill(billId);
        try {

            BillReportDto reportDto = globalMethods.getBillReportDto(bill);
            if (bill == null) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("");
                response.setStatus(true);
                return response;
            }
            if (bill.getBillControlNumber().equalsIgnoreCase("0")) {
                System.out.println("######### inside controlnumber 0 ############");
                response.setData(null);
                response.setCode(ResponseCode.FAILURE);
                response.setDescription(getResponseCode((bill.getResponseCode())));
                response.setStatus(true);
                return response;
            }
            List<BillReportDto> billReport = new ArrayList<BillReportDto>();
            billReport.add(reportDto);

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            List<BillItems> billItems = billItemRepository.getBillItemOfTheSameBill(billId);
            List<BillItemsReportDto> billItemsReportDtos = new ArrayList<>();

            billItems.forEach(item -> {
                BillItemsReportDto dto = new BillItemsReportDto();
                dto.setGovernmentFinancialStatisticsDescription(item.getBillItemRef());
                dto.setItemBilledAmount(item.getBillItemAmount().toString());
                billItemsReportDtos.add(dto);

            });


            Map<String, List<BillItemsReportDto>> listOfBillItemsGrouped =
                    billItemsReportDtos.stream().collect(Collectors.groupingBy(w -> w.getGovernmentFinancialStatisticsDescription()));


            List<BillItemsReportDto> newBillItemsReportDtos = new ArrayList<>();
            listOfBillItemsGrouped.forEach((k,v) -> {
                        BillItemsReportDto dto = new BillItemsReportDto();
                        dto.setGovernmentFinancialStatisticsDescription(v.size() + " x " + v.get(0).getGovernmentFinancialStatisticsDescription());
                        dto.setItemBilledAmount(String.valueOf(v.size()*Double.valueOf(v.get(0).getItemBilledAmount())));
                        newBillItemsReportDtos.add(dto);
                     }
                    );


            System.out.println("######### list of bill items ############");
            TrabHelper.print(listOfBillItemsGrouped);



            File file = new File(REPORT_DESIGN_PATH + "bill_item_sub_report.jrxml");
            final JasperReport billSubreport = JasperCompileManager.compileReport(new FileInputStream(file));
            JRDataSource reportDataSource = new JRBeanCollectionDataSource(billReport);


            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("billSubreport", billSubreport);
            parameters.put("billItems", newBillItemsReportDtos);
            parameters.put("coat", REPORT_IMG);

            File billReportTemplate = new File(REPORT_DESIGN_PATH + "bill.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, reportDataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, REPORT_DESIGN_PATH + "bill_invoice.pdf");

            reportLogger.info("############  Genarating Report For ###########");

            File fileToRead = new File(REPORT_DESIGN_PATH + "bill_invoice.pdf");
            byte[] fileContent = FileUtils.readFileToByteArray(fileToRead);
            String contentToSend = Base64.getEncoder().encodeToString(fileContent);

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription(getResponseCode((bill.getResponseCode())));
            response.setStatus(true);
            return response;
        }
    }

    @PostMapping("/bills-dynamic/")
    @ResponseBody
    public ReportResponseDto bills(@RequestBody Map<String, String> request) throws JRException, IOException, ParseException {

        System.out.println("################" + request);
        // TODO Auto-generated method stub
        ReportResponseDto reportResponseDto = new ReportResponseDto();
        List<BillReportDto> billReportDtoList = new ArrayList<>();
        List<Bill> bills = globalMethods.findBills(request);

        bills.forEach(bill -> {
            BillReportDto reportDto = new BillReportDto();
            reportDto.setBillId(bill.getBillId().toString());
            reportDto.setControlNumber(bill.getBillControlNumber());
            reportDto.setTotalBilledAmount(bill.getBilledAmount().doubleValue());
            reportDto.setPayerName(bill.getPayerName());
            reportDto.setPayerPhone(bill.getPayerPhone());
            reportDto.setCcy(bill.getCurrency());
            reportDto.setBillExpiryDate(bill.getExpiryDate().toString());
            reportDto.setBillCreatedDate(bill.getGeneratedDate().toString());
            billReportDtoList.add(reportDto);
        });


        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(billReportDtoList);

        File file = new File(REPORT_DESIGN_PATH + "test.jrxml");
        final JasperReport billSubreport = JasperCompileManager.compileReport(new FileInputStream(file));

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ItemDataSource", itemsJRBean);
        parameters.put("coat", REPORT_IMG);


        JasperPrint jasperPrint = JasperFillManager.fillReport(REPORT_DESIGN_PATH + "test.jasper", parameters, new JREmptyDataSource());

        String outputFile = REPORT_DESIGN_PATH + "test.pdf";
        OutputStream outputStream = new FileOutputStream(new File(outputFile));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);


        File fileToRead = new File(REPORT_DESIGN_PATH + "test.pdf");
        byte[] fileContent = FileUtils.readFileToByteArray(fileToRead);
        String contentToSend = Base64.getEncoder().encodeToString(fileContent);

        System.out.println("######" + bills.size());

        if (bills.size() > 1) {
            reportResponseDto.setSuccess(true);
        } else {
            reportResponseDto.setSuccess(false);
        }

        reportResponseDto.setMessage(contentToSend);

        return reportResponseDto;


    }


    @PostMapping("/applications-dynamic/format/{format}")
    @ResponseBody
    public Response<String> applications(@RequestBody Map<String, String> request, @PathVariable("format") String format) {
        System.out.println("################" + request);

        String dateFrom = "";

        try {
            // TODO Auto-generated method stub
            ReportResponseDto reportResponseDto = new ReportResponseDto();
            List<ApplicationRegister> applicationRegisters = globalMethods.getApplications(request);
            reportResponseDto.setApplicationRegisters(applicationRegisters);


            if (!(applicationRegisters.size() > 0)) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("No Records Found");
                response.setStatus(true);
                return response;
            } else {


                List<ApplicationDto> applicationDtos = new ArrayList<ApplicationDto>();
                applicationRegisters.forEach(app -> {
                    ApplicationDto applicationDto = new ApplicationDto();
                    applicationDto.setApplicationNo(app.getApplicationNo());
                    applicationDto.setAppeleantName(app.getApplicant().getFirstName());
                    applicationDto.setRespondent(app.getRespondent().getName());
                    applicationDto.setDateOfDecision(app.getDateOfDecision());
                    applicationDto.setDateOfFilling(app.getDateOfFilling());
                    applicationDto.setTax(app.getTaxes().getTaxName());
                    applicationDto.setDecidedBy(app.getDecideBy());
                    applicationDto.setDecision(app.getStatusTrend().getApplicationStatusTrendName());
                    applicationDto.setStatus(app.getStatusTrend().getApplicationStatusTrendName());
                    applicationDtos.add(applicationDto);
                });
                if (!request.get("dateFrom").isEmpty()) {
                    dateFrom = "Date From: " + request.get("dateFrom");
                }
                if (!request.get("dateTo").isEmpty()) {
                    dateFrom = "Date To: " + request.get("dateFrom");
                }
                if (!request.get("dateFrom").isEmpty() && !request.get("dateTo").isEmpty()) {
                    dateFrom = "";
                    dateFrom = "Date From: " + request.get("dateFrom") + " Date To: " + request.get("dateTo");
                }

                //getting report and print
                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(applicationDtos);
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("coat", REPORT_IMG);
                parameters.put("dateFrom", dateFrom);
                File billReportTemplate = new File(REPORT_DESIGN_PATH + "applications_trab.jrxml");


                JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, itemsJRBean);

                String contentToSend = "";
                if (format.equals("pdf")) {
                    byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                    contentToSend = Base64.getEncoder().encodeToString(fileContent);
                } else {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "applications2.xlsx");

                    exporter.exportReport();
                    File excellFile;
                    excellFile = new File(REPORT_DESIGN_PATH + "applications2.xlsx");


                    byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                    contentToSend = Base64.getEncoder().encodeToString(fileContent);
                }

                response.setDescription("Success");
                response.setData(contentToSend);
                response.setCode(ResponseCode.SUCCESS);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
            return response;
        }
    }


    @PostMapping("/notices-dynamic/format/{format}")
    @ResponseBody
    public Response<String> notices(@RequestBody Map<String, String> request, @PathVariable("format") String format) {
        System.out.println("################" + request);

        String dateFrom = "";

        try {
            // TODO Auto-generated method stub
            List<Notice> notices = globalMethods.getNotices(request);
            List<NoticeDto> noticeDtos = new ArrayList<NoticeDto>();
            if (!(notices.size() > 0)) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("No Records Found");
                response.setStatus(true);
                return response;
            } else {

                if (!request.get("dateFrom").isEmpty()) {
                    dateFrom = "Date From: " + request.get("dateFrom");
                }
                if (!request.get("dateTo").isEmpty()) {
                    dateFrom = "Date To: " + request.get("dateFrom");
                }
                if (!request.get("dateFrom").isEmpty() && !request.get("dateTo").isEmpty()) {
                    dateFrom = "";
                    dateFrom = "Date From: " + request.get("dateFrom") + " Date To: " + request.get("dateTo");
                }

                notices.forEach(notice -> {
                    NoticeDto noticeDto = new NoticeDto();
                    noticeDto.setNoticeNo(notice.getNoticeNo());
                    noticeDto.setAppealantName(notice.getAppelantName());
                    noticeDto.setAppealantTin(notice.getAppelantName());
                    noticeDto.setLoggedAt(notice.getLoggedAt());
                    noticeDto.setAdress(notice.getAdressId().getSlp() + notice.getAdressId().getRegion().getName());
                    noticeDto.setCreatedBy(notice.getSystemUser().getCreatedBy());
                    noticeDtos.add(noticeDto);

                });

                //getting report and print
                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(noticeDtos);
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("coat", REPORT_IMG);
                parameters.put("dateFrom", dateFrom);
                File billReportTemplate = new File(REPORT_DESIGN_PATH + "notices.jrxml");


                JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, itemsJRBean);

                String contentToSend = "";
                if (format.equals("pdf")) {
                    byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                    contentToSend = Base64.getEncoder().encodeToString(fileContent);
                } else {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "notices.xlsx");

                    exporter.exportReport();
                    File excellFile;
                    excellFile = new File(REPORT_DESIGN_PATH + "notices.xlsx");


                    byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                    contentToSend = Base64.getEncoder().encodeToString(fileContent);
                }

                response.setDescription("Success");
                response.setData(contentToSend);
                response.setCode(ResponseCode.SUCCESS);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
            return response;
        }
    }


    @PostMapping("/appeals-dynamic/format/{format}")
    @ResponseBody
    public Response<String> appeals(@RequestBody Map<String, String> request, @PathVariable("format") String format) {


        String dateFrom = "";
        String  details = "";

        try {
            List<AppealDto> appealDtos = new ArrayList<AppealDto>();
            System.out.println("################" + request);
            // TODO Auto-generated method stub
            ReportResponseDto reportResponseDto = new ReportResponseDto();

            List<Appeals> appeals;

            if (request.get("isTribunal").equals("YES")) {
                appeals = appealsRepository.findByIsFilledTratTrue();
            } else {
                appeals = globalMethods.getAppeals(request);
            }

            reportResponseDto.setAppeals(appeals);

            if (!(appeals.size() > 0)) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("No Records Found");
                response.setStatus(true);
                return response;
            } else {


                BigDecimal sumAmountInTzs = new BigDecimal("0.00");
                BigDecimal sumAmountInUsd = new BigDecimal("0.00");


                appeals.forEach(app -> {

                    AppealDto appealDto = new AppealDto();
                    appealDto.setAppealant(app.getAppellantName());
                    appealDto.setRespondent("COMM GENERAL");
                    appealDto.setAppealNo(app.getAppealNo());
                    appealDto.setDateOfFilling(app.getDateOfFilling());
                    appealDto.setDecidedBy(app.getSummons() != null ? app.getSummons().getJudge() : "NONE");
                    appealDto.setNatureOfAppeal(app.getNatureOfAppeal());
                    appealDto.setDecidedDate(app.getDecidedDate());
                    appealDto.setTax(app.getTax().getTaxName());
                    appealDto.setRemarks(app.getRemarks().toUpperCase());
                    appealDto.setAmountDetails("");
                    AppealAmount usdAmt = app.getAppealAmount().stream().filter(x -> x.getCurrency().getCurrencyShortName().
                            equals("USD")).findAny().isPresent() ? app.getAppealAmount().stream().filter(x -> x.getCurrencyName().
                            equals("USD")).findAny().get() : null;
                    AppealAmount tzsmt = app.getAppealAmount().stream().filter(x -> x.getCurrency().getCurrencyShortName().
                            equals("TZS")).findAny().isPresent() ? app.getAppealAmount().stream().filter(x -> x.getCurrencyName().
                            equals("TZS")).findAny().get() : null;

                    appealDto.setUsd(usdAmt != null ? usdAmt.getAmountOnDispute() : new BigDecimal("0.00"));
                    appealDto.setTzs(tzsmt != null ? tzsmt.getAmountOnDispute() : new BigDecimal("0.00"));
                    appealDto.setFindings(app.getStatusTrend().getAppealStatusTrendName());

                    sumAmountInTzs.add(usdAmt != null ? usdAmt.getAmountOnDispute() : new BigDecimal("0.00"));
                    sumAmountInUsd.add(tzsmt != null ? tzsmt.getAmountOnDispute() : new BigDecimal("0.00"));

                    appealDtos.add(appealDto);
                });


                System.out.println("tzs: " + sumAmountInTzs);
                System.out.println("usd: " + sumAmountInUsd);


                if (!request.get("dateFrom").isEmpty()) {
                    dateFrom = "Date From: " + request.get("dateFrom");
                }
                if (!request.get("dateTo").isEmpty()) {
                    dateFrom = "Date To: " + request.get("dateFrom");
                }
                if (!request.get("dateFrom").isEmpty() && !request.get("dateTo").isEmpty()) {
                    dateFrom = "";
                    dateFrom = "Date From: " + request.get("dateFrom") + " Date To: " + request.get("dateTo");
                }

                details = "Total Amount: " + sumAmountInTzs.toString() + " TZS "  + sumAmountInUsd.toString() + " USD";

                //getting report and print
                JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(appealDtos);
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("coat", REPORT_IMG);
                parameters.put("dateFrom", dateFrom);
               // parameters.put("details", details);
                File billReportTemplate = new File(REPORT_DESIGN_PATH + "appeals2.jrxml");


                JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, itemsJRBean);

                String contentToSend = "";
                if (format.equals("pdf")) {
                    byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                    contentToSend = Base64.getEncoder().encodeToString(fileContent);
                } else {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "appeals2.xlsx");
                    exporter.exportReport();
                    File excellFile;
                    excellFile = new File(REPORT_DESIGN_PATH + "appeals2.xlsx");


                    byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                    contentToSend = Base64.getEncoder().encodeToString(fileContent);
                }
                response.setDescription("Success");
                response.setData(contentToSend);
                response.setCode(ResponseCode.SUCCESS);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
            return response;
        }
    }


    @RequestMapping(value = "/format/{format}/payments/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<String> getPaymentsReports(@PathVariable("format") String reportFormat,
                                               @RequestBody PaymentSearchDto paymentSearchDto)
            throws JRException, IOException {
        return reportsGeneratorService.getPaymentsReports(reportFormat, paymentSearchDto);
    }

    @RequestMapping(value = "/format/{format}/payments-summary/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<String> getPaymentSummaryReport(@PathVariable("format") String reportFormat,
                                                    @RequestBody PaymentSearchDto paymentSearchDto)
            throws JRException, IOException {
        return reportsGeneratorService.getPaymentSummary(reportFormat, paymentSearchDto);
    }


    @RequestMapping(value = "/format/{format}/bill-summary/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<String> getBillSummary(@PathVariable("format") String reportFormat,
                                           @RequestBody BillSummaryReportDto billSummaryReportDto)
            throws JRException, IOException {
        return reportsGeneratorService.getBillSummary(reportFormat, billSummaryReportDto, true);
    }


    @RequestMapping(value = "/format/{format}/bill-summary-amount/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<String> getBillSummaryAmount(@PathVariable("format") String reportFormat,
                                                 @RequestBody BillSummaryReportDto billSummaryReportDto)
            throws JRException, IOException {
        return reportsGeneratorService.getBillSummary(reportFormat, billSummaryReportDto, false);
    }


    @RequestMapping(value = "/format/{format}/defaulter-report/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<String> getDefaulters(@PathVariable("format") String reportFormat,
                                          @RequestBody BillSearchDto billSearchDto)
            throws JRException, IOException {

        return reportsGeneratorService.getDefaulters(reportFormat, billSearchDto);
    }

    @RequestMapping(value = "/format/{format}/cause-listed/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<String> getCauseListed(@PathVariable("format") String reportFormat,
                                           @RequestBody BillSearchDto billSearchDto)
            throws JRException, IOException {

        return reportsGeneratorService.getCauseListed(reportFormat, billSearchDto);
    }

    @RequestMapping(value = "/format/{format}/summons/{id}/type/{type}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Response<String> getSummons(@PathVariable("format") String reportFormat,
                                       @PathVariable("id") Long id, @PathVariable("type") Boolean isRespondent)
            throws JRException, IOException {
        SummonDto summonDto = new SummonDto();
        return reportsGeneratorService.getSummons(reportFormat, id, isRespondent);
    }

    @RequestMapping(value = "/format/{format}/attach/{name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Response<String> getFile(@PathVariable("format") String reportFormat,
                                    @PathVariable("name") String name)
            throws JRException, IOException {
        SummonDto summonDto = new SummonDto();
        return reportsGeneratorService.getPath(reportFormat, name);
    }


    @RequestMapping(value = "/notices-dates", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Page<Notice> getNoticesBetweenDates(@RequestBody Map<String, String> req) throws ParseException {


        Pageable paging = PageRequest.of(0, 10000, Sort.by("noticeId").descending());
        SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
       return noticeRepository.findAllByLoggedAtBetween(dmyFormat.parse(req.get("dateFrom")),  dmyFormat.parse(req.get("dateTo")), paging);


    }

    @RequestMapping(value = "/applications-dates", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Page<ApplicationRegister>  getApplicationsBetweenDates(@RequestBody Map<String, String> req) throws ParseException {


        Pageable paging = PageRequest.of(0, 10000, Sort.by("applicationId").descending());
        SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
        return applicationRegisterRepository.findApplicationRegistersByDateOfFillingBetween(dmyFormat.parse(req.get("dateFrom")),  dmyFormat.parse(req.get("dateTo")), paging);


    }

    public String getResponseCode(String txtCode){
        String responseCode[] = txtCode.split(";");
        String fullResponse = "";

        for (String code : responseCode) {
            if (fullResponse.isEmpty()) {
                fullResponse = messageSource.getMessage(code, null, currentLocale);
            } else {
                fullResponse = fullResponse + "\n" + messageSource.getMessage(code, null, currentLocale);
            }
        }
        return fullResponse;
    }

}
