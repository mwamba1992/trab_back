package tz.go.mof.trab.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tz.go.mof.trab.dto.bill.PageListResponse;
import tz.go.mof.trab.dto.payment.*;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.Payment;
import tz.go.mof.trab.models.ReconBatch;
import tz.go.mof.trab.models.ReconcPayment;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.repositories.PaymentRepository;
import tz.go.mof.trab.repositories.ReconBatchRepository;
import tz.go.mof.trab.repositories.ReconcPaymentRepository;
import tz.go.mof.trab.service.PaymentService;
import tz.go.mof.trab.utils.*;

import javax.websocket.server.PathParam;
import javax.xml.bind.JAXBContext;
import java.io.File;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Joel M Gaitan
 */

@CrossOrigin(origins = {"*"})
@Controller
public class BatchController {

	@Autowired
	private ReconBatchRepository reconBatchRepository;

    @Autowired
    GlobalMethods globalMethods;

    @Autowired
    BillRepository billService;

    @Autowired
    ReconcPaymentRepository reconcPaymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Value("${gepg.private.key.passphrase}")
    private String gepgPassphrase;

    @Value("${gepg.private.key.alias}")
    private String gepgAlias;

    @Value("${gepg.private.key.file.path}")
    private String gepgKeyFilePath;

    @Value("${tz.go.trab.spcode}")
    private String spCode;

    @Value("${tz.go.trab.systemid}")
    private String systemId;

    @Value("${tz.go.trab.gepgurl}")
    private String gepgUrl;

    @Value("${tz.go.trab.gepgcom}")
    private String gepgComm;

    @Value("${tz.go.trab.gepgcode}")
    private String gepgCode;

    @Value("${gepg.public.key.file.path}")
    private String gepgPublicFilePath;

    @Value("${gepg.public.key.passphrase}")
    private String gepgPublicPassphrase;

    @Value("${gepg.public.key.alias}")
    private String gepgPublicAlias;

    @Autowired
    private GePGGlobalSignature globalSignature;

    @Autowired
    private GepgMiddleWare gepgMiddleWare;

    private static final Logger reconcRequest = Logger.getLogger("trab.reconc.request");

    @Scheduled(cron = "0 0 3 * * *")
    @ResponseBody
    public void scheduleTaskWithCronExpression() throws Exception {


        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        String date = simpleDateFormat.format(yesterday);
        gepgMiddleWare.sendReconBatch(date);

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("Cron Task :: Execution Time - {}" + dateTimeFormatter.format(LocalDateTime.now()));
    }


    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = "0 30 3 * * *")
    public void saveTransactionFromRecon() throws Exception {
        List<ReconcPayment> reconPayments = (List<ReconcPayment>) reconcPaymentService.findAll();
        Payment newPayment = new Payment();

        for (ReconcPayment payments : reconPayments) {
            if (paymentRepository.selectOnePayment(payments.getPayRefId(), payments.getPspReceiptNumber(),
                    payments.getBill().getBillId()) == null) {

                newPayment = new Payment();
                newPayment.setBill(payments.getBill());
                newPayment.setCreatedDate(payments.getCreatedDate());
                newPayment.setPayRefId(payments.getPayRefId());
                newPayment.setPspReceiptNumber(payments.getPspReceiptNumber());
                newPayment.setPaidAmt(payments.getPaidAmt());
                newPayment.setPayCtrNum(payments.getPayCtrNum());
                newPayment.setPyrCellNum(payments.getPyrCellNum());
                newPayment.setPyrName(payments.getPyrName());
                newPayment.setTrxId(payments.getUsdPayChnl());
                newPayment.setPspName(payments.getPspName());
                newPayment.setBillAmt(payments.getBill().getBilledAmount());
                newPayment.setTrxId(payments.getTrxId());

                paymentRepository.save(newPayment);

                reconcRequest.info("###### Save Transaction From Reconcillition #######" + "Billid: + TIC "
                        + payments.getBill().getBillId() + " ControlNumber: " + payments.getPayCtrNum());

            }
        }

    }

    @PostMapping(path = "/batch/receiveBatch", consumes = "application/xml", produces = "application/xml")
    @ResponseBody
    public ResponseEntity<Object> receiveReconcFile(@RequestBody String requestBody, @RequestHeader HttpHeaders headers) {

        try {

            ReconBatch reconBatch = new ReconBatch();

            ReconcillitionResponseWrapper responseWraper = new ReconcillitionResponseWrapper();
            ReconcillitionResponseAck responseAck = new ReconcillitionResponseAck();
            ReconcillitionResposeAckWrapper responseAckWrapper = new ReconcillitionResposeAckWrapper();
            ReconcPayment reconPayment = new ReconcPayment();

            reconcRequest.info("######## ReconcRequest ######## " + globalMethods.beautifyXmlString(requestBody));

            responseWraper = (ReconcillitionResponseWrapper) globalMethods
                    .convertStringToXml(JAXBContext.newInstance(ReconcillitionResponseWrapper.class), requestBody);

            String reqMessage = globalMethods.getStringWithinXmlTag(requestBody, "gepgSpReconcResp");


            reconBatch.setId(responseWraper.getReconcillitionResponse().getReconcBatchInfo().getSpReconcReqId());
            reconBatch.setTransactionReceived(responseWraper.getReconcillitionResponse().getReconcTrxInf().getReconcTrxInf().size());

            reconBatch.setTransactionPresent(paymentRepository.findPaymentByTrxDtm(dateFromId(responseWraper
					.getReconcillitionResponse().getReconcBatchInfo().getSpReconcReqId())).size());

            reconBatchRepository.save(reconBatch);


            boolean verified = globalSignature.verifySignature(responseAckWrapper.getGepgSignature(), reqMessage, gepgPublicPassphrase,
                    gepgPublicAlias, gepgPublicFilePath);

            if (verified) {
                if (!responseWraper.getReconcillitionResponse().getReconcTrxInf().getReconcTrxInf().isEmpty()
                        || responseWraper.getReconcillitionResponse().getReconcTrxInf().getReconcTrxInf() != null) {
                    for (ReconTransactionInfo info : responseWraper.getReconcillitionResponse().getReconcTrxInf()
                            .getReconcTrxInf()) {

                        System.out.println("id: " + info.getSpBillId());
                        Bill bill = billService.selectOneBill(info.getSpBillId());

                        if (info.getSpBillId() != null || !info.getSpBillId().isEmpty()) {

                            reconPayment = new ReconcPayment();
                            reconPayment.setBillAmt(bill.getBilledAmount());
                            reconPayment.setUsdPayChnl(info.getSsdPayChnl());
                            reconPayment.setPaidAmt(new BigDecimal(info.getPaidAmt()));
                            reconPayment.setPayCtrNum(info.getBillCtrNum());
                            reconPayment.setPayRefId(info.getPayRefId());
                            reconPayment.setPspReceiptNumber(info.getPspTrxId());
                            reconPayment.setCreatedDate(info.getTrxDtTm());
                            reconPayment.setPspName(info.getPspName());
                            reconPayment.setPyrCellNum(info.getDptCellNum());
                            reconPayment.setPyrName(info.getDptName());
                            System.out.println("Bill id: " + Long.valueOf(info.getSpBillId().substring(4)));

                            reconPayment.setBill(bill);

                            reconcPaymentService.save(reconPayment);
                        } else {

                        }

                    }
                } else {
                    System.out.println("inside else");
                }

                responseAck.setReconcStsCode("7101");
                String ackRespString = "";

                ackRespString = globalMethods.convertXmlToString(JAXBContext.newInstance(ReconcillitionResponseAck.class),
                        responseAck);

                String signedString = null;
                String responseString = "";

                if (globalMethods.isFileExist(gepgKeyFilePath) && globalMethods.isNullOREmptyString(gepgPassphrase)
                        && globalMethods.isNullOREmptyString(gepgAlias)) {

                    ackRespString = globalMethods.getStringWithinXmlTag(ackRespString, "gepgSpReconcRespAck");

                    signedString = globalSignature.CreateSignature(ackRespString, gepgPassphrase, gepgAlias, gepgKeyFilePath);

                    responseAckWrapper.setGepgSignature(signedString);
                    responseAckWrapper.setGepgSpReconcRespAck(responseAck);

                    responseString = globalMethods.convertXmlToString(
                            JAXBContext.newInstance(ReconcillitionResposeAckWrapper.class), responseAckWrapper);

                    reconcRequest.info("***Final Response Ack ****" + globalMethods.beautifyXmlString(responseString));

                }

                return new ResponseEntity<Object>(responseString, HttpStatus.ACCEPTED);
            } else {
                reconcRequest.info("############## Invalid Content From Gepg ########");
                return null;
            }
        } catch (Exception e) {
            System.out.println("System Errors");
            e.printStackTrace();
            String responseString = "";
            return new ResponseEntity<Object>(responseString, HttpStatus.ACCEPTED);

        }

    }

    @PostMapping(value = "/gepgPayment", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> searchBatch(@PathParam("dateTo") String dateTo, final RedirectAttributes redirectAttributes,
                                                 Model model) {

        redirectAttributes.addFlashAttribute("successMsg", "Payment Found");

        String date = dateTo.replaceAll("/", "-");

        String data[] = date.split("-");

        String newDate = data[2] + "-" + data[0] + "-" + data[1];

        System.out.println("New Date is: " + newDate);

        List<ReconcPayment> reconcPayment = reconcPaymentService.getPaymentFromBatchDay(newDate.trim());

        List<Map<String, Object>> payments = new ArrayList<Map<String, Object>>();
        Map<String, Object> payment = new HashMap<String, Object>();

        if (reconcPayment != null && !reconcPayment.isEmpty()) {


            for (ReconcPayment reco : reconcPayment) {

                payment = new HashMap<String, Object>();

                payment.put("payid", reco.getPaymentId().toString());
                payment.put("billAmt", reco.getBill().getBilledAmount().toString());
                payment.put("createddate", reco.getCreatedDate());
                payment.put("payCtrNum", reco.getPayCtrNum());
                payment.put("ccy", reco.getBill().getCurrency());
                payment.put("mobileno", reco.getPyrCellNum());
                payment.put("paidAmt", reco.getPaidAmt());
                payment.put("payRefId", reco.getPayRefId());
                payment.put("pspName", reco.getPspName());
                payment.put("pspReceiptNumber", reco.getPspReceiptNumber());
                payment.put("bill", reco.getBill());

                payments.add(payment);

            }
        }
        return payments;
    }


    @PostMapping("/dowloadSummary")
    public ResponseEntity<Resource> downloadSuccefullSummary(@PathParam("batch") String batch,
                                                             final RedirectAttributes redirectAttributes, Model model) {

        System.out.println("batch: " + batch);
        List<ReconcPayment> recoList = reconcPaymentService.getPaymentFromBatchDay("2020-05-28");

        System.out.println("size: " + recoList.size());


        String filename = "summary_of_" + batch + ".xlsx";

        File file = new File(filename);
        Path path = Paths.get(file.getAbsolutePath());

        HashMap<Integer, ArrayList<String>> tempData = new HashMap<>();

        // Headers row
        String[] headers = {"paymet_id", "Psp Receipt", "Paid Amount", "Currency", "Mobile Number",
                "Bill ControlNumber", "PaymentReference", "PaymentDate", "Psp Name"};

        ArrayList<String> headersRow = new ArrayList<String>(Arrays.asList(headers));
        tempData.put(0, headersRow);

        int rowNumber = 1;
        ArrayList<String> row = null;

        for (ReconcPayment reco : recoList) {
            row = getArrayForFile(reco);
            tempData.put(rowNumber, row);
            rowNumber++;

        }
        if (FileDataExtractor.writeXLSXFile(file, tempData)) {
            try {
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Content-Disposition", "attachment; filename=" + filename);
                httpHeaders.add("Pragma", "no-cache");
                httpHeaders.add("Expires", "0");
                return ResponseEntity.ok().headers(httpHeaders).contentLength(file.length())
                        .contentType(MediaType.parseMediaType("application/excel")).body(resource);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;

    }

    @PostMapping("/recon-manually")
    public Response getReconFilesManually(Map<String, String> req) {

        return null;
    }

    @GetMapping(path = "/api/batch/get-unreconcilied-transactions", produces = "application/json")
    @ResponseBody
    public PageListResponse<Payment> getAllUnreconcilledTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return paymentService.findAllUnreconciledTransactions(page, size);

    }

    public ArrayList<String> getArrayForFile(ReconcPayment ob) {
        ArrayList<String> toReturn = new ArrayList<>();

        toReturn.add(ob.getPaymentId().toString());
        toReturn.add(ob.getPspReceiptNumber());
        toReturn.add(ob.getPaidAmt().toString());
        toReturn.add(ob.getBill().getCurrency());

        toReturn.add(ob.getPyrCellNum());
        toReturn.add(ob.getPayCtrNum());

        toReturn.add(ob.getPayRefId());
        toReturn.add(ob.getCreatedDate());
        toReturn.add(ob.getPspName());

        return toReturn;
    }

    Date dateFromId(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
		System.out.println(sdf.parse(date));
        return sdf.parse(date);
    }

}