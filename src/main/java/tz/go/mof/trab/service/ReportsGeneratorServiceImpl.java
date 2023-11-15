package tz.go.mof.trab.service;


import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import tz.go.mof.trab.controllers.SummonsController;
import tz.go.mof.trab.dto.bill.*;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.dto.payment.PaymentSummaryDto;
import tz.go.mof.trab.dto.report.SummonDto;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.BillSummary;
import tz.go.mof.trab.models.Payment;
import tz.go.mof.trab.models.Summons;
import tz.go.mof.trab.repositories.AppealsRepository;
import tz.go.mof.trab.repositories.ApplicationRegisterRepository;
import tz.go.mof.trab.repositories.SummonsRepository;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;


@Service
public class ReportsGeneratorServiceImpl implements ReportsGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(ReportsGeneratorServiceImpl.class);

    public Response<String> response = new Response<String>();

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BillService billService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SummonsRepository summonsRepository;

    @Autowired
    private AppealsRepository appealsRepository;

    @Autowired
    private SummonsService summonsService;

    @Autowired
    private BillItemService billItemService;

    @Autowired
    private GlobalMethods globalMethods;

    @Autowired
    private ApplicationRegisterRepository applicationRegisterRepository;

    @Value("${tz.go.tarula.termis.report-path}")
    private String REPORT_DESIGN_PATH;


    @Value("${tz.go.tarula.termis.report-img}")
    private String REPORT_IMG;

    @Value("${tz.go.trab.upload.dir}")
    private String uploadingDir;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public Response<String> getPaymentsReports(String reportFormat, PaymentSearchDto paymentSearchDto) {
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");


            List<Payment> paymentList = new ArrayList<Payment>();
            paymentList = paymentService.searchPayments(paymentSearchDto);
            String details = "";
            String dateFrom = "";

            if (paymentList.size() < 1) {

                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Record(s) Not Found");
                response.setStatus(false);

                return response;
            }

            if (paymentSearchDto.getDateTo().isEmpty() && !paymentSearchDto.getDateFrom().isEmpty()) {
                Date dFrom = format.parse(paymentSearchDto.getDateFrom());
                dateFrom = "Generated From: " + DateFor.format(dFrom);
            }


            if (paymentSearchDto.getDateFrom().isEmpty() && !paymentSearchDto.getDateTo().isEmpty()) {
                Date dTo = format.parse(paymentSearchDto.getDateTo());
                dateFrom = "Generated To: " + DateFor.format(dTo);
            }

            if (!paymentSearchDto.getDateFrom().isEmpty() && !paymentSearchDto.getDateTo().isEmpty()) {
                Date dFrom = format.parse(paymentSearchDto.getDateFrom());
                Date dTo = format.parse(paymentSearchDto.getDateTo());
                dateFrom = "Generated From: " + DateFor.format(dFrom) + " To: " + DateFor.format(dTo);
            }

            if (!paymentSearchDto.getRegionCode().isEmpty()) {
                details = "Region: " + paymentSearchDto.getRegionCode().split("-")[1];
            }

            if (!paymentSearchDto.getCouncilCode().isEmpty()) {
                details = "Region: " + paymentSearchDto.getRegionCode().split("-")[1] + " - Council: " +
                        paymentSearchDto.getCouncilCode().split("-")[1];
            }


            if(!paymentSearchDto.getType().isEmpty()){
                details = "Payment For: " + paymentSearchDto.getType().toUpperCase();
            }


            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(paymentList);

            File FileName = new File(".");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);
            parameters.put("details", details);
            parameters.put("dateFrom", dateFrom);


            File billReportTemplate = new File(REPORT_DESIGN_PATH + "reconFileReportLandscape.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);


            String contentToSend = "";
            if (reportFormat.equals("pdf")) {

                logger.info("pdf");
                byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            } else {

                logger.info("xls");
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "payment_summary.xlsx");
                exporter.exportReport();
                File excellFile = new File(REPORT_DESIGN_PATH + "payment_summary.xlsx");
                byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            }

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("############### Exception Generation Payment Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }


        return response;
    }

    @Override
    public Response<String> getPaymentSummary(String reportFormat, PaymentSearchDto paymentSearchDto) throws JRException, IOException {
        try {

            logger.info("####  payment search dtos #### " + paymentSearchDto);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");

            List<PaymentSummaryDto> paymentList = new ArrayList<PaymentSummaryDto>();
            paymentList = paymentService.searchPaymentSummary(paymentSearchDto);

            String details = "";
            String dateFrom = "";

            if (paymentList.size() < 1) {

                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Record(s) Not Found");
                response.setStatus(false);

                return response;
            }

            if (paymentSearchDto.getDateTo().isEmpty() && !paymentSearchDto.getDateFrom().isEmpty()) {
                Date dFrom = format.parse(paymentSearchDto.getDateFrom());
                dateFrom = "Generated From: " + DateFor.format(dFrom);
            }


            if (paymentSearchDto.getDateFrom().isEmpty() && !paymentSearchDto.getDateTo().isEmpty()) {
                Date dTo = format.parse(paymentSearchDto.getDateTo());
                dateFrom = "Generated To: " + DateFor.format(dTo);
            }

            if (!paymentSearchDto.getDateFrom().isEmpty() && !paymentSearchDto.getDateTo().isEmpty()) {
                Date dFrom = format.parse(paymentSearchDto.getDateFrom());
                Date dTo = format.parse(paymentSearchDto.getDateTo());
                dateFrom = "Generated From: " + DateFor.format(dFrom) + " To: " + DateFor.format(dTo);
            }


            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(paymentList);

            File FileName = new File(".");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);
            parameters.put("details", details);
            parameters.put("dateFrom", dateFrom);


            File billReportTemplate = new File(REPORT_DESIGN_PATH + "cash_book_summary_trab.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);


            String contentToSend = "";
            if (reportFormat.equals("pdf")) {

                logger.info("pdf");
                byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            } else {

                logger.info("xls");
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "cash_book_summary_trab.xlsx");
                exporter.exportReport();
                File excellFile = new File(REPORT_DESIGN_PATH + "cash_book_summary_trab.xlsx");
                byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            }


            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("############### Exception Generation Payment Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }


        return response;
    }


    @Override
    public Response<String> getBillSummary(String format, BillSummaryReportDto billSummaryReportDto, boolean isCount) {

        try {

            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");

            List<BillSummary> billSummaries = billService.getBIllSummary(billSummaryReportDto, isCount);

            String details = "";
            String dateFrom = "";

            if (billSummaries.size() < 1) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Record(s) Not Found");
                response.setStatus(true);
                return response;
            }

            if (billSummaryReportDto.getDateTo().isEmpty() && !billSummaryReportDto.getDateFrom().isEmpty()) {
                Date dFrom = dFormat.parse(billSummaryReportDto.getDateFrom());
                dateFrom = "Generated From: " + DateFor.format(dFrom);
            }


            if (billSummaryReportDto.getDateFrom().isEmpty() && !billSummaryReportDto.getDateTo().isEmpty()) {
                Date dTo = dFormat.parse(billSummaryReportDto.getDateTo());
                dateFrom = "Generated To: " + DateFor.format(dTo);
            }

            if (!billSummaryReportDto.getDateFrom().isEmpty() && !billSummaryReportDto.getDateTo().isEmpty()) {
                Date dFrom = dFormat.parse(billSummaryReportDto.getDateFrom());
                Date dTo = dFormat.parse(billSummaryReportDto.getDateTo());
                dateFrom = "Generated From: " + DateFor.format(dFrom) + " To: " + DateFor.format(dTo);
            }

            if (!billSummaryReportDto.getRegionCode().isEmpty()) {
                details = "Region: " + billSummaryReportDto.getRegionCode().split("-")[1];
            }

            if (!billSummaryReportDto.getCouncilCode().isEmpty()) {
                details = "Region: " + billSummaryReportDto.getRegionCode().split("-")[1] + " - Council: " +
                        billSummaryReportDto.getCouncilCode().split("-")[1];
            }


            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(billSummaries);

            File FileName = new File(".");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);
            parameters.put("details", details);
            parameters.put("dateFrom", dateFrom);


            File billReportTemplate;
            if (isCount) {
                billReportTemplate = new File(REPORT_DESIGN_PATH + "bill_summary.jrxml");
            } else {
                billReportTemplate = new File(REPORT_DESIGN_PATH + "bill_summary_amount.jrxml");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);


            String contentToSend = "";
            if (format.equals("pdf")) {
                byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            } else {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                if (isCount) {
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "bill_summary.xlsx");
                } else {
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "bill_summary_amount.xlsx");
                }
                exporter.exportReport();
                File excellFile;
                if (isCount) {
                    excellFile = new File(REPORT_DESIGN_PATH + "bill_summary.xlsx");
                } else {
                    excellFile = new File(REPORT_DESIGN_PATH + "bill_summary_amount.xlsx");
                }

                byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            }

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;
        } catch (Exception e) {

            e.printStackTrace();
            logger.error("############### Exception Generation  Bill Summary Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }
        return response;
    }

    @Override
    public Response<String> getBillSummaryAmount(String format, BillSummaryReportDto billSummaryReportDto) throws JRException, IOException {
        try {

            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");


        } catch (Exception e) {
            e.printStackTrace();
            logger.error("############### Exception Generation  Bill Summary Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }
        return response;
    }


    @Override
    public Response<String> getDefaulters(String format, BillSearchDto billSearchDto) throws JRException, IOException {

        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");

            List<Bill> billList = billService.searchBills(0, 0, billSearchDto);

            System.out.println("bills from search: " + billList);

            if (billList.size() < 1) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Record(s) Not Found");
                response.setStatus(true);
                return response;
            }

            Double totalBillsAmount = billList.stream().mapToDouble(o -> o.getBilledAmount().doubleValue()).sum();
            Double totalPaidAMount = billList.stream().mapToDouble(o -> o.getPaidAmount().doubleValue()).sum();
            Double pendingAmount = totalBillsAmount - totalPaidAMount;

            String details = "";
            String dateFrom = "";
            String sourceName = "";


            if (billSearchDto.getDateTo().isEmpty() && !billSearchDto.getDateFrom().isEmpty()) {
                Date dFrom = dFormat.parse(billSearchDto.getDateFrom());
                dateFrom = "Generated From: " + DateFor.format(dFrom);
            }


            if (billSearchDto.getDateFrom().isEmpty() && !billSearchDto.getDateTo().isEmpty()) {
                Date dTo = dFormat.parse(billSearchDto.getDateTo());
                dateFrom = "Generated To: " + DateFor.format(dTo);
            }

            if (!billSearchDto.getDateFrom().isEmpty() && !billSearchDto.getDateTo().isEmpty()) {
                Date dFrom = dFormat.parse(billSearchDto.getDateFrom());
                Date dTo = dFormat.parse(billSearchDto.getDateTo());
                dateFrom = "Generated From: " + DateFor.format(dFrom) + " To: " + DateFor.format(dTo);
            }


            if (!billSearchDto.getSourceId().isEmpty()) {
                sourceName = " Source: " + billSearchDto.getSourceId().split("-")[1];
            }

            if (!billSearchDto.getRegionCode().isEmpty()) {
                details = " Region: " + billSearchDto.getRegionCode().split("-")[1];
            }

            if (!billSearchDto.getCouncilCode().isEmpty()) {
                details = " Region: " + billSearchDto.getRegionCode().split("-")[1] + " - Council: " +
                        billSearchDto.getCouncilCode().split("-")[1];
            }


            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(billList);

            File FileName = new File(".");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);
            parameters.put("details", sourceName + details);
            parameters.put("dateFrom", dateFrom);


            File billReportTemplate = new File(REPORT_DESIGN_PATH + "bill_defaulters.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

            String contentToSend = "";
            if (format.equals("pdf")) {
                byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            } else {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "bill_defaulter.xlsx");
                exporter.exportReport();
                File excellFile = new File(REPORT_DESIGN_PATH + "bill_defaulter.xlsx");
                byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            }

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("############### Exception Generation  Bill defaulters Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }

        return response;
    }

    @Override
    public Response<String> getSummons(String format, Long id, Boolean isRespondent) throws JRException, IOException {
        try {
            Summons summons = summonsRepository.findById(id).get();

            logger.info("######### Summons ########" + summons.toString());
            List<SummonDto> list = new ArrayList<SummonDto>();
            SummonDto summonDto1 = new SummonDto();

            DateFormat Date = DateFormat.getDateInstance();
            Calendar cals = Calendar.getInstance();
            System.out.println("The original Date: " + cals.getTime());
            String currentDate = Date.format(cals.getTime());



            summonDto1.setRespondent(summons.getRespondent().toUpperCase());
            summonDto1.setAppeleant(summons.getAppeleant().toUpperCase());
            summonDto1.setVenue(summons.getVenue());
            summonDto1.setStartDate(Date.format(summons.getSummonStartDate()));
            summonDto1.setEndDate(Date.format(summons.getSummonEndDate()));
            summonDto1.setType(summons.getSummonType().equals("APPEAL")?"APPELLANT":"APPLICANT");
            summonDto1.setTypeInside(summons.getSummonType().equals("APPEAL")?"appeals":"applications");
            summonDto1.setTime(summons.getTime());
            summonDto1.setTodaysDate(currentDate);
            summonDto1.setDrawnBy(summons.getDrawnBy());




            String afterRemoving = summons.getAppList().substring(1).replace(" ", "");

            System.out.println(afterRemoving);
            String removing[] = afterRemoving.split(",");

            TrabHelper.print(removing);

            String appList = "";
            if(summons.getSummonType().equals("APPEAL")) {
                appList = "APPEALS NO: ";
            }else{
                appList = "APPLICATION NO: ";
            }
            String year = "";

            int i = 0;
            for(String removed: removing){
                i++;
                if(summons.getSummonType().equals("APPEAL")) {
                    if (!removed.isEmpty()) {
                        String appealNo = appealsRepository.findById(Long.valueOf(removed)).get().getAppealNo();
                        System.out.println(appealNo);
                        year = appealNo.split("/")[1];
                        String connector = i == removing.length ? "" : " & ";
                        appList = appList + appealNo.split("/")[0].split("\\.")[1] + connector;
                    }
                }else{
                    if (!removed.isEmpty()) {
                        String appealNo = applicationRegisterRepository.findById(Long.valueOf(removed)).get().getApplicationNo();
                        System.out.println(appealNo);
                        year = appealNo.split("/")[1];
                        String connector = i == removing.length ? "" : " & ";
                        appList = appList + appealNo.split("/")[0].split("\\.")[1] + connector;
                    }
                }
            }

            summonDto1.setAppealList(appList + " OF YEAR " + year);
            if(summons.getSummonType().equals("APPEAL") && isRespondent) {
                summonDto1.setName("COMMISIONER GENERAL");
                summonDto1.setAdress("P.O BOX 11491" + "\n" + "DAR-ES-SALAAM"  + "\n"+ "TANZANIA");
            }

            if(summons.getSummonType().equals("APPEAL") && !isRespondent) {
                summonDto1.setName(summons.getDrawnBy());
                summonDto1.setAdress(summons.getAppeleantAdress() + "\n"  + "TANZANIA");
            }

            if(summons.getSummonType().equals("APPLICATION")){
                if(isRespondent){
                    summonDto1.setName(summons.getRespondent());
                    summonDto1.setAdress(summons.getRespondentAdress().replace(", ", "\n"));
                }else{
                    summonDto1.setName(summons.getDrawnBy());
                    summonDto1.setAdress(summons.getAppeleantAdress().replace(", ", "\n"));
                }

            }

            list.add(summonDto1);
            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(list);

            File FileName = new File(".");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);

            File summonsTemplate = new File(REPORT_DESIGN_PATH + "summo.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(summonsTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

            String contentToSend = "";
            if (format.equals("pdf")) {
                byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            } else {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "app_summon.xlsx");
                exporter.exportReport();
                File excellFile = new File(REPORT_DESIGN_PATH + "app_summon.xlsx");
                byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            }

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("############### Exception Generation  Bill defaulters Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }
        return response;
    }

    @Override
    public Response<String> getPath(String format, String id)  {
        String storagePath = uploadingDir;
        System.out.println("storage: " + storagePath);
        System.out.println("filename: " + id);


        File file = new File(storagePath, id);
        Response<String> fileResponse = new Response<String>();
        if (file.exists()) {
            try {
                byte[] fileContent = FileUtils.readFileToByteArray(file);
                String contentToSend = Base64.getEncoder().encodeToString(fileContent);
                fileResponse.setData(contentToSend);
                fileResponse.setCode(ResponseCode.SUCCESS);
                fileResponse.setDescription("Attached File");
                fileResponse.setStatus(true);
                System.out.println("There file {} was encoded and returned succesifuly  ");
                return fileResponse;

            } catch (Exception e) {
                logger.error("fail to encode the file {}  ", id);
                fileResponse.setCode(ResponseCode.FAILURE);
                fileResponse.setStatus(false);
                fileResponse.setDescription("Failed to read File");
                return fileResponse;
            }
        } else {
            System.out.println("The file wit name {} does not exist ");
            fileResponse.setCode(ResponseCode.NO_RECORD_FOUND);
            fileResponse.setStatus(false);
            fileResponse.setDescription("No file attached for Assessment Report");
            return fileResponse;
        }
    }

    @Override
    public Response<String> getCauseListed(String reportFormat, BillSearchDto billSearchDto) {

        logger.info("###### Inside Searching Cause List ######");


        try {
            String details = "";
            String dateFrom = "";

            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFor = new SimpleDateFormat("dd MMMM yyyy");

            List<SummonDto> summonDtoList = new ArrayList<SummonDto>();
            List<Summons> summonsList = summonsService.searchSummons(billSearchDto);

            if (summonsList.size() < 1) {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Record(s) Not Found");
                response.setStatus(true);
                return response;
            }

            globalMethods.getSummonDtos(summonsList, dateFor, summonDtoList);

            if (billSearchDto.getDateTo().isEmpty() && !billSearchDto.getDateFrom().isEmpty()) {
                Date dFrom = dFormat.parse(billSearchDto.getDateFrom());
                dateFrom = "Generated From: " + dateFor.format(dFrom);
            }


            if (billSearchDto.getDateFrom().isEmpty() && !billSearchDto.getDateTo().isEmpty()) {
                Date dTo = dFormat.parse(billSearchDto.getDateTo());
                dateFrom = "Generated To: " + dateFor.format(dTo);
            }

            if (!billSearchDto.getDateFrom().isEmpty() && !billSearchDto.getDateTo().isEmpty()) {
                Date dFrom = dFormat.parse(billSearchDto.getDateFrom());
                Date dTo = dFormat.parse(billSearchDto.getDateTo());
                dateFrom = "Cases To be Herd From: " + dateFor.format(dFrom) + " To: " + dateFor.format(dTo);
            }

            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(summonDtoList);

            File FileName = new File(".");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("coat", REPORT_IMG);
            parameters.put("details", details);
            parameters.put("dateFrom", dateFrom);


            File billReportTemplate = new File(REPORT_DESIGN_PATH + "cause_lists.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(billReportTemplate.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);


            String contentToSend = "";
            if (reportFormat.equals("pdf")) {

                logger.info("pdf");
                byte[] fileContent = JasperExportManager.exportReportToPdf(jasperPrint);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            } else {

                logger.info("xls");
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DESIGN_PATH + "cause_lists.xlsx");
                exporter.exportReport();
                File excellFile = new File(REPORT_DESIGN_PATH + "cause_lists.xlsx");
                byte[] fileContent = FileUtils.readFileToByteArray(excellFile);
                contentToSend = Base64.getEncoder().encodeToString(fileContent);
            }

            response.setDescription("Success");
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("############### Exception Generation  Bill defaulters Reports ########" + e.getMessage());
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("");
            response.setStatus(true);
        }
        return response;
    }

}
