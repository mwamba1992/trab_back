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
import tz.go.mof.trab.dto.bill.PageListResponse;
import tz.go.mof.trab.dto.payment.PaymentNotificationWrapper;
import tz.go.mof.trab.dto.payment.PaymentResponse;
import tz.go.mof.trab.dto.payment.PaymentResponseWrapper;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.Payment;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.repositories.PaymentRepository;
import tz.go.mof.trab.service.FinancialYearService;
import tz.go.mof.trab.service.PaymentService;
import tz.go.mof.trab.utils.GePGGlobalSignature;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.NumberToWordsUtils;
import tz.go.mof.trab.utils.ResponseCode;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;


@Controller
public class PaymentController {

	@Autowired
	private GlobalMethods globalMethod;

	@Value("${gepg.private.key.passphrase}")
	private String gepgPassphrase;

	@Value("${gepg.private.key.alias}")
	private String gepgAlias;

	@Value("${gepg.private.key.file.path}")
	private String gepgKeyFilePath;

	@Value("${gepg.public.key.file.path}")
	private String gepgPublicFilePath;

	@Value("${gepg.public.key.passphrase}")
	private String gepgPublicPassphrase;

	@Value("${gepg.public.key.alias}")
	private String gepgPublicAlias;

	@Autowired
	private NumberToWordsUtils numToWords;
	 
	@Autowired
	private BillRepository billRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private GePGGlobalSignature globalSignature;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private FinancialYearService financialYearService;

	private static final Logger paymentLogger = Logger.getLogger("trab.payment.request");
	
	@PostMapping(path = "/payment/receivePayment", consumes = "application/xml", produces = "application/xml")
	@ResponseBody
	public ResponseEntity<Object> receivePayment(@RequestBody String requestBody, @RequestHeader HttpHeaders headers) 
			throws Exception {


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		PaymentNotificationWrapper paymentNotificationWrapper = new PaymentNotificationWrapper();
		Payment payment = new Payment();
		PaymentResponse response = new PaymentResponse();
		PaymentResponseWrapper respoWrapper = new PaymentResponseWrapper();
		String ackRespString = "";
		String signedString = "";
		String responseString = "";
		paymentLogger.info("######## SP PaymentInfo Receive ######## " + globalMethod.beautifyXmlString(requestBody));

		requestBody = removeXmlDeclaration(requestBody);

		paymentLogger.info("######## SP PaymentInfo Receive  after removing ######## " + globalMethod.beautifyXmlString(requestBody));
		
		try {


		paymentNotificationWrapper = (PaymentNotificationWrapper) globalMethod
				.convertStringToXml(JAXBContext.newInstance(PaymentNotificationWrapper.class), requestBody);

			String reqMessage = globalMethod.getStringWithinXmlTag(requestBody, "gepgPmtSpInfo");

			//boolean verified = globalSignature.verifySignature(paymentNotificationWrapper.getGepgSignature(), reqMessage, gepgPublicPassphrase,
			//		gepgPublicAlias,gepgPublicFilePath);

			boolean verified = true;

			if(verified){
			if(billRepository.findById(
				paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getBillId()) != null) {

				Bill bill = billRepository.findById(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getBillId()).get();

				bill.setBillPayed(true);
				bill.setPaidAmount(new BigDecimal(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPaidAmt()));
				billRepository.save(bill);

				payment.setBill(bill);
				payment.setAction("1");
				payment.setBillAmt(new BigDecimal(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getBillAmt()));
				payment.setPaidAmt(new BigDecimal(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPaidAmt()));
				payment.setPayCtrNum(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPayCtrNum());
				payment.setPayRefId(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPayRefId());
				payment.setPspName(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPspName());
				payment.setPspReceiptNumber(
						paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPspReceiptNumber());
				payment.setCtrAccNum(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getCtrAccNum());
				payment.setPyrCellNum(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPyrCellNum());
				payment.setPyrName(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPyrName());
				payment.setCreatedDate(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getTrxDtTm());
				payment.setTrxId(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getTrxId());
				payment.setUsdPayChnl(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getUsdPayChnl());
				payment.setTrxDtm(sdf.parse(paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getTrxDtTm()));
				payment.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());

				String result;
				if (paymentRepository.findByPayRefIdAndPspReceiptNumber(
						paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPayRefId(),
						paymentNotificationWrapper.getPymentNot().getPymntTRxInfo().getPspReceiptNumber()) != null) {
					result = "";
				} else {
					result = paymentRepository.save(payment).getPaymentId();

				}
				if (!result.isEmpty()) {
					response.setTrxStsCode("7101");
					ackRespString = globalMethod.convertXmlToString(JAXBContext.newInstance(PaymentResponse.class),
							response);
					if (globalMethod.isFileExist(gepgKeyFilePath) && !globalMethod.isNullOREmptyString(gepgPassphrase)
							&& !globalMethod.isNullOREmptyString(gepgAlias)) {
						ackRespString = globalMethod.getStringWithinXmlTag(ackRespString, "gepgPmtSpInfoAck");
						signedString = globalSignature.CreateSignature(ackRespString, gepgPassphrase, gepgAlias,
								gepgKeyFilePath);
						respoWrapper.setGepgSignature(signedString);
						respoWrapper.setPaymentResponse(response);
						responseString = globalMethod.convertXmlToString(
								JAXBContext.newInstance(PaymentResponseWrapper.class), respoWrapper);
						paymentLogger.info("########### Response For Payment Notification ###################"
								+ globalMethod.beautifyXmlString(responseString));
					}
				}
			}else{
				paymentLogger.info("########### Failed to Verify Payment Details ############");
			}
		}
	       return new ResponseEntity<>(responseString, HttpStatus.ACCEPTED);
		}catch(Exception e){
		  e.printStackTrace();
		  
		}
		
		return null;
	}

	@RequestMapping(value = "/api/search-payments", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public PageListResponse<Payment> searchPayment(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,@RequestBody
					PaymentSearchDto paymentSearchDto) {



		paymentLogger.info("####  Search Payments ####");
		List<Payment> paymentList = paymentService.searchPayments(paymentSearchDto);

		Pageable paging = PageRequest.of(page, size);
		Page<Payment> pages = new PageImpl<Payment>(paymentList, paging, paymentList.size());
		PageListResponse<Payment> billPageListResponse = new PageListResponse<Payment>();
		billPageListResponse.setCode(ResponseCode.SUCCESS);
		billPageListResponse.setStatus(true);
		billPageListResponse.setData(pages);
		billPageListResponse.setTotalElements(Long.valueOf(paymentList.size()));
		return billPageListResponse;

	}


	public String removeXmlDeclaration(String xmlString) {
		return xmlString.replaceFirst("<\\?xml[^>]+\\?>", "");
	}

}
