package tz.go.mof.trab.controllers;

import javax.xml.bind.JAXBContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.*;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.service.BillService;
import tz.go.mof.trab.utils.GePGGlobalSignature;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;

import java.util.List;
import java.util.Map;


@Controller
public class BillController {


    @Value("${gepg.private.key.passphrase}")
    private String gepgPassphrase;

    @Value("${gepg.private.key.alias}")
    private String gepgAlias;

    @Value("${gepg.private.key.file.path}")
    private String gepgKeyFilePath;

    @Autowired
    private GlobalMethods globalMethods;

    @Autowired
    private BillRepository billRespository;

    @Autowired
    private BillService billService;

    @Autowired
    private GePGGlobalSignature globalSignature;

    private static final Logger logger = Logger.getLogger("trab.bill.request");

    @PostMapping(path = "/bill/receiveControlNumber", consumes = "application/xml", produces = "application/xml")
    @ResponseBody
    public ResponseEntity<Object> receivePayment(@RequestBody String requestBody, @RequestHeader HttpHeaders headers)
            throws Exception {
        BillRespWrapper billResp = new BillRespWrapper();
        BillFinalRespAck respAck = new BillFinalRespAck();
        BillFinalRespAckWrapper respAckWrapper = new BillFinalRespAckWrapper();
        String ackRespString = "";
        String responseString = "";

        logger.info("###### Final Response #######" + globalMethods.beautifyXmlString(requestBody));

        billResp = (BillRespWrapper) globalMethods.convertStringToXml(JAXBContext.newInstance(BillRespWrapper.class),
                requestBody);


        billRespository.editBill(billResp.getBillResp().getTrx().getPayCntrNum(),
                billResp.getBillResp().getTrx().getBillId(), billResp.getBillResp().getTrx().getTrxStsCode());


        respAck.setTrxStsCode("7101");

        ackRespString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillFinalRespAck.class), respAck);

        String signedString = null;

        if (globalMethods.isFileExist(gepgKeyFilePath) && globalMethods.isNullOREmptyString(gepgPassphrase)
                && globalMethods.isNullOREmptyString(gepgAlias)) {

            ackRespString = globalMethods.getStringWithinXmlTag(ackRespString, "gepgBillSubRespAck");
            signedString = globalSignature.CreateSignature(ackRespString, gepgPassphrase, gepgAlias, gepgKeyFilePath);
            respAckWrapper.setGepgSignature(signedString);
            respAckWrapper.setBillFinalAck(respAck);

            responseString = globalMethods.convertXmlToString(JAXBContext.newInstance(BillFinalRespAckWrapper.class),
                    respAckWrapper);

            logger.info("#### Final Response Ack #####" + globalMethods.beautifyXmlString(responseString));
        }

        return new ResponseEntity<Object>(responseString, HttpStatus.ACCEPTED);

    }



    @PostMapping(path = "/bill/generateControlNumber", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String generateControlNumber() {
        return null;

    }

    @RequestMapping(value = "/api/search-bills", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public PageListResponse<Bill> searchBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestBody BillSearchDto billSearchDto) {

        logger.info("#### Search Bill ####");

        List<Bill> billList = billService.searchBills(page, size,billSearchDto);
        Pageable paging = PageRequest.of(page, size);
        Page<Bill> pages = new PageImpl<>(billList, paging, billList.size());
        PageListResponse<Bill> billPageListResponse = new PageListResponse<Bill>();
        billPageListResponse.setCode(ResponseCode.SUCCESS);
        billPageListResponse.setStatus(true);
        billPageListResponse.setData(pages);
        billPageListResponse.setTotalElements(Long.valueOf(billList.size()));
        return billPageListResponse;

    }

    @RequestMapping(value = "/api/bill-response-null", method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public  ListResponse<Bill> getBillResponseCodeNulls() {
        return billService.getResponseCodeNullResponse();
    }


    @RequestMapping(value = "/api/bill-resend-bill", method = RequestMethod.POST,
            produces = "application/json")
    @ResponseBody
    public Response<Bill> resendBill(@RequestBody Map<String, String> req) {
        return billService.billResend(req);
    }

}
