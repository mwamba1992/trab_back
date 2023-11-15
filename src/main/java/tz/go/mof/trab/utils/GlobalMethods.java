package tz.go.mof.trab.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.PortalBillRequestDto;
import tz.go.mof.trab.dto.report.BillReportDto;
import tz.go.mof.trab.dto.report.PaymentReportDto;
import tz.go.mof.trab.dto.report.QrCodeDto;
import tz.go.mof.trab.dto.report.SummonDto;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.service.*;


/**
 * @author Joel M Gaitan
 */
@Component
public class GlobalMethods {

    private static final Logger billLogger = Logger.getLogger("trab.bill.request");

    private HashMap<Integer, ArrayList<String>> storedFileData;

    private static ArrayList<String> storedFile;

    @Value("${tz.go.tarula.termis.report-img}")
    private String REPORT_IMG;

    @Value("${tz.go.tarula.termis.spcode}")
    private String spCode;

    @Value("${tz.go.tarula.termis.qr.short-code}")
    private String shortCode;

    @Value("${tz.go.tarula.termis.qr.pay-opt}")
    private String optType;

    @Value("${tz.go.trab.exact.bill}")
    private String exact;


    @Value("${tz.go.tarula.termis.qr.short-code}")
    private String transferBank;

    @Value("${tz.go.tarula.termis.qr.transfer-account}")
    private String transferAccount;

    @Value("${tz.go.tarula.termis.qr.tarura-name}")
    private String taruraName;

    @Value("${tz.go.tarula.termis.qr.swift-code}")
    private String swifCode;
    private final CurrencyService currencyService;

    @Autowired
    private BillService billService;

    private final BillItemService billItemService;
    private final FinancialYearService financialYearService;
    private final SummonsRepository summonRepository;
    private final UserRepository userRepository;
    private final AppealsRepository appealsRepository;
    private final ApplicationRegisterRepository applicationRegisterRepository;
    private final JudgeService judgeService;
    private final AppealantRepository appealantRepository;
    private final RabbitTemplate rabbitTemplate;

    private final  NoticeRepository noticeRepository;

    @Value("${external.system.connection.urlReadTimeout}")
    private int externalSystemUrlReadTimeout;

    @Value("${tz.go.trab.noOfDays}")
    private int noOfDays;

    @Value("${external.system.connection.urlConnectionTimeout}")
    private int externalSystemUrlConnectionTimeout;
    public int statusCode;

    @Autowired
    private NumberToWordsUtils numberToWordsUtils;

    @Autowired
    LoggedUser loggedUser;

    private Date billExpireDate;

    @PersistenceContext
    private EntityManager em;

    @Value("${tz.go.trab.systemid}")
    private String systemId;


    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();
    private static byte[] key;


    // Money abbreviation letters suffixes
    static {
        suffixes.put(1_000D, " k");
        suffixes.put(1_000_000D, " M");
        suffixes.put(1_000_000_000D, " B");
        suffixes.put(1_000_000_000_000D, " T");
        suffixes.put(1_000_000_000_000_000D, " P");
        suffixes.put(1_000_000_000_000_000_000D, " E");
    }

    GlobalMethods(CurrencyService currencyService, BillItemService billItemService,
                  FinancialYearService financialYearService, AppealantRepository appealantRepository,
                  SummonsRepository summonRepository, UserRepository userRepository, AppealsRepository appealsRepository,
                  ApplicationRegisterRepository applicationRegisterRepository, JudgeService judgeService,
                  RabbitTemplate rabbitTemplate, NoticeRepository noticeRepository){
        this.currencyService = currencyService;
        this.billItemService = billItemService;
        this.financialYearService = financialYearService;
        this.appealantRepository  = appealantRepository;
        this.summonRepository = summonRepository;
        this.userRepository = userRepository;
        this.appealsRepository = appealsRepository;
        this.applicationRegisterRepository = applicationRegisterRepository;
        this.judgeService = judgeService;
        this.rabbitTemplate = rabbitTemplate;
        this.noticeRepository = noticeRepository;
    }



    public String connectToAnotherSystem(String uri, String content, String contentType,
                                         String acceptType, Hashtable<String, String> hashtable) throws IOException {

        HttpURLConnection externalconnnection = null;

        String reponse = null;


        try {

            URL obj = new URL(uri);

            externalconnnection = (HttpURLConnection) obj.openConnection();

            externalconnnection.setReadTimeout(externalSystemUrlReadTimeout);
            externalconnnection.setConnectTimeout(externalSystemUrlConnectionTimeout);


            externalconnnection.setRequestProperty("Gepg-Code", hashtable.get("Gepg-Code"));
            externalconnnection.setRequestProperty("Gepg-Com", hashtable.get("Gepg-Com"));

            if (acceptType.equalsIgnoreCase("TPLAIN")) {
                externalconnnection.setRequestProperty("Accept", "text/plain");
            } else if (acceptType.equalsIgnoreCase("TXML")) {
                externalconnnection.setRequestProperty("Accept", "text/xml");
            } else if (acceptType.equalsIgnoreCase("AXML")) {
                externalconnnection.setRequestProperty("Accept", "application/xml");
            } else if (acceptType.equalsIgnoreCase("AJSON")) {
                externalconnnection.setRequestProperty("Accept", "application/json");
            } else {
                externalconnnection.setRequestProperty("Accept", contentType);
            }

            if (contentType.equalsIgnoreCase("TPLAIN")) {
                externalconnnection.setRequestProperty("Content-Type", "text/plain");
            } else if (contentType.equalsIgnoreCase("TXML")) {
                externalconnnection.setRequestProperty("Content-Type", "text/xml");
            } else if (contentType.equalsIgnoreCase("AXML")) {
                externalconnnection.setRequestProperty("Content-Type", "application/xml");
            } else if (contentType.equalsIgnoreCase("AJSON")) {
                externalconnnection.setRequestProperty("Content-Type", "application/json");
            } else {
                externalconnnection.setRequestProperty("Content-Type", contentType);
            }

            // Send content
            externalconnnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(externalconnnection.getOutputStream());
            wr.writeBytes(content);
            wr.flush();
            wr.close();

            statusCode = externalconnnection.getResponseCode();

            System.out.println("Status code is: " + statusCode);

            if ((statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED
                    || statusCode == HttpURLConnection.HTTP_ACCEPTED)) {

                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(externalconnnection.getInputStream()))) {

                    statusCode = externalconnnection.getResponseCode();

                    String inputLine;

                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    reponse = response.toString();

                } catch (SocketTimeoutException e) {
                    throw new RuntimeException("Fail to read the content from the server, because of timeout " + e);
                } catch (IOException io) {
                    throw new RuntimeException("IO Exception " + io);
                }

            }

        } catch (Exception e) {
            System.out.println("Status code :" + statusCode + " \n Error: " + e);
            throw e;

        } finally {

            if (externalconnnection != null) {
                externalconnnection.disconnect();
            }

        }

        return reponse;
    }



    /**
     * Method handling beautification of string contains xml content
     *
     * @return String that has been beatified.
     */

    public String beautifyXmlString(String xml) {

        try {

            InputSource src = new InputSource(new StringReader(xml));
            Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();

            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set
            // this
            // to
            // true
            // if
            // the
            // output
            // needs
            // to
            // be
            // beautified.

            return writer.writeToString(document);

        } catch (Exception e) {
            // throw new RuntimeException(e);
            return xml;
        }
    }


    /**
     * @return size with description
     * @author Salum Shomvi This function assign generate xml in string format
     */
    public String convertXmlToString(JAXBContext jaxbContext, Object object) {
        String xmlInStringFormat;
        JAXBContext jaxbContext1;
        StringWriter xmlStringWriter;
        Marshaller jaxbMarshaller1;

        try {
            jaxbContext1 = jaxbContext;
            xmlStringWriter = new StringWriter();
            jaxbMarshaller1 = jaxbContext1.createMarshaller();
            jaxbMarshaller1.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            jaxbMarshaller1.marshal(object, xmlStringWriter);
            xmlInStringFormat = xmlStringWriter.toString();
        } catch (Exception e) {
            xmlInStringFormat = "";
        }

        return xmlInStringFormat;

    }

    public Object convertStringToXml(JAXBContext jaxbContext, String string) {
        Object obj;
        Unmarshaller jaxbUnMarshaller;
        StringReader reader;

        try {
            jaxbUnMarshaller = jaxbContext.createUnmarshaller();
            reader = new StringReader(string);
            obj = jaxbUnMarshaller.unmarshal(reader);

        } catch (Exception e) {
            obj = null;
        }

        return obj;
    }


    public boolean isNullOREmptyString(String inputString) {
        boolean result = (inputString == null || inputString.isEmpty());
        return result;
    }


    public boolean isFileExist(String filePath) {
        boolean result = false;
        if (!filePath.isEmpty()) {
            File fis = new File(filePath);

            if (fis.exists()) {
                result = true;
            }
        }

        return result;
    }


    public String getStringWithinXmlTag(String xmlBody, String xmlTag) {
        String xmlString = "";
        if (xmlBody != null && !xmlBody.isEmpty() && xmlTag != null && !xmlTag.isEmpty()) {

            try {
                xmlString = xmlBody.substring(xmlBody.indexOf("<" + xmlTag + ">"),
                        xmlBody.indexOf("</" + xmlTag + ">") + xmlTag.length() + 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return xmlString;
    }


    public void setKey(String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }




    public long getDifferenceInDays(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;

        return diffTime / (1000 * 60 * 60 * 24);


    }

    @SuppressWarnings("unchecked")
    public List<Payment> findPaymentByCreature(String controlNumber, String pspRef, String bank, String startDate, String endDate) {

        String parameter = "";
        String sqlQuery;
        String joiner = " AND";


        if (!controlNumber.isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.pay_ctr_num=:pay_ctr_num ";
        }

        if (!pspRef.isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.psp_receipt_number=:psp_receipt_number ";
        }

        if (!bank.isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.psp_name=:psp_name ";
        }


        parameter = " where " + parameter;
        sqlQuery = " select * from payment d " + parameter;


        Query q = em.createNativeQuery(sqlQuery, Payment.class);

        if (controlNumber != null && !controlNumber.isEmpty()) {
            q.setParameter("pay_ctr_num", controlNumber);
        }

        if (pspRef != null && !pspRef.isEmpty()) {
            q.setParameter("psp_receipt_number", pspRef);
        }

        if (bank != null && !bank.isEmpty()) {
            q.setParameter("psp_name", "%" + bank + "%");
        }
        return q.getResultList();
    }


    public List<Bill> findBills(Map<String, String> criteria) {

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";


        if ((!criteria.get("status").isEmpty()) && (criteria.get("status")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.bill_payed=:bill_payed ";
        }

        String startDate = criteria.get("startDate").split("T")[0];
        String endDate = criteria.get("endDate").split("T")[0];

        parameter = " where " + parameter;
        sqlQuery = " select * from bill d " + parameter + " AND (d.generated_date BETWEEN " + "\"" + startDate + "\""
                + " AND " + "\"" + endDate + "\"" + ")";

        System.out.println("#### from querying bills #####");
        System.out.println(sqlQuery);

        Query q = em.createNativeQuery(sqlQuery, Bill.class);

        if ((!criteria.get("status").isEmpty()) && (criteria.get("status")) != null) {
            if (criteria.get("status").equals("PAID")) {
                q.setParameter("bill_payed", true);
            }
            if (criteria.get("status").equals("PENDING")) {
                q.setParameter("bill_payed", false);
            }
        }

        System.out.println(q.getResultList());
        return q.getResultList();
    }

    public String generateQRCodeImage(String text, int width, int height, Bill bill)
            throws WriterException, IOException {
        File logoFile = new File(REPORT_IMG);
        BufferedImage logoImage = ImageIO.read(logoFile);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(pngData);

    }

    public BillReportDto getBillReportDto(Bill bill) throws IOException, WriterException {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        ObjectMapper mapper = new ObjectMapper();

        QrCodeDto qrCodeDto = new QrCodeDto();
        qrCodeDto.setAmount(bill.getBilledAmount().toString());
        qrCodeDto.setBillCcy(bill.getCurrency());
        qrCodeDto.setBillPayOpt(bill.getBillPayType());
        qrCodeDto.setBillExprDt(bill.getExpiryDate().toString());
        qrCodeDto.setOpType(optType);
        qrCodeDto.setBillReference(bill.getBillControlNumber());
        qrCodeDto.setBillRsv01(bill.getBillDescription());
        qrCodeDto.setShortCode(shortCode);
        String qr = mapper.writeValueAsString(qrCodeDto);



        BillReportDto billReportDto = new BillReportDto();
        billReportDto.setControlNumber(bill.getBillControlNumber());
        billReportDto.setQrString(generateQRCodeImage(qr, 200, 200, bill));
        billReportDto.setPayerPhone(bill.getPayerPhone());
        billReportDto.setExpireDate(bill.getExpiryDate().toString());
        billReportDto.setPayerName(bill.getPayerName().replace("\n", ""));
        billReportDto.setServiceProviderCode(spCode);
        billReportDto.setPreparedBy(loggedUser.getInfo().getName());
        billReportDto.setPrintedBy(loggedUser.getInfo().getName());
        billReportDto.setTotalBilledAmount(bill.getBilledAmount().doubleValue());
        billReportDto.setBillDescription(bill.getBillDescription());
        billReportDto.setAmountInWords(numberToWordsUtils.convert((long)bill.getBilledAmount().doubleValue()));


        TrabHelper.print(billReportDto);

        if(bill.getAppType() !=null){
            if(bill.getAppType().equals("STATEMENT")) {
                billReportDto.setPaymentRef(appealsRepository.findAppealsByBill(bill.getBillId()) !=null?
                        appealsRepository.findAppealsByBill(bill.getBillId()).getAppealNo():bill.getBillReference());
            }

            if(bill.getAppType().equals("NOTICE")){
                billReportDto.setPaymentRef(noticeRepository.findNoticeByBill(bill.getBillId()) !=null?
                        noticeRepository.findNoticeByBill(bill.getBillId()).getNoticeNo():bill.getBillReference());
            }

            if(bill.getAppType().equals("APPLICATION")){
                billReportDto.setPaymentRef(applicationRegisterRepository.findApplicationRegisterByBill(bill.getBillId()) !=null?
                        applicationRegisterRepository.findApplicationRegisterByBill(bill.getBillId()).getApplicationNo():bill.getBillReference());
            }
        }else {
            billReportDto.setPaymentRef(bill.getBillReference());
        }
        billReportDto.setPrintedDate(date);
        billReportDto.setCollectionCenter("DAR-ES-SALAAM");
        billReportDto.setBankName(transferBank);
        billReportDto.setSpName(taruraName);
        billReportDto.setAccountNumber(transferAccount);
        billReportDto.setSwiftCode(swifCode);

        return billReportDto;

    }

    public PaymentReportDto getPaymentReportDto(Payment payment) {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        PaymentReportDto reportDto = new PaymentReportDto();

        if(payment.getBill().getAppType() !=null){
            if(payment.getBill().getAppType().equals("STATEMENT")) {
                reportDto.setBillId(appealsRepository.findAppealsByBill(payment.getBill().getBillId()).getAppealNo());
            }

            if(payment.getBill().getAppType().equals("NOTICE")){
                reportDto.setBillId(noticeRepository.findNoticeByBill(payment.getBill().getBillId()).getNoticeNo());
            }

            if(payment.getBill().getAppType().equals("APPLICATION")){
                reportDto.setBillId(applicationRegisterRepository.findApplicationRegisterByBill(payment.getBill().getBillId()).getApplicationNo());
            }
        }else {
            reportDto.setBillId(payment.getBill().getBillReference());
        }
        reportDto.setControlNumber(payment.getPayCtrNum());
        reportDto.setPaidAmount(payment.getPaidAmt());
        reportDto.setPayerName(payment.getBill().getPayerName());
        reportDto.setTransactionDate(payment.getCreatedDate().toString());
        reportDto.setPspReceiptNumber(payment.getPayRefId());
        reportDto.setAmountInWords(numberToWordsUtils.convert(payment.getPaidAmt().longValue()));
        reportDto.setOutstandingBalance(payment.getBillAmt().subtract(payment.getPaidAmt()).toString());
        reportDto.setIssuedDate(date);
        reportDto.setIssuedBy(loggedUser.getInfo().getName().toUpperCase());
        return reportDto;
    }

    public List<ApplicationRegister> getApplications(Map<String, String> criterial) {

        List<ApplicationRegister> applicationRegisters;

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";


        if ((!criterial.get("hearing").isEmpty()) && (criterial.get("hearing")) != null) {
            if (criterial.get("hearing").equalsIgnoreCase("decided")) {
                if (!parameter.isEmpty()) {
                    parameter = parameter + joiner;
                }
                parameter = parameter + " d.date_of_decision is not null";
            }

            if (criterial.get("hearing").equalsIgnoreCase("pending")) {
                if (!parameter.isEmpty()) {
                    parameter = parameter + joiner;
                }
                parameter = parameter + " d.date_of_decision is null";
            }
        }


        if ((!criterial.get("tax").isEmpty()) && (criterial.get("tax")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.tax_id=:tax_id ";
        }

        if ((!criterial.get("region").isEmpty()) && (criterial.get("region")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.application_no LIKE :region ";
        }

        if (!criterial.get("applicationTrendType").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.status_trend=:status_trend ";
        }
        if (!criterial.get("dateFrom").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.date_of_filling >=:from_date_of_filling ";
        }

        if (!criterial.get("dateTo").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.date_of_filling <=:to_date_of_filling ";
        }



        if (!criterial.get("decidedDateFrom").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.date_of_decision >=:from_decided_date ";
        }

        if (!criterial.get("decidedDateTo").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.date_of_decision <=:to_decided_date ";
        }


        if ((!criterial.get("chairMan").isEmpty()) && (criterial.get("chairMan")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.decided_by=:chairMan ";
        }

        if (criterial.get("tax").isEmpty() && criterial.get("applicationTrendType").isEmpty() && criterial.get("dateFrom").isEmpty()
                && criterial.get("dateTo").isEmpty() && criterial.get("region").isEmpty() && criterial.get("hearing").isEmpty()
                &&  criterial.get("decidedDateFrom").isEmpty()&&
                criterial.get("decidedDateTo").isEmpty() && criterial.get("chairMan").isEmpty()
        ) {
            sqlQuery = "select * from application_register ";
            Query q = em.createNativeQuery(sqlQuery, ApplicationRegister.class);
            applicationRegisters = q.getResultList();
        } else {
            parameter = " where " + parameter;
            sqlQuery = " select * from application_register d " + parameter +  " order by date_of_filling desc";
            Query q = em.createNativeQuery(sqlQuery, ApplicationRegister.class);
            getQueryFromSelection(criterial, q);
            if ((!criterial.get("region").isEmpty()) && (criterial.get("region")) != null) {
                q.setParameter("region", criterial.get("region").toUpperCase());
            }

            applicationRegisters = q.getResultList();
        }
        return applicationRegisters;

    }

    private void getQueryFromSelection(Map<String, String> criterial, Query q) {
        if ((!criterial.get("tax").isEmpty()) && (criterial.get("tax")) != null) {
            q.setParameter("tax_id", criterial.get("tax"));
        }
        if ((!criterial.get("applicationTrendType").isEmpty()) && (criterial.get("applicationTrendType")) != null) {
            q.setParameter("status_trend", criterial.get("applicationTrendType"));
        }
        if ((!criterial.get("dateFrom").isEmpty()) && (criterial.get("dateFrom")) != null) {
            q.setParameter("from_date_of_filling", criterial.get("dateFrom"));
        }
        if ((!criterial.get("dateTo").isEmpty()) && (criterial.get("dateTo")) != null) {
            q.setParameter("to_date_of_filling", criterial.get("dateTo"));
        }

        if ((!criterial.get("decidedDateFrom").isEmpty()) && (criterial.get("dateFrom")) != null) {
            q.setParameter("from_decided_date", criterial.get("decidedDateFrom"));
        }
        if ((!criterial.get("decidedDateTo").isEmpty()) && (criterial.get("decidedDateTo")) != null) {
            q.setParameter("to_decided_date", criterial.get("decidedDateTo"));
        }


        if ((criterial.get("wonBy")) != null) {
            if(!criterial.get("wonBy").isEmpty()){
                q.setParameter("won_by", criterial.get("wonBy"));
            }
        }
        if ((criterial.get("chairMan")) != null) {
            if(!criterial.get("chairMan").isEmpty()) {
                q.setParameter("chairMan", criterial.get("chairMan"));
            }
        }
    }


    public List<Notice> getNotices(Map<String, String> criterial) {

        List<Notice> notices;

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";

        if (!criterial.get("dateFrom").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.logged_at >=:from_date_of_filling ";
        }

        if (!criterial.get("dateTo").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.logged_at <=:to_date_of_filling ";
        }


        if (criterial.get("dateFrom").isEmpty() && criterial.get("dateTo").isEmpty()) {
            sqlQuery = "select * from notice ";
            Query q = em.createNativeQuery(sqlQuery, Notice.class);
            notices = q.getResultList();
        } else {
            parameter = " where " + parameter;
            sqlQuery = " select * from notice d " + parameter;

            System.out.println("###########  QUERY ##############" + sqlQuery);

            Query q = em.createNativeQuery(sqlQuery, Notice.class);


            if ((!criterial.get("dateFrom").isEmpty()) && (criterial.get("dateFrom")) != null) {
                q.setParameter("from_date_of_filling", criterial.get("dateFrom"));
            }
            if ((!criterial.get("dateTo").isEmpty()) && (criterial.get("dateTo")) != null) {
                q.setParameter("to_date_of_filling", criterial.get("dateTo"));
            }

            notices = q.getResultList();
        }
        return notices;

    }


    public List<Appeals> getAppeals(Map<String, String> criterial) {



        System.out.println("#### Get Appeals ###");
        TrabHelper.print(criterial);
        List<Appeals> appeals;

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";


        if ((!criterial.get("hearing").isEmpty()) && (criterial.get("hearing")) != null) {
            if (criterial.get("hearing").equalsIgnoreCase("decided")) {
                if (!parameter.isEmpty()) {
                    parameter = parameter + joiner;
                }
                parameter = parameter + " d.decided_date is not null";
            }

            if (criterial.get("hearing").equalsIgnoreCase("pending")) {
                if (!parameter.isEmpty()) {
                    parameter = parameter + joiner;
                }
                parameter = parameter + " d.decided_date is null";
            }


            if (criterial.get("hearing").equalsIgnoreCase("Pending For Judgement")) {
                if (!parameter.isEmpty()) {
                    parameter = parameter + joiner;
                }
                parameter = parameter + " d.proceding_status = 'CONCLUDED'";
            }
        }

        if ((!criterial.get("chairMan").isEmpty()) && (criterial.get("chairMan")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.decided_by=:chairMan ";
        }

        if ((!criterial.get("tax").isEmpty()) && (criterial.get("tax")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.tax_id=:tax_id ";
        }

        if (!criterial.get("applicationTrendType").isEmpty()) {
            System.out.println("inside Trend Type");
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.status_trend=:status_trend ";
        }

        if (!criterial.get("dateFrom").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.date_of_filling >=:from_date_of_filling ";
        }

        if (!criterial.get("dateTo").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.date_of_filling <=:to_date_of_filling ";
        }


        if (!criterial.get("decidedDateFrom").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.decided_date >=:from_decided_date ";
        }

        if (!criterial.get("decidedDateTo").isEmpty()) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.decided_date <=:to_decided_date ";
        }

        if ((!criterial.get("region").isEmpty()) && (criterial.get("region")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.appeal_no LIKE :region ";
        }

        if ((!criterial.get("wonBy").isEmpty()) && (criterial.get("wonBy")) != null) {
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.won_by=:won_by ";
        }

        if (criterial.get("tax").isEmpty() && criterial.get("applicationTrendType").isEmpty() && criterial.get("dateFrom").isEmpty()
                && criterial.get("dateTo").isEmpty() && criterial.get("region").isEmpty() && criterial.get("wonBy").isEmpty() &&
                criterial.get("chairMan").isEmpty()&&criterial.get("hearing").isEmpty() && criterial.get("decidedDateFrom").isEmpty()&&
                criterial.get("decidedDateTo").isEmpty()) {
            sqlQuery = "select * from appeals";
            Query q = em.createNativeQuery(sqlQuery, Appeals.class);
            appeals = q.getResultList();
        } else {
            parameter = " where " + parameter;
            sqlQuery = " select * from  appeals d " + parameter + "  ORDER BY CAST(SUBSTRING_INDEX(appeal_no, '/', -1) AS UNSIGNED), CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(appeal_no, '.', -1), '/', 1) AS UNSIGNED) ";

            System.out.println("## " + sqlQuery);
            Query q = em.createNativeQuery(sqlQuery, Appeals.class);

            getQueryFromSelection(criterial, q);
            if ((!criterial.get("region").isEmpty()) && (criterial.get("region")) != null) {
                q.setParameter("region", criterial.get("region"));
            }

            appeals = q.getResultList();
        }
        return appeals;
    }


    public Bill savingBillFromDto(PortalBillRequestDto billRequestDto) {

        Bill savedBill = null;
        Date date1 = new Date();
        long time = date1.getTime();


        try {
            setBillExpireDate(new Date());
            Double totalBill = billRequestDto.getBillItems().stream().mapToDouble(x -> x.getBilledAmount().doubleValue()).sum();

            Bill bill = new Bill();

            bill.setAction("1");
            bill.setPayerEmail(billRequestDto.getEmail());
            bill.setPayerPhone(billRequestDto.getPhoneNumber());
            bill.setBillPayed(false);
            bill.setBillDescription(billRequestDto.getBillDescription());
            bill.setGeneratedDate(new Date());
            bill.setBillControlNumber("0");
            bill.setBillPayType(exact);
            bill.setExpiryDate(getBillExpireDate());
            bill.setBilledAmount(new BigDecimal(totalBill));
            bill.setBillEquivalentAmount(new BigDecimal(totalBill));
            bill.setMiscellaneousAmount(new BigDecimal("0.0"));
            bill.setPaidAmount(new BigDecimal("0"));
            bill.setApprovedBy(billRequestDto.getCreatedBy());
            bill.setCreatedBy(billRequestDto.getApprovedBy());
            bill.setCurrency(currencyService.findByCurrencyShortName(billRequestDto.getCcy()).getCurrencyShortName());
            bill.setItemId(billRequestDto.getBillItems().get(0).getGsfCode());
            bill.setBillReference("TERMIS" + time);
            bill.setSpSystemId(systemId);
            bill.setStatus("PENDING");
            bill.setPaidAmount(new BigDecimal("0"));
            bill.setPayerName(billRequestDto.getPayerName());
            bill.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());

            savedBill = billService.saveBill(bill);


            Bill finalSavedBill = savedBill;
            billRequestDto.getBillItems().forEach(item -> {
                BillItems billItem = new BillItems();
                billItem.setSourceName(item.getSourceName());
                billItem.setBillItemDescription(item.getSourceName());
                billItem.setGsfCode(item.getGsfCode());
                billItem.setBillItemRef("ITEM" + time);
                billItem.setBillItemAmount(item.getBilledAmount());
                billItem.setBillItemEqvAmount(item.getBilledAmount());
                billItem.setBillItemMiscAmount(new BigDecimal("0.0"));
                billItem.setBillItemDescription(item.getSourceName());
                billItem.setBill(finalSavedBill);
                billItemService.saveBillItem(billItem);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedBill;
    }


    public Date getBillExpireDate() {
        return billExpireDate;
    }

    public void setBillExpireDate(Date billExpireDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billExpireDate);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date date = calendar.getTime();
        this.billExpireDate = date;
    }


    public Response<Summons> createSummon(Map<String, String> req, boolean isNew) {

        System.out.println("#### request#####");
        TrabHelper.print(req);

        Response<Summons> res = new Response<>();
        try {
            Summons summons;

            if (isNew) {
                summons = new Summons();
            } else {
                summons = summonRepository.findById(Long.valueOf(req.get("summoId"))).get();
            }


            AtomicReference<String> appList = new AtomicReference<>("");
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Integer>> mapList;
            SystemUser user = userRepository.findById(loggedUser.getInfo().getId()).get();
            Date startDate;
            Date endDate;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            String startDates[] = req.get("startDate").split("T");
            String endDates[] = req.get("endDate").split("T");


            startDate = formatter.parse(startDates[0]);
            endDate = formatter.parse(endDates[0]);


            if (endDate.before(startDate)) {
                res.setDescription("Dates mismatch!!!");
                res.setStatus(false);
                res.setData(null);
                res.setCode(ResponseCode.FAILURE);
                return res;
            }


            SummonsAppeal summonsAppeals = new SummonsAppeal();

            Judge judge = judgeService.findById(req.get("judge"));

            summons.setJudge(judge.getName());
            summons.setJud(judge);


            mapList = mapper.readValue(req.get("appList"), List.class);

            if(mapList.size()<1){
                res.setDescription("Please select Appeals or Applications For Summons creation!!!");
                res.setStatus(false);
                res.setData(null);
                res.setCode(ResponseCode.FAILURE);
                return res;
            }



            if (mapList.size() > 0) {
                mapList.forEach(x -> {
                    appList.set(appList + " , " + x.get("id"));
                });
            }

            summons.setAppList(appList.get());
            summons.setVenue(req.get("venue"));
            summons.setSummonStartDate(startDate);
            summons.setSummonEndDate(endDate);
            summons.setSystemUser(user);
            summons.setCreatedDate(new Date());
            summons.setTime(req.get("time") + " HRS");
            summons.setMemberOne(req.get("memberOne"));
            summons.setMemberTwo(req.get("memberTwo"));
            summons.setAppeleantAdress(req.get("drawnByAdress"));
            summons.setDrawnBy(req.get("drawnByName"));

            if (req.get("name").equals("Appeals")) {


                summons.setAppeleant(req.get("applicant"));
                summons.setRespondentAdress(req.get("respondentAdress"));
                summons.setSummonType("APPEAL");


                summons.setRespondent("COMM GENERAL");
                summons.setRespondentAdress("P.O BOX 11491  ,DAR-ES-SALAAM");


                summons.setAppeleantAdress(req.get("drawnByAdress"));
                summons.setDrawnBy(req.get("drawnByName"));
                summons.setAppeleant(appealsRepository.findById(Long.valueOf(mapList.get(0).get("id"))).get().getAppellantName());

                Summons newSummons = summonRepository.save(summons);

                mapList.forEach(x -> {

                    Appeals appeal = appealsRepository.findById(Long.valueOf(x.get("id"))).get();

                    summonsAppeals.setAppealId(appeal.getAppealId().toString());
                    summonsAppeals.setSummonId(newSummons.getSummonId().toString());
                   // summonRepository.save(summonsAppeals);
                    appeal.setSummons(newSummons);
                    appealsRepository.save(appeal);


                    newSummons.setTaxType(appeal.getTax().getTaxName());
                    newSummons.setSummonNo(newSummons.getSummonId().toString());
                });


                summonRepository.save(newSummons);


                res.setStatus(true);
                res.setData(newSummons);
                res.setCode(ResponseCode.SUCCESS);
            } else {

                summons.setSummonType("APPLICATION");
                Summons newSummons = summonRepository.save(summons);


                mapList.forEach(x -> {
                    ApplicationRegister application = applicationRegisterRepository.findById(Long.valueOf(x.get("id"))).get();
                    summonsAppeals.setAppealId(application.getApplicationId().toString());
                    summonsAppeals.setSummonId(newSummons.getSummonId().toString());
                   // summonRepository.save(summonsAppeals);

                    if (application.getType().equals("1")) {
                        newSummons.setAppeleant("COMM GENERAL");
                        newSummons.setAppeleantAdress("P.O BOX 11491 DAR-ES-SALAAM");

                        newSummons.setRespondentAdress(req.get("drawnByAdress"));
                        newSummons.setRespondent(applicationRegisterRepository.findById(Long.valueOf(mapList.get(0).get("id"))).get().getApplicant().getFirstName());
                    } else {
                        newSummons.setRespondent("COMM GENERAL");
                        newSummons.setRespondentAdress("P.O BOX 11491 DAR-ES-SALAAM");

                        newSummons.setAppeleantAdress(req.get("drawnByAdress"));
                        newSummons.setAppeleant(applicationRegisterRepository.findById(Long.valueOf(mapList.get(0).get("id"))).get().getApplicant().getFirstName());
                    }

                    application.setSummons(newSummons);

                    newSummons.setTaxType(application.getTaxes().getTaxName());
                    newSummons.setSummonNo(newSummons.getSummonId().toString());
                    summonRepository.save(newSummons);
                    applicationRegisterRepository.save(application);
                });


                res.setStatus(true);
                res.setData(newSummons);
                res.setCode(ResponseCode.SUCCESS);

                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setDescription("Service Temporary Unavailable");
            res.setStatus(false);
            res.setCode(ResponseCode.FAILURE);

        }
        return res;
    }


    public void getSummonDtos(List<Summons> summonsList, SimpleDateFormat dateFor, List<SummonDto> summonDtoList) {

        summonsList.forEach(summon -> {
            try {
                SummonDto summonDto = new SummonDto();
                summonDto.setTime(summon.getTime());
                summonDto.setRespondent(summon.getRespondent());
                summonDto.setAppeleant(summon.getAppeleant());
                summonDto.setOne(summon.getMemberOne());
                summonDto.setTwo(summon.getMemberTwo());
                summonDto.setChairMan(summon.getJudge());
                summonDto.setTaxType(summon.getTaxType());
                summonDto.setStartDate(dateFor.format(summon.getSummonStartDate()));
                summonDto.setEndDate(dateFor.format(summon.getSummonEndDate()));
                summonDto.setTime(summon.getTime());
                String appeals[] = summon.getAppList().split(",");
                String appList = "";
                int i = 0;
                for (String test : appeals) {
                    test = test.replace(" ", "");
                    if (summon.getSummonType().equals("APPLICATION")) {
                        if (!test.isEmpty()) {
                            String appealNo = applicationRegisterRepository.findById(Long.valueOf(test)).get().getApplicationNo();
                            String connector = i == appeals.length ? "" : " , ";
                            appList = appList + appealNo + connector;
                        }
                    } else {
                        if (!test.isEmpty()) {
                            String appealNo = appealsRepository.findById(Long.valueOf(test)).get().getAppealNo();
                            String connector = i == appeals.length ? "" : " , ";
                            appList = appList + appealNo + connector;
                        }
                    }
                }
                summonDto.setAppealNo(appList);
                summonDto.setType(summon.getSummonType());
                summonDto.setVenue(summon.getVenue());
                summonDtoList.add(summonDto);
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public boolean checkIfAllowedFile(String extension){
        String[] allowedFormat = {"xls", "csv", "xlsx"};
        return Arrays.asList(allowedFormat).contains(extension.toLowerCase());
    }

    public String extractControlNumber(String sample) {
        String payControlNum = null;
        try {
//            Pattern p = Pattern.compile("(99\\d+)");
            //(99\d{10,1})
            Pattern p = Pattern.compile("(99\\d{10,13})");
            Matcher m = p.matcher(sample);
            if (m.find()) {
                payControlNum = m.group();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payControlNum;
    }



    /**

     * @author Joel M Gaitan - MoFP
     * @desc The "publishToExchangeWithHeaders" sends message to exchange with routing key provided. It receives only the
     * request which are suppose to be published to queue(generall coming from other systems). In case failure
     * occur on publishing it will notify back caller so as to no upon.
     * @since 1.0.0
     */
    public String convertHashMapToCsv(HashMap<Integer, ArrayList<String>> storedFileHashMap) throws IOException {

        String csvBuilder = null;
        StringBuilder stringBuffer;
        try {

            if (!storedFileHashMap.isEmpty()) {
                stringBuffer = new StringBuilder();
                for (Map.Entry<Integer, ArrayList<String>> entry : storedFileHashMap.entrySet()) {

                    if (Integer.valueOf(entry.getKey()) == 0) {
                        stringBuffer.append(String.join(",", entry.getValue()).replace("[", "").
                                replace("]", "")).append("\n");
                    }
                    if (Integer.valueOf(entry.getKey()) != 0) {
                        stringBuffer.append(String.join(",", entry.getValue())).append("\n");
                    }

                }
                csvBuilder = stringBuffer.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return csvBuilder;

    }


    /**
     * @return size with description
     * @author Joel M Gaitan reding different of files xsl , csv and xlsx
     */

    public HashMap<Integer, ArrayList<String>> readReconFile(String rootFileName, String fileExtension) {
        storedFileData = new HashMap<>();

        System.out.println("extension: " + fileExtension);
        System.out.println("file name: " + rootFileName);

        try {
            switch (fileExtension.toLowerCase()) {
                case "":
                    storedFileData = null;
                    break;
                case "csv":
                    storedFileData = this.readCsvFile(rootFileName);
                    break;
                case "xlsx":
                    storedFileData = this.readXlsxFile(rootFileName);
                    break;
                case "txt":
                    storedFileData = null;
                    break;
                case "xml":
                    storedFileData = null;
                    break;
                case "xls":
                    storedFileData = this.readXlsFile(rootFileName);
                    break;
                default:
                    storedFileData = null;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return storedFileData;
    }


    /**
     *
     * @author Joel M Gaitan - MoFP
     * @param rootFileName
     * @return
     */
    private HashMap<Integer, ArrayList<String>> readCsvFile(String rootFileName) {
        try {
            storedFileData = new HashMap<>();
            storedFile = new ArrayList<>();

            int rowCount = 0;
            BufferedReader br = new BufferedReader(new FileReader(rootFileName));
            Pattern p = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");

            String[] columns = p.split(br.readLine());
            ArrayList<String> rowData = new ArrayList<>(Arrays.asList(columns));

            storedFileData.put(rowCount, rowData);
            storedFile.addAll(Arrays.asList(columns));

            rowCount++;
            rowData = new ArrayList<>();
            String data;

            while ((data = br.readLine()) != null) {
                String[] values = p.split(data);
                rowData.addAll(Arrays.asList(values));
                storedFileData.put(rowCount, rowData);

                rowCount++;
                rowData = new ArrayList<>();
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return storedFileData;
    }


    /**
     *
     * @author Joel M Gaitan - MoFP
     * @param rootFileName
     * @return
     * @throws ParseException
     */
    private HashMap<Integer, ArrayList<String>> readXlsxFile(String rootFileName) throws ParseException {
        try {
            storedFileData = new HashMap<>();

            int rowCount = 0;

            ArrayList<String> rowData = new ArrayList<>();

            InputStream ExcelFileToRead = new FileInputStream(rootFileName);

            Workbook wb = new XSSFWorkbook(ExcelFileToRead);

            Sheet sheet = wb.getSheetAt(0);

            int rowStart = sheet.getFirstRowNum();

            int rowEnd = sheet.getLastRowNum();

            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row rows = sheet.getRow(rowNum);
                if (rows == null) {
                    continue;
                }
                int lastColumn = rows.getLastCellNum();
                for (int cn = 0; cn < lastColumn; cn++) {
                    Cell currentCell = rows.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (currentCell == null) {
                        rowData.add("");
                    } else {
                        rowData.add(this.getCellValue(currentCell) !=null?this.getCellValue(currentCell).replaceAll(",", ""):null);
                    }
                }
                storedFileData.put(rowCount, rowData);
                rowCount++;
                rowData = new ArrayList<>();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return storedFileData;

    }


    /**
     *
     * @param currentCell
     * @return String
     */
    private String getCellValue(Cell currentCell) {
        String cellValue = null;

        DataFormatter dataFormatter = new DataFormatter();

        try {
            switch (currentCell.getCellType()) {
                case FORMULA:
                    cellValue = currentCell.getStringCellValue();
                    break;
                case STRING:
                    cellValue = currentCell.getStringCellValue();
                    break;
                case BLANK:
                    cellValue = "";
                    break;
                case NUMERIC:
                    double doubleValue = currentCell.getNumericCellValue();
                    BigDecimal bd = new BigDecimal(Double.toString(doubleValue));
                    cellValue = bd.toString().trim();
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            //System.out.println("Error: " + currentCell.getCellType() + " " + currentCell.getColumnIndex() + " " + currentCell.getStringCellValue());
            e.printStackTrace();

        }
        return cellValue;
    }


    /**
     * @author Joel M Gaitan - MoFP
     * @param rootFileName
     * @return HashMap
     * @desc accept the file path and read it convert it to hashmap
     */
    private HashMap<Integer, ArrayList<String>> readXlsFile(String rootFileName) {
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(rootFileName));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            int rowStart = sheet.getFirstRowNum();
            int rowEnd = sheet.getLastRowNum();

            Iterator rows = sheet.rowIterator();
            storedFileData = new HashMap<>();
            ArrayList<String> rowData = new ArrayList<>();
            int rowCount = 0;
            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                HSSFRow row = (HSSFRow) rows.next();
                if (row == null) {
                    continue;
                }
                int lastColumn = row.getLastCellNum();
                for (int cn = 0; cn < lastColumn; cn++) {
                    Cell currentCell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (currentCell == null) {
                        rowData.add("");
                    } else {
                        rowData.add(this.getCellValue(currentCell).replaceAll(",", ""));
                    }
                }
                storedFileData.put(rowCount, rowData);
                rowCount++;
                rowData = new ArrayList<>();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return storedFileData;

    }


    /**
     * @param exchangeName
     * @param routingKey
     * @param contentPublished
     * @param msgHeaders
     * @return
     * @author Joel M Gaitan - MoFP
     * @desc The "publishToExchangeWithHeaders" sends message to exchange with routing key provided. It receive only the
     * request which are suppose to be published to queue(generall comming from other systems). In case failure
     * occur on publishing it will notify back caller so as to no upon.
     * @since 1.0.0
     */
    public boolean publishToExchangeWithHeaders(String exchangeName, String routingKey, Object contentPublished,
                                                final Map<String, String> msgHeaders) {
        boolean status = false;
        ObjectMapper object = new ObjectMapper();
        String receivedContent = "";

        try {
            receivedContent = object.writeValueAsString(contentPublished);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, contentPublished, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message m) throws AmqpException {
                    m.getMessageProperties().getHeaders().put("retryCounter", "0");
                    m.getMessageProperties().getHeaders().put("mappingHeader", msgHeaders);
                    for (String key : msgHeaders.keySet()) {
                        m.getMessageProperties().getHeaders().put(key, msgHeaders.get(key));
                    }
                    return m;
                }
            });
            status = true;
        } catch (Exception e) {
            billLogger.error("Failed message to exchange: " + exchangeName + ", routing Key: " + routingKey + ",content: " + receivedContent);
            e.printStackTrace();
        }
        return status;
    }



    /**
     *
     * @author Joel M Gaitan - MoFP
     * @param reconFileDtoCsv
     * @param writer
     * @retun void
     * @desc This Method is used to write to csv reconc file
     */
    public void csvWriterReconFile(Writer writer, String reconFileDtoCsv) {
        try {
            writer.write(reconFileDtoCsv);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @author Joel M Gaitan - MoFP
     * @param req
     * @param notice
     *
     * @return void
     * @desc This method to save appealant from notice
     */
    public void saveAppellant(@RequestBody Map<String, String> req, Notice notice) {
//        Appellant appealant = notice.getAppealantId();
//        appealant.setNatureOfBusiness(req.get("natOf"));
//        appealant.setEmail(req.get("email"));
//        appealant.setPhoneNumber(req.get("phone"));
//        appealant.setTinNumber(req.get("tinNumber"));
//
//
//        appealantRepository.save(appealant);
    }


    public boolean validatePhoneNumber(String phoneNo) {
        phoneNo = phoneNo.replace("-", "");
        if (phoneNo.isEmpty()) {
            return false;
        } else if (phoneNo.trim().matches("\\d{10}") && phoneNo.trim().substring(0, 1).equals("0")) {

            // validate phone numbers of format "0723XXXXXX"
            return true;

        } else if (phoneNo.trim().matches("\\d{12}") && phoneNo.trim().substring(0, 3).equals("255")) {

            // validate phone numbers of format "255723XXXXXX"
            return true;
        } else {
            // return false if nothing matches the input
            return false;
        }

    }

    public boolean isValidEmailAddress(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
