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
import tz.go.mof.trab.service.*;
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


    @Autowired
    private GfsService gfsService;

    @Autowired
    private GepgMiddleWare gepgMiddleWare;


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
                adress.setAdressId(adressRepository.findLastUsedId() + 1);
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

                Optional<Fees> applicationFee = feesRepository.findById("APPLICATION");

                if(applicationFee.isPresent() ==false) {
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

                System.out.println("last id: " + adressRepository.findLastUsedId());
                adress.setAdressId(adressRepository.findLastUsedId() + 1);
                Region region = regionService.getRegionByCode(req.get("region")).getData();
                adress.setRegion(region);

                System.out.println("address object: ");
                TrabHelper.print(adress);

                register.setAdressId(adressRepository.save(adress));

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
                bill.setBilledAmount(new BigDecimal("0"));
                bill.setBillEquivalentAmount(new BigDecimal("0"));
                bill.setMiscellaneousAmount(new BigDecimal(0.0));
                bill.setRemarks("UNPAID");
                bill.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());



                Bill newBill = billRepository.save(bill);
                register.setApplicationNo("APP" + newBill.getBillId());


                BillItems billItems = new BillItems();
                billItems.setBillItemDescription(bill.getBillDescription());
                billItems.setBillItemMiscAmount(new BigDecimal(0.0));
                billItems.setBillItemRef("APPLICATION" + time);
                billItems.setBillItemAmount(applicationFee.get().getAmount());
                billItems.setBillItemEqvAmount(applicationFee.get().getAmount());
                billItems.setGsfCode(applicationFee.get().getGfs().getGfsCode());
                billItems.setSourceName(gfsService.findByGfsCode(applicationFee.get().getGfs().getGfsCode()).getGfsName());
                billItems.setBill(bill);
                billItemRepository.save(billItems);




                Optional < Fees > evidenceFee = feesRepository.findById("ANNEXTURE");

                if (!req.get("annextures").isEmpty()) {
                    for (int i = 0; i < Integer.parseInt(req.get("annextures")); i++) {

                        billItems = new BillItems();
                        billItems.setBillItemDescription(bill.getBillDescription());
                        billItems.setBillItemMiscAmount(new BigDecimal(0.00));
                        billItems.setBillItemRef("Annextures To pleadings");
                        billItems.setBillItemAmount(evidenceFee.get().getAmount());
                        billItems.setBillItemEqvAmount(evidenceFee.get().getAmount());
                        billItems.setGsfCode(evidenceFee.get().getGfs().getGfsCode());
                        billItems.setSourceName(gfsService.findByGfsCode(evidenceFee.get().getGfs().getGfsCode()).getGfsName());
                        billItems.setBill(bill);
                        billItemRepository.save(billItems);
                    }
                }


                if (!req.get("annextures").isEmpty()) {
                    if(Integer.valueOf(req.get("annextures")) > 0){
                    bill.setBilledAmount(bill.getBilledAmount().add(evidenceFee.get().getAmount()
                            .multiply(new BigDecimal(Integer.valueOf(req.get("annextures"))))));


                    bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(evidenceFee.get().getAmount()
                            .multiply(new BigDecimal(Integer.valueOf(req.get("annextures"))))));
                    }
                }

                bill.setBilledAmount(bill.getBilledAmount().add(applicationFee.get().getAmount()));
                bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(applicationFee.get().getAmount()));

                Bill billSaved = billRepository.save(bill);


                if(gepgMiddleWare.sendRequestToGepg(billSaved)){
                    register.setBillId(newBill);
                    register.setApplicationNo("");

                    getApplicationNumber(req, res, currentYear, register, region);
                }else{
                    res.setDescription("Errors code received from gepg is: ");
                    res.setStatus(false);
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
        }else if(taxTypeService.findById(req.get("tax")).getTaxName().equals("CUSTOM AND EXCISE")){
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