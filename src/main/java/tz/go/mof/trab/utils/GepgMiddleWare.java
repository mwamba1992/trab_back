package tz.go.mof.trab.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.bind.JAXBContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.dto.bill.*;
import tz.go.mof.trab.dto.payment.ReconcillitionRequest;
import tz.go.mof.trab.dto.payment.ReconcillitionRequestWrapper;
import tz.go.mof.trab.dto.payment.ReconcillitionResponseAck;
import tz.go.mof.trab.dto.payment.ReconcillitionResposeAckWrapper;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.BillItems;
import tz.go.mof.trab.repositories.BillItemRepository;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.service.BillService;


@Component
public class GepgMiddleWare {

    private static final Logger billLogger = Logger.getLogger("trab.bill.request");

    private static final Logger reconcRequest = Logger.getLogger("trab.reconc.request");

    @Value("${tz.go.trab.spcode}")
    private String spcode;

    @Value("${tz.go.trab.systemid}")
    private String systemId;

    @Value("${tz.go.trab.subspcode}")
    private String subspcode;

    @Value("${gepg.private.key.passphrase}")
    private String gepgPassphrase;

    @Value("${gepg.private.key.alias}")
    private String gepgAlias;

    @Value("${gepg.private.key.file.path}")
    private String gepgKeyFilePath;

    @Value("${tz.go.trab.gepgcom}")
    private String gepgComm;

    @Value("${tz.go.trab.gepgcode}")
    private String gepgCode;

    @Value("${tz.go.trab.gepgurl}")
    private String gepgUrl;

    private final BillItemRepository billItemRepository;
    @Autowired
    private BillService billService;
    @Autowired
    private GlobalMethods globalMethods;

    private final BillRepository billRepository;
    private final GePGGlobalSignature gePGGlobalSignature;


    GepgMiddleWare(BillItemRepository billItemRepository, BillRepository billRepository, GePGGlobalSignature gePGGlobalSignature){
        this.billItemRepository = billItemRepository;
        this.billRepository = billRepository;
        this.gePGGlobalSignature = gePGGlobalSignature;
    }

    public boolean sendRequestToGepg(Bill bill) {


        try {

            Hashtable<String, String> hashtable = new Hashtable<>();
            BillWrapper billWrapper = new BillWrapper();
            BillAckWrapper billAck;
            BillMapperHeaderDto billMapperHeaderDto = new BillMapperHeaderDto();
            BillMapperDto billMapperDto = new BillMapperDto();
            BillMapperDetailsDto billMapperDetailsDto = new BillMapperDetailsDto();
            BillItemMapperDto billItemMapperDto = new BillItemMapperDto();
            BillItemsMapperDto billItemsMapperDto = new BillItemsMapperDto();
            List<BillMapperDetailsDto> listOfBillDetailsDto = new ArrayList<>();
            List<BillItemMapperDto> listOfBillItemsDto = new ArrayList<>();

            billMapperHeaderDto.setSpCode(spcode);
            billMapperDetailsDto.setSubSpCode(subspcode);
            hashtable.put("Gepg-Code", gepgCode);


            billMapperHeaderDto.setRtrRespFlg(true);
            billMapperDto.setBillHeaders(billMapperHeaderDto);
            billMapperDetailsDto.setSpBillId(bill.getBillId() + "");
            billMapperDetailsDto.setSpSysId(systemId);
            billMapperDetailsDto.setBilledAmount(bill.getBilledAmount());
            billMapperDetailsDto.setMiscellaneousAmount(bill.getMiscellaneousAmount());
            billMapperDetailsDto
                    .setExpiryDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(bill.getExpiryDate()));
            billMapperDetailsDto.setSpPyrId(StringEscapeUtils.escapeXml(bill.getPayerName()));
            billMapperDetailsDto.setSpPyrName(StringEscapeUtils.escapeXml(bill.getPayerName()));
            billMapperDetailsDto.setBillDescription(bill.getBillDescription());
            billMapperDetailsDto.setGeneratedDate(
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(bill.getGeneratedDate().getTime()));
            billMapperDetailsDto.setUser(bill.getApprovedBy());
            billMapperDetailsDto.setApprovedBy(bill.getApprovedBy());
            billMapperDetailsDto.setPayerPhone(bill.getPayerPhone().contains(",")? bill.getPayerPhone().split(",")[0]:bill.getPayerPhone());
            billMapperDetailsDto.setPayerEmail(bill.getPayerEmail());
            billMapperDetailsDto.setCurrency(bill.getCurrency());
            billMapperDetailsDto.setBillEquivalentAmount(bill.getBillEquivalentAmount());
            billMapperDetailsDto.setReminderFlag(false);
            billMapperDetailsDto.setBillPayType(Long.parseLong(bill.getBillPayType()));
            billItemMapperDto.setBillItemReference(bill.getBillReference());
            billMapperDetailsDto.setBillItems(billItemsMapperDto);

            List<BillItems> billItemList = billItemRepository.getBillItemOfTheSameBill(bill.getBillId());

            if (billItemList.size() > 0) {
                billItemList.forEach(item -> {
                    BillItemMapperDto billItemMapper = new BillItemMapperDto();
                    billItemMapper.setBillItemReference("TRAB-" + item.getBillItemRefId());
                    billItemMapper.setGf(item.getGsfCode());
                    billItemMapper.setItemBilledAmount(item.getBillItemAmount());
                    billItemMapper.setItemEquivalentAmount(item.getBillItemEqvAmount());
                    billItemMapper.setItemMiscellaneousAmount(new BigDecimal("0.00"));
                    billItemMapper.setUseItemRefOnPay("N");
                    listOfBillItemsDto.add(billItemMapper);
                });
            }

            billItemsMapperDto.setBillItem(listOfBillItemsDto);
            listOfBillDetailsDto.add(billMapperDetailsDto);
            billMapperDto.setBillDetails(listOfBillDetailsDto);

            String billXmlString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillMapperDto.class),
                    billMapperDto);

            String signedString;
            String keyChosenPath;


            keyChosenPath = gepgKeyFilePath;

            billLogger.info("######## Kay Paths ######" + keyChosenPath);

            if (globalMethods.isFileExist(keyChosenPath) && !globalMethods.isNullOREmptyString(gepgPassphrase)
                    && !globalMethods.isNullOREmptyString(gepgAlias)) {

                billXmlString = globalMethods.getStringWithinXmlTag(billXmlString, "gepgBillSubReq");
                signedString = gePGGlobalSignature.CreateSignature(billXmlString, gepgPassphrase, gepgAlias,
                        keyChosenPath);
                billWrapper.setGepgSignature(signedString);
                billWrapper.setGepgBillSubReq(billMapperDto);
                String contentSentToGepg = globalMethods.convertXmlToString(JAXBContext.newInstance(BillWrapper.class),
                        billWrapper);


                billLogger.info("####### Bill Request to Gepg  ######" + globalMethods.beautifyXmlString(contentSentToGepg));
                System.out.println("#### request to gepg #####");
                System.out.println(globalMethods.beautifyXmlString(contentSentToGepg));

                String response;

                try {

                    hashtable.put("Gepg-Com", gepgComm);
                    String url;


                    hashtable.put("Gepg-Com", gepgComm);
                    url = "api/bill/sigqrequest ";
                    response = globalMethods.connectToAnotherSystem(gepgUrl + url,
                            contentSentToGepg, "AXML", "AXML", hashtable);

                    billLogger.info("####### Response From Gepg ######" + globalMethods.beautifyXmlString(response));
                    System.out.println(globalMethods.beautifyXmlString(response));

                    billAck = (BillAckWrapper) globalMethods
                            .convertStringToXml(JAXBContext.newInstance(BillAckWrapper.class), response);

                    bill.setResponseCode(billAck.getBillAckCode().getTrxStsCode());
                    billRepository.save(bill);

                    return billAck.getBillAckCode().getTrxStsCode().equals("7101");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;

                }

            } else {
                billLogger.info("######### Keys Not Find #########");
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String constructAckToGepg() throws Exception {

        BillFinalRespAck respAck = new BillFinalRespAck();
        BillFinalRespAckWrapper respAckWrapper = new BillFinalRespAckWrapper();

        respAck.setTrxStsCode("7101");

        String ackRespString;
        String responseString = "";

        ackRespString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillFinalRespAck.class), respAck);

        String signedString;


        String keyChosenPath;

        keyChosenPath = gepgKeyFilePath;

        if (globalMethods.isFileExist(keyChosenPath) && globalMethods.isNullOREmptyString(gepgPassphrase)
                && globalMethods.isNullOREmptyString(gepgAlias)) {

            ackRespString = globalMethods.getStringWithinXmlTag(ackRespString, "gepgBillSubRespAck");

            signedString = gePGGlobalSignature.CreateSignature(ackRespString, gepgPassphrase, gepgAlias, keyChosenPath);

            respAckWrapper.setGepgSignature(signedString);
            respAckWrapper.setBillFinalAck(respAck);

            responseString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillFinalRespAckWrapper.class),
                    respAckWrapper);

            billLogger.info("#### Final Response Ack ####" + globalMethods.beautifyXmlString(responseString));
        }
        return responseString;
    }

    public String constructReconAck() throws Exception {
        ReconcillitionResponseAck responseAck = new ReconcillitionResponseAck();
        ReconcillitionResposeAckWrapper responseAckWrapper = new ReconcillitionResposeAckWrapper();

        responseAck.setReconcStsCode("7101");
        String ackRespString;

        ackRespString = globalMethods.convertXmlToString(JAXBContext.newInstance(ReconcillitionResponseAck.class),
                responseAck);

        String signedString;
        String responseString = "";
        String keyChosenPath;

        keyChosenPath = gepgKeyFilePath;


        if (globalMethods.isFileExist(keyChosenPath) && globalMethods.isNullOREmptyString(gepgPassphrase)
                && globalMethods.isNullOREmptyString(gepgAlias)) {
            ackRespString = globalMethods.getStringWithinXmlTag(ackRespString, "gepgSpReconcRespAck");

            signedString = gePGGlobalSignature.CreateSignature(ackRespString, gepgPassphrase, gepgAlias, keyChosenPath);

            responseAckWrapper.setGepgSignature(signedString);
            responseAckWrapper.setGepgSpReconcRespAck(responseAck);

            responseString = globalMethods.convertXmlToString(
                    JAXBContext.newInstance(ReconcillitionResposeAckWrapper.class), responseAckWrapper);

            billLogger.info("#### Final Response Ack ####" + globalMethods.beautifyXmlString(responseString));

        }

        return responseString;
    }


    public Boolean sendCancelRequest(Bill bill) throws Exception {

        BillCancelReq cancelReq = new BillCancelReq();
        BillCancelReqWrapper cancelReqWrapper = new BillCancelReqWrapper();
        BillCancelResWrapper respWrapper;
        String cancelString;
        String spCode = "SP" + bill.getBillControlNumber().substring(2, 5);

        cancelReq.setSpCode(spCode);
        cancelReq.setBillId(bill.getBillId());
        cancelReq.setSpSysId(systemId);


        cancelString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillCancelReq.class), cancelReq);
        String signedString;
        String requestString;

        String keyChosenPath;

        keyChosenPath = gepgKeyFilePath;


        if (globalMethods.isFileExist(gepgKeyFilePath) && globalMethods.isNullOREmptyString(gepgPassphrase)
                && globalMethods.isNullOREmptyString(gepgAlias)) {
            cancelString = globalMethods.getStringWithinXmlTag(cancelString, "gepgBillCanclReq");

            signedString = gePGGlobalSignature.CreateSignature(cancelString, gepgPassphrase, gepgAlias, keyChosenPath);

            cancelReqWrapper.setGepgSignature(signedString);
            cancelReqWrapper.setBillCancelReq(cancelReq);


            requestString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillCancelReqWrapper.class),
                    cancelReqWrapper);

            billLogger.info("####### Cancel Request ########" + globalMethods.beautifyXmlString(requestString));

            String responseString;

            try {

                Hashtable<String, String> hashtable = new Hashtable<>();
                hashtable.put("Gepg-Com", gepgComm);
                hashtable.put("Gepg-Code", spCode);

                System.out.println("######## Maps #######" + hashtable);

                responseString = globalMethods.connectToAnotherSystem(gepgUrl + "api/bill/sigcancel_request",
                        requestString, "AXML", "AXML", hashtable);

                billLogger.info("########  Bill Cancel Response #########" + globalMethods.beautifyXmlString(responseString));

                respWrapper = (BillCancelResWrapper) globalMethods
                        .convertStringToXml(JAXBContext.newInstance(BillCancelResWrapper.class), responseString);

                if (respWrapper.getGepgBillCanclResp().getGepgBillCanclResp().get(0).getTrxStsCode().equals("7283")) {
                    bill.setRemarks("CANCELLED");
                    billService.saveBill(bill);
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                billLogger.error("############" + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return null;
    }


    public Boolean sendReconBatch(String date) throws Exception {

        String reqXml;
        ReconcillitionRequest request = new ReconcillitionRequest();
        ReconcillitionRequestWrapper requestWrapper = new ReconcillitionRequestWrapper();

        System.out.println("date is: " + date);
        request.setSpCode(spcode);
        request.setSpSysId(systemId);
        request.setSpReconcReqId(date.replaceAll("-", "") +
                new Date().getHours() + new Date().getMinutes());
        request.setTnxDt(date);
        //request.setTnxDt("2020-05-28");

        request.setReconcOpt("1");

        reqXml = globalMethods.convertXmlToString(JAXBContext.newInstance(ReconcillitionRequest.class), request);

        String signedString;

        String requestString;

        if (globalMethods.isFileExist(gepgKeyFilePath) && globalMethods.isNullOREmptyString(gepgPassphrase)
                && globalMethods.isNullOREmptyString(gepgAlias)) {
            reqXml = globalMethods.getStringWithinXmlTag(reqXml, "gepgSpReconcReq");

            signedString = gePGGlobalSignature.CreateSignature(reqXml, gepgPassphrase, gepgAlias, gepgKeyFilePath);

            requestWrapper.setGepgSignature(signedString);
            requestWrapper.setGepgSpReconcReq(request);

            requestString = globalMethods
                    .convertXmlToString(JAXBContext.newInstance(ReconcillitionRequestWrapper.class), requestWrapper);

            System.out.println(globalMethods.beautifyXmlString(requestString));


            reconcRequest.info("##### Reconciliation Request ######### " + globalMethods.beautifyXmlString(requestString));

            String responseString = "";

            try {
                Hashtable<String, String> hashtable = new Hashtable<>();
                hashtable.put("Gepg-Com", gepgComm);
                hashtable.put("Gepg-Code", gepgCode);

                responseString = globalMethods.connectToAnotherSystem(gepgUrl + "api/reconciliations/sig_sp_qrequest",
                        requestString, "AXML", "AXML", hashtable);

                System.out.println("Response: " + globalMethods.beautifyXmlString((responseString)));

                reconcRequest
                        .info("########  Response From Recon Request is ############# " + globalMethods.beautifyXmlString(responseString));

                requestWrapper = (ReconcillitionRequestWrapper) globalMethods.convertStringToXml(
                        JAXBContext.newInstance(ReconcillitionRequestWrapper.class), responseString);

            } catch (Exception e) {

                reconcRequest.info(e.getMessage());
                reconcRequest.info("###### Exception has occurred while sending Recon File ########" + responseString);

            }
        }

        return true;
    }
}
