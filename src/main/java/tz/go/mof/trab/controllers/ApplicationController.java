package tz.go.mof.trab.controllers;

import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.*;
import org.apache.log4j.*;
import org.springframework.beans.factory.annotation.*;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.service.CurrencyService;
import tz.go.mof.trab.service.FinancialYearService;
import tz.go.mof.trab.service.RegionService;
import tz.go.mof.trab.service.TaxTypeService;
import tz.go.mof.trab.utils.*;
import tz.go.mof.trab.repositories.*;
import java.io.File;
import java.io.IOException;
import java.math.*;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.dto.bill.*;
import java.text.*;
import org.apache.commons.lang3.*;
import javax.xml.bind.*;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.utils.Response;
import java.util.*;

@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping({"/application"})
public class ApplicationController {
    private static final Logger billLogger;

    @Autowired
    private SystemUserRepository userRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private BillItemRepository billItemRepository;
    @Autowired
    private GePGGlobalSignature globalSignature;

    @Autowired
    private ApplicationRegisterRepository applicationRegisterRepository;

    @Value("${gepg.private.key.passphrase}")
    private String gepgPassphrase;
    @Value("${gepg.private.key.alias}")
    private String gepgAlias;
    @Value("${tz.go.trab.upload.dir}")
    private String uploadingDir;
    @Value("${gepg.private.key.file.path}")
    private String gepgKeyFilePath;
    @Value("${tz.go.trab.noOfDays}")
    private int noOfDays;
    @Value("${tz.go.trab.systemid}")
    private String systemId;
    @Value("${tz.go.trab.spcode}")
    private String spcode;
    @Value("${tz.go.trab.subspcode}")
    private String subspcode;
    @Value("${tz.go.trab.gepgcom}")
    private String gepgComm;
    @Value("${tz.go.trab.gepgcode}")
    private String gepgCode;
    @Value("${tz.go.trab.gepgurl}")
    private String gepgUrl;
    @Autowired
    GlobalMethods globalMethods;
    private Date billExpireDate;
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private AdressRepository adressRepository;
    @Autowired
    private TaxesRepository taxRepository;
    @Autowired
    private ApplicationStatusTrendRepository statusRepo;
    @Autowired
    private ApplicationRegisterRepository appRepo;
    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TaxTypeService taxTypeService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private FinancialYearService financialYearService;

    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private AppealantRepository appealantRepository;

    @Autowired
    private RespondentRepository respondentRepository;

    @Autowired
    private ApplicationServedByRepository applicationServedByRepository;

    @Autowired
    private ManualApplicationSequenceRepository manualApplicationSequenceRepository;

    @Autowired
    private ApplicationStatusTrendRepository applicationStatusTrendRepository;


    @PostMapping(path = {"/internalCreate"})
    @ResponseBody
    public Response<ApplicationRegister> createUSer(@RequestBody final Map<String, String> req) throws JAXBException {
        Response<ApplicationRegister> res = new Response<ApplicationRegister>();

        Currency currency = currencyService.findByCurrencyShortName("TZS");
        Appellant appealant = new Appellant();
        Respondent respondent = new Respondent();
        SystemUser user = userRepository.findById(loggedUser.getInfo().getId()).get();
        final int currentYear = new Date().getYear() + 1900;
        Notice notice = null;

        TrabHelper.print(req);
        try {




            if (req.get("type").equals("1")) {
                //setting up appealeant details
                respondent.setCreatedDate(new Date());
                respondent.setEmailAdress(req.get("email"));
                respondent.setPhoneNumber(req.get("phone"));
                respondent.setName(req.get("appeleantName"));


                appealant.setCreatedDate(new Date());
                appealant.setEmail("comm@genereal.com");
                appealant.setFirstName("Commisioner General");
                appealant.setPhoneNumber("XXX-XXX-XXXX");

            }  else {
                //setting up appealeant details
                appealant.setCreatedDate(new Date());
                appealant.setEmail(req.get("email"));
                appealant.setPhoneNumber(req.get("phone"));
                appealant.setFirstName(req.get("appeleantName"));


                respondent.setCreatedDate(new Date());
                respondent.setEmailAdress("comm@genereal.com");
                respondent.setName("Commisioner General");
                respondent.setTinNumber("XXX-XXX-XXX");
                respondent.setPhoneNumber("XXX-XXX-XXXX");
            }


            if (req.get("type").equals("1")) {

                ApplicationRegister register = new ApplicationRegister();
                register.setType("1");

                register.setAction("1");
                register.setDateOfFilling(new Date());
                register.setNatureOfRequest(req.get("natureOf"));
                register.setRemarks(req.get("remarks"));
                register.setStatusTrend(statusRepo.findApplicationStatusTrendByApplicationStatusTrendName("NEW"));
                register.setNotice(notice);
                register.setType("2");
                register.setCreatedBy(loggedUser.getInfo().getName());
                register.setDecideBy("NONE");
                register.setTaxes(taxTypeService.findById(req.get("tax")));


                Adress adress = new Adress();
                adress.setSlp(req.get("slp"));
                Region region = regionService.getRegionByCode(req.get("region")).getData();
                adress.setRegion(region);
                register.setAdressId(adressRepository.save(adress));
                register.setBillId(null);

                Appellant newAp = appealantRepository.save(appealant);
                Respondent newRes = respondentRepository.save(respondent);
                register.setApplicant(newAp);
                register.setRespondent(newRes);
                register.setApplicationNo("");
                register.setStatusTrend(applicationStatusTrendRepository.findApplicationStatusTrendByApplicationStatusTrendName("NEW"));

                getApplicationNumber(req, res, currentYear, register, region);

                return  res;

            }else {

                String phoneNumber = req.get("phone").replace("-", "");

                // validate phone
                if(!globalMethods.validatePhoneNumber(phoneNumber)){
                    res.setStatus(false);
                    res.setDescription("Invalid Phone Number");
                    res.setCode(ResponseCode.FAILURE);

                    return res;
                }
                // validate email
                if(!globalMethods.isValidEmailAddress(req.get("email"))){
                    res.setStatus(false);
                    res.setDescription("Invalid email adress");
                    res.setCode(ResponseCode.FAILURE);

                    return res;
                }

                Optional<Fees> fees = feesRepository.findById("APPLICATION");

                if(fees.isPresent() ==false) {
                    res.setStatus(false);
                    res.setCode(ResponseCode.FAILURE);
                    res.setDescription("No Fees Set For This Operation");

                    return  res;
                }


                Bill bill = new Bill();
                Date date1 = new Date();
                long time = date1.getTime();
                this.setBillExpireDate(new Date());
                ApplicationRegister register = new ApplicationRegister();
                Adress adress = new Adress();
                adress.setSlp(req.get("slp"));
                Region region = regionService.getRegionByCode(req.get("region")).getData();
                adress.setRegion(region);

                register.setAction("1");
                register.setCreatedBy(loggedUser.getInfo().getName());
                register.setDateOfFilling(new Date());
                register.setNatureOfRequest(req.get("natureOf"));
                register.setRemarks(req.get("remarks"));
                register.setStatusTrend(statusRepo.findApplicationStatusTrendByApplicationStatusTrendName("NEW"));
                register.setNotice(notice);

                Appellant newAp = appealantRepository.save(appealant);
                Respondent newRes = respondentRepository.save(respondent);

                register.setType("2");
                register.setApplicant(newAp);
                register.setRespondent(newRes);
                register.setDecideBy("NONE");
                register.setTaxes(taxTypeService.findById(req.get("tax")));
                bill.setAction("1");
                bill.setBillPayed(false);
                bill.setBillPayType("3");
                bill.setBillReference("APP-" + time);
                bill.setBillControlNumber("0");
                bill.setBillDescription("Fee For lodging Application");
                bill.setPayerEmail((String) req.get("email"));
                bill.setPayerPhone((String) req.get("phone").replace("-", ""));
                bill.setPayerName((String) req.get("appeleantName"));
                bill.setPaidAmount(new BigDecimal("0"));
                bill.setExpiryDate(this.getBillExpireDate());
                bill.setApprovedBy(loggedUser.getInfo().getName());
                bill.setCurrency(currency.getCurrencyShortName());
                bill.setSpSystemId(this.systemId);
                bill.setGeneratedDate((Date) new java.sql.Date(new Date().getTime()));
                bill.setAppType("APPLICATION");
                bill.setBilledAmount(fees.get().getAmount());
                bill.setBillEquivalentAmount(fees.get().getAmount());
                bill.setMiscellaneousAmount(new BigDecimal(0.0));
                bill.setRemarks("UNPAID");
                bill.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());
                register.setAdressId(adressRepository.save(adress));
                Bill newBill = billRepository.save(bill);
                register.setApplicationNo("APP" + newBill.getBillId());
                BillWrapper billWrapper = new BillWrapper();
                BillAckWrapper billAck = new BillAckWrapper();
                BillItems billItems = new BillItems();
                BillMapperHeaderDto billMapperHeaderDto = new BillMapperHeaderDto();
                BillMapperDto billMapperDto = new BillMapperDto();
                BillMapperDetailsDto billMapperDetailsDto = new BillMapperDetailsDto();
                BillItemMapperDto billItemMapperDto = new BillItemMapperDto();
                BillItemsMapperDto billItemsMapperDto = new BillItemsMapperDto();
                List<BillMapperDetailsDto> listOfBillDetailsDto = new ArrayList<BillMapperDetailsDto>();
                List<BillItemMapperDto> listOfBillItemsDto = new ArrayList<BillItemMapperDto>();
                billMapperHeaderDto.setRtrRespFlg(true);
                billMapperHeaderDto.setSpCode(this.spcode);
                billMapperDto.setBillHeaders(billMapperHeaderDto);
                billMapperDetailsDto.setSpBillId(newBill.getBillId());
                billMapperDetailsDto.setSubSpCode(this.subspcode);
                billMapperDetailsDto.setSpSysId(this.systemId);
                billMapperDetailsDto.setBilledAmount(bill.getBilledAmount());
                billMapperDetailsDto.setMiscellaneousAmount(bill.getMiscellaneousAmount());
                billMapperDetailsDto.setExpiryDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(bill.getExpiryDate()));
                billMapperDetailsDto.setSpPyrId(StringEscapeUtils.escapeXml(bill.getPayerName()));
                billMapperDetailsDto.setSpPyrName(StringEscapeUtils.escapeXml(bill.getPayerName()));
                billMapperDetailsDto.setBillDescription(bill.getBillDescription());
                billMapperDetailsDto.setGeneratedDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(bill.getGeneratedDate().getTime()));
                billMapperDetailsDto.setUser(loggedUser.getInfo().getId());
                billMapperDetailsDto.setApprovedBy(bill.getApprovedBy());
                billMapperDetailsDto.setPayerPhone(bill.getPayerPhone());
                billMapperDetailsDto.setPayerEmail(bill.getPayerEmail());
                billMapperDetailsDto.setCurrency(currency.getCurrencyShortName());
                billMapperDetailsDto.setBillEquivalentAmount(bill.getBillEquivalentAmount());
                billMapperDetailsDto.setReminderFlag(false);
                billMapperDetailsDto.setBillPayType(Long.parseLong(bill.getBillPayType()));
                billItems.setBillItemDescription(bill.getBillDescription());
                billItems.setBillItemMiscAmount(new BigDecimal(0.0));
                billItems.setBillItemRef("APPLICATION" + time);
                billItems.setBillItemAmount(bill.getBilledAmount());
                billItems.setBillItemEqvAmount(bill.getBillEquivalentAmount());
                billItems.setBill(bill);
                final BillItems billItemToSerialize = billItemRepository.save(billItems);
                billItemMapperDto.setBillItemReference(bill.getBillReference());
                billItemMapperDto.setGf(fees.get().getGfs().getGfsCode());
                billItemMapperDto.setItemBilledAmount(bill.getBilledAmount());
                billItemMapperDto.setItemEquivalentAmount(bill.getBillEquivalentAmount());
                billItemMapperDto.setItemMiscellaneousAmount(billItemToSerialize.getBillItemMiscAmount());
                billItemMapperDto.setUseItemRefOnPay("N");
                billMapperDetailsDto.setBillItems(billItemsMapperDto);
                listOfBillItemsDto.add(billItemMapperDto);
                billItemsMapperDto.setBillItem((List) listOfBillItemsDto);
                listOfBillDetailsDto.add(billMapperDetailsDto);
                billMapperDto.setBillDetails((List) listOfBillDetailsDto);


                String billXmlString = this.globalMethods.convertXmlToString(JAXBContext.newInstance(BillMapperDto.class), (Object) billMapperDto);
                String signedString = "";
                if (this.globalMethods.isFileExist(this.gepgKeyFilePath) && !this.globalMethods.isNullOREmptyString(this.gepgPassphrase) && !this.globalMethods.isNullOREmptyString(this.gepgAlias)) {
                    billXmlString = this.globalMethods.getStringWithinXmlTag(billXmlString, "gepgBillSubReq");
                    signedString = this.globalSignature.CreateSignature(billXmlString, this.gepgPassphrase, this.gepgAlias, this.gepgKeyFilePath);
                    billWrapper.setGepgSignature(signedString);
                    billWrapper.setGepgBillSubReq(billMapperDto);
                    final Hashtable<String, String> hashtable = new Hashtable<String, String>();
                    hashtable.put("Gepg-Com", this.gepgComm);
                    hashtable.put("Gepg-Code", this.gepgCode);
                    final String contentSentToGepg = this.globalMethods.convertXmlToString(JAXBContext.newInstance(BillWrapper.class), (Object) billWrapper);
                    System.out.println("*****" + contentSentToGepg);
                    ApplicationController.billLogger.info((Object) ("**Request To Gepg***" + this.globalMethods.beautifyXmlString(contentSentToGepg) + "\n"));
                    try {
                        final String response = this.globalMethods.connectToAnotherSystem(this.gepgUrl + "api/bill/sigqrequest ", contentSentToGepg, "AXML", "AXML", (Hashtable) hashtable);
                        ApplicationController.billLogger.info((Object) ("*****bill response***" + this.globalMethods.beautifyXmlString(response) + "\n"));
                        billAck = (BillAckWrapper) this.globalMethods.convertStringToXml(JAXBContext.newInstance(BillAckWrapper.class), response);

                        newBill.setResponseCode(billAck.getBillAckCode().getTrxStsCode());
                        billRepository.save(newBill);

                        if (billAck.getBillAckCode().getTrxStsCode().equals("7101")) {
                            register.setBillId(newBill);
                            register.setApplicationNo("");

                            getApplicationNumber(req, res, currentYear, register, region);
                        } else {
                            res.setDescription("Errors code received from gepg is: " + billAck.getBillAckCode().getTrxStsCode());
                            res.setStatus(false);
                            res.setCode(ResponseCode.FAILURE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        res.setDescription("Errors sending requests to gepg");
                        res.setStatus(false);
                        res.setCode(ResponseCode.FAILURE);
                    }
                } else {
                    System.out.println("##### keys not configured from path #####");
                    res.setStatus(false);
                    res.setDescription("Keys Not Configured");
                    res.setCode(ResponseCode.FAILURE);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            res.setDescription("Errors sending requests to gepg");
            res.setStatus(false);
            res.setCode(ResponseCode.FAILURE);
        }
        return res;
    }

    private void getApplicationNumber(@RequestBody Map<String, String> req, Response<ApplicationRegister> res, int currentYear, ApplicationRegister register, Region region) {
        ManualApplicationSequence manualApplicationSequence = manualApplicationSequenceRepository.findAll().get(0);

        if(taxTypeService.findById(req.get("tax")).getTaxName().equals("VAT")) {
            register.setApplicationNo(region.getCode().toUpperCase() + "." +manualApplicationSequence.getVatSequence() + "/" + currentYear);
            manualApplicationSequence.setVatSequence(manualApplicationSequence.getVatSequence()+1);
            manualApplicationSequenceRepository.save(manualApplicationSequence);
        }else if(taxTypeService.findById(req.get("tax")).getTaxName().equals("CUSTOM AND EXERCISE")){
            register.setApplicationNo(region.getCode().toUpperCase() + "." +manualApplicationSequence.getCustomSequence() + "/" + currentYear);
            manualApplicationSequence.setCustomSequence(manualApplicationSequence.getCustomSequence()+1);
            manualApplicationSequenceRepository.save(manualApplicationSequence);
        }else if(taxTypeService.findById(req.get("tax")).getTaxName().equals("INCOME TAX")){
            register.setApplicationNo(region.getCode().toUpperCase() + "." +manualApplicationSequence.getIncomeSequence() + "/" + currentYear);
            manualApplicationSequence.setIncomeSequence(manualApplicationSequence.getIncomeSequence()+1);
            manualApplicationSequenceRepository.save(manualApplicationSequence);
        }

        ApplicationRegister newApp = appRepo.save(register);

        res.setStatus(true);
        res.setDescription("Control number generated Successful");
        res.setCode(ResponseCode.SUCCESS);
        res.setData(newApp);
    }

    @PostMapping({"/internalEdit"})
    @ResponseBody
    public Response<ApplicationRegister> editUser(@RequestBody final Map<String, String> req) {

        System.out.println("##########" + req);


        Response<ApplicationRegister> res = new Response<ApplicationRegister>();
        try {
            final ApplicationRegister app = this.appRepo.findByapplicationNo((String) req.get("appNo"));
            app.setWonBy(req.get("wonBy"));
            app.setDecideBy((String) req.get("decidedBy"));
            app.setDesicionSummary(req.get("remarks").getBytes());
            app.setRemarks((String) req.get("remarks"));
            app.setStatusTrend(statusRepo.findApplicationStatusTrendByApplicationStatusTrendName(req.get("statusTrend")));
            app.setDateOfDecision(new SimpleDateFormat("YYYY-MM-DD").parse(req.get("date").split("T")[0]));
            app.setFilePath(req.get("fileName"));
            app.setAction("2");
            app.setCreatedBy(loggedUser.getInfo().getName());


            String filePath = "";
            String binaryData = "";
            if (req.get("file") != null) {
                //check if LocationSketch available then upload
                binaryData = req.get("file");
                filePath = req.get("fileName");

            }

            if (!new File(uploadingDir).exists()) {
                boolean success = new File(uploadingDir).mkdirs();
                if (!success) {
                }
            }

            File fileToCreate = new File(uploadingDir, filePath);


            if (!fileToCreate.exists()) {
                byte[] decodedBytes = Base64.getDecoder().decode(binaryData);
                FileUtils.writeByteArrayToFile(fileToCreate, decodedBytes);
            }


            appRepo.save(app);
            res.setStatus(true);
            res.setDescription("Error occcure while editing Appeal");
            res.setCode(ResponseCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(false);
            res.setCode(ResponseCode.FAILURE);
            res.setDescription("Error occcure while editing Appeal");
        }
        return res;
    }

    public Date getBillExpireDate() {
        return this.billExpireDate;
    }

    public void setBillExpireDate(final Date billExpireDate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(billExpireDate);
        calendar.add(6, this.noOfDays);
        final Date date = calendar.getTime();
        this.billExpireDate = date;
    }

    static {
        billLogger = Logger.getLogger("trab.bill.request");
    }

    @PostMapping(path = "/update-served-by", produces = "application/json")
    @ResponseBody
    public Response<ApplicationServedBy> updateServedBy(@RequestBody Map<String, String> req) throws IllegalStateException, IOException {

        System.out.println("#### Req is ###### " + req);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Response<ApplicationServedBy> appealsResponse = new Response<ApplicationServedBy>();
        try{

            ApplicationRegister applicationRegister = appRepo.findByapplicationNo(req.get("appNo"));
            applicationRegister.setAction("2");
            applicationRegister.setCreatedBy(loggedUser.getInfo().getName());

            ApplicationServedBy applicationServedBy = new ApplicationServedBy();
            applicationServedBy.setAppName(req.get("appName"));
            applicationServedBy.setAppPhone(req.get("appPhone"));
            applicationServedBy.setAppDate(formatter.parse(req.get("appDate").split("T")[0]));

            applicationServedBy.setResoName(req.get("resoName"));
            applicationServedBy.setResoPhone(req.get("resoPhone"));
            applicationServedBy.setResoDate(formatter.parse(req.get("resoDate").split("T")[0]));

            ApplicationServedBy newApplicationServedBy = applicationServedByRepository.save(applicationServedBy);

            applicationRegister.setApplicationServedBy(newApplicationServedBy);
            appRepo.save(applicationRegister);

            appealsResponse.setDescription("Succefull");
            appealsResponse.setStatus(true);
            appealsResponse.setCode(ResponseCode.SUCCESS);
            appealsResponse.setData(applicationServedBy);

        }catch (Exception e){
            e.printStackTrace();
            appealsResponse.setCode(ResponseCode.FAILURE);
            appealsResponse.setStatus(false);
            appealsResponse.setData(null);
            appealsResponse.setDescription("System Temporary unavailable");

        }
        return appealsResponse;
    }

    @PostMapping(path = "/applications-date", produces = "application/json")
    @ResponseBody
    public Page<ApplicationRegister> getAppealsByDateRange(@RequestBody Map<String, String> req) throws IllegalStateException {

        TrabHelper.print(req);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Pageable pageable = PageRequest.of(0, 10000, Sort.by("appealId").descending());
        Date start = null;
        Date end = null;
        try {
            start = formatter.parse(req.get("dateFrom").split("T")[0]);
            end = formatter.parse(req.get("dateTo").split("T")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(req.get("tax") == null || req.get("tax").isEmpty())
            return  applicationRegisterRepository.findApplicationRegisterByDateOfFillingBetween(start, end, pageable);
        else{
            return applicationRegisterRepository.findApplicationRegisterByDateOfFillingBetweenAndTaxes_Id(start, end, req.get("tax"), pageable);
        }

    }
}