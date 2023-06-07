package tz.go.mof.trab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.jvm.hotspot.ui.SAEditorPane;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.utils.*;
import tz.go.mof.trab.utils.Response;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
public class AppealsServiceImpl implements AppealsService {


    private Date billExpireDate;
    @Value("${tz.go.trab.noOfDays}")
    private int noOfDays;

    @Value("${tz.go.trab.systemid}")
    private String systemId;
    private final AppealStatusTrendRepository appealStatusTrendRepository;
    private final TaxTypeService taxTypeService;
    private final AppealsAmountRepository appealsAmountRepository;
    private final AppealsRepository appealsRepository;
    private final GlobalMethods globalMethods;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final FeesRepository feesRepository;
    private final WitnessRepository witnessRepository;
    private final CurrencyRepository currencyRepository;
    private final FinancialYearService financialYearService;
    private final BillRepository billRepository;
    private final LoggedUser loggedUser;
    private final GfsService gfsService;
    private final BillItemRepository billItemRepository;

    private RegionRepository regionRepository;

    private TaxTypeRepository taxTypeRepository;

    @Autowired
    void setTaxTypeRepository(TaxTypeRepository taxTypeRepository) {
        this.taxTypeRepository = taxTypeRepository;
    }

    @Autowired
    void setRegionRepository(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    private final ManualAppealsSequenceRepository manualAppealsSequenceRepository;


    @Autowired
    private GepgMiddleWare gepgMiddleWare;


    AppealsServiceImpl(AppealStatusTrendRepository appealStatusTrendRepository,
                       GfsService gfsService, TaxTypeService taxTypeService, AppealsAmountRepository appealsAmountRepository,
                       AppealsRepository appealsRepository, GlobalMethods globalMethods,
                       NoticeRepository noticeRepository, UserRepository userRepository, FeesRepository feesRepository,
                       WitnessRepository witnessRepository, CurrencyRepository currencyRepository,
                       FinancialYearService financialYearService, BillRepository billRepository, LoggedUser loggedUser,
                       BillItemRepository billItemRepository, ManualAppealsSequenceRepository manualAppealsSequenceRepository) {
        this.appealStatusTrendRepository = appealStatusTrendRepository;
        this.gfsService = gfsService;
        this.taxTypeService = taxTypeService;
        this.appealsAmountRepository = appealsAmountRepository;
        this.appealsRepository = appealsRepository;
        this.globalMethods = globalMethods;
        this.noticeRepository = noticeRepository;
        this.feesRepository = feesRepository;
        this.userRepository = userRepository;
        this.witnessRepository = witnessRepository;
        this.currencyRepository = currencyRepository;
        this.financialYearService = financialYearService;
        this.billRepository = billRepository;
        this.loggedUser = loggedUser;
        this.billItemRepository = billItemRepository;
        this.manualAppealsSequenceRepository = manualAppealsSequenceRepository;
    }

    @Override
    public Response < Appeals > createAppeal(Map < String, String > request) {
        System.out.println("###### appeal registration ######");
        TrabHelper.print(request);

        Response < Appeals > res = new Response < > ();

        try {
            ObjectMapper mapper = new ObjectMapper();
            List < Map < String, String >> witnessList;
            List < Map < String, String >> amountList;

            Set witnessSet = new HashSet();
            Set AppealAmountSet = new HashSet();
            Appeals app = new Appeals();
            Bill bill = new Bill();

            SystemUser user = userRepository.findById(loggedUser.getInfo().getId()).get();
            Notice notice = noticeRepository.findBynoticeNo(request.get("invoiceNo"));


            witnessList = mapper.readValue(request.get("witnessList"), List.class);
            amountList = mapper.readValue(request.get("amountList"), List.class);


            Date noticeDate = notice.getLoggedAt();

            long diff = globalMethods.getDifferenceInDays(noticeDate, new Date());

            if (diff < 45) {

                Optional < Fees > appealFee = feesRepository.findById("STATEMENT");
                Optional < Fees > witnessFee = feesRepository.findById("WITNESS");
                Optional < Fees > evidenceFee = feesRepository.findById("ANNEXTURE");

                if (!appealFee.isPresent()) {
                    res.setStatus(false);
                    res.setCode(ResponseCode.FAILURE);
                    res.setDescription("No Fees Set For This Appeal application");

                    return res;
                }


                Date date1 = new Date();
                long time = date1.getTime();
                setBillExpireDate(new Date());

                app.setCreatedDate(new Date());
                app.setCurrencyOfAmountOnDispute("TZS");
                app.setAppellantName(notice.getAppelantName());
                app.setAssNo(request.get("assNo"));
                app.setBankNo(request.get("bankNo"));
                app.setBillNo(request.get("billNo"));
                app.setStatus("UNPROCESSED");
                app.setRemarks(request.get("statement").isEmpty() ? "NO REMARKS" : request.get("statement"));
                app.setDateOfFilling(new Date());
                app.setTaxedOff(request.get("taxedOffice"));
                app.setOutcomeOfDecision("NO DECISION");
                app.setNoticeNumber(request.get("invoiceNo"));
                app.setNatureOfAppeal(request.get("natureOfAppeal"));
                app.setIsFilledTrat(false);
                app.setPhone(request.get("phone"));
                app.setEmail(request.get("email"));
                app.setTinNumber(request.get("tinNumber"));
                app.setNatOfBus(request.get("natOf"));
                app.setAction("1");
                app.setCreatedBy(loggedUser.getInfo().getName());


                globalMethods.saveAppellant(request, notice);


                Witness witness;
                AppealAmount appealAmount = new AppealAmount();

                if (witnessList.size() > 0) {

                    for (Map < String, String > witnes: witnessList) {
                        witness = new Witness();
                        witness.setFullName(witnes.get("name"));
                        witness.setPhoneNumber(witnes.get("phone"));

                        Witness savedWit = witnessRepository.save(witness);
                        witnessSet.add(savedWit);
                    }

                    app.setWitnessId(witnessSet);
                }

                AppealAmountSet = saveAmount(AppealAmountSet, amountList);
                app.setAppealAmount(AppealAmountSet);


                // setting up bill details

                bill.setAction("1");
                bill.setBillPayed(false);
                bill.setBillPayType("3");
                bill.setBillReference("APPL-" + time);
                bill.setPaidAmount(new BigDecimal("0"));
                bill.setBilledAmount(new BigDecimal("0"));
                bill.setBillEquivalentAmount(new BigDecimal("0"));
                bill.setBillControlNumber("0");
                bill.setBillDescription("Fee for lodging statement of appeal");
                bill.setPayerEmail(request.get("email") !=null? request.get("email") : "");
                bill.setPayerPhone(request.get("phone")!=null?request.get("phone").replace("-", ""):"0753107301");
                bill.setExpiryDate(getBillExpireDate());
                bill.setApprovedBy(loggedUser.getInfo().getName());
                bill.setCurrency(currencyRepository.findByCurrencyShortName("TZS").getCurrencyShortName());
                bill.setSpSystemId(systemId);
                bill.setGeneratedDate(new java.sql.Date(new java.util.Date().getTime()));
                bill.setAppType("STATEMENT");
                bill.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());


                if (witnessList.size() > 0) {
                    bill.setBilledAmount(bill.getBilledAmount().add(witnessFee.get().getAmount()
                            .multiply(new BigDecimal(witnessSet.size()))));

                    bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(witnessFee.get().getAmount()
                            .multiply(new BigDecimal(witnessSet.size()))));
                }

                if (!request.get("statement").isEmpty()) {
                    bill.setBilledAmount(bill.getBilledAmount().add(evidenceFee.get().getAmount()
                            .multiply(new BigDecimal(Integer.valueOf(request.get("statement"))))));


                    bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(evidenceFee.get().getAmount()
                            .multiply(new BigDecimal(Integer.valueOf(request.get("statement"))))));
                }

                bill.setBilledAmount(bill.getBilledAmount().add(appealFee.get().getAmount()));
                bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(appealFee.get().getAmount()));

                bill.setMiscellaneousAmount(new BigDecimal(0.00));
                bill.setRemarks("UNPAID");
                bill.setPayerName(notice.getAppelantName());


                Bill newBill = billRepository.save(bill);
                BillItems billItems;


                if (witnessList.size() > 0) {
                    for (Map < String, String > wit: witnessList) {
                        billItems = new BillItems();
                        billItems.setBillItemDescription(bill.getBillDescription());
                        billItems.setBillItemMiscAmount(new BigDecimal(0.00));
                        billItems.setBillItemRef("Fee for witness: " + wit.get("name"));
                        billItems.setBillItemAmount(witnessFee.get().getAmount());
                        billItems.setBillItemEqvAmount(witnessFee.get().getAmount());
                        billItems.setGsfCode(witnessFee.get().getGfs().getGfsCode());
                        billItems.setSourceName(gfsService.findByGfsCode(witnessFee.get().getGfs().getGfsCode()).getGfsName());
                        billItems.setBill(bill);
                        billItemRepository.save(billItems);
                    }

                }


                if (!request.get("statement").isEmpty()) {
                    for (int i = 0; i < Integer.parseInt(request.get("statement")); i++) {

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

                // fees for appeals
                billItems = new BillItems();
                billItems.setBillItemDescription(bill.getBillDescription());
                billItems.setBillItemMiscAmount(new BigDecimal(0.00));
                billItems.setBillItemRef("fee for appeal");
                billItems.setBillItemAmount(appealFee.get().getAmount());
                billItems.setBillItemEqvAmount(appealFee.get().getAmount());
                billItems.setGsfCode(appealFee.get().getGfs().getGfsCode());
                billItems.setSourceName(gfsService.findByGfsCode(appealFee.get().getGfs().getGfsCode()).getGfsName());
                billItems.setBill(bill);
                billItemRepository.save(billItems);


                if (gepgMiddleWare.sendRequestToGepg(newBill)) {
                    app.setBillId(newBill);
                    app.setTax(taxTypeService.findById(request.get("typeOfTax")));
                    app.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName("NEW"));


                    ManualAppealsSequence manualAppealsSequence = manualAppealsSequenceRepository.findAll().get(0);
                    int currentYear = notice.getLoggedAt().getYear() + 1900;

                    if(taxTypeService.findById(request.get("typeOfTax")).getTaxName().equals("VAT")) {
                        app.setAppealNo(request.get("region").toUpperCase().toUpperCase() + "." +
                                manualAppealsSequence.getVatSequence()+ "/" + currentYear);
                        manualAppealsSequence.setVatSequence(manualAppealsSequence.getVatSequence()+1);
                        manualAppealsSequenceRepository.save(manualAppealsSequence);

                    }else if(taxTypeService.findById(request.get("typeOfTax")).getTaxName().equals("CUSTOM AND EXCISE")){
                        app.setAppealNo(request.get("region").toUpperCase() + "." +
                                manualAppealsSequence.getCustomSequence()+ "/" + currentYear);
                        manualAppealsSequence.setCustomSequence(manualAppealsSequence.getCustomSequence()+1);
                        manualAppealsSequenceRepository.save(manualAppealsSequence);

                    }else if(taxTypeService.findById(request.get("typeOfTax")).getTaxName().equals("INCOME TAX")){
                        app.setAppealNo(request.get("region").toUpperCase().toUpperCase() + "." +
                                manualAppealsSequence.getIncomeSequence()+ "/" + currentYear);
                        manualAppealsSequence.setIncomeSequence(manualAppealsSequence.getIncomeSequence()+1);
                        manualAppealsSequenceRepository.save(manualAppealsSequence);
                    }

                    newBill.setResponseCode("7101");
                    app.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName("NEW"));
                    appealsRepository.save(app);
                    TimeUnit.SECONDS.sleep(3);

                    res.setStatus(true);
                    res.setDescription("Control number generated Successful");
                    res.setData(app);
                    res.setCode(ResponseCode.SUCCESS);
                } else {
                    res.setStatus(false);
                    res.setDescription("Problem occurred Please Contact Support! ");
                    res.setCode(ResponseCode.FAILURE);
                }

            } else {
                res.setStatus(false);
                res.setDescription("Notice has passed 40 Days Since Registration.! ");
                res.setCode(ResponseCode.FAILURE);
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(false);
            res.setDescription("Problem occurred Please Contact Support! ");
            res.setCode(ResponseCode.FAILURE);
            return res;
        }
    }

    @Override
    public Set saveAmount(Set appealAmountSet, List < Map < String, String >> amountList) {
        AppealAmount appealAmount;
        if (amountList.size() > 0) {
            for (Map < String, String > amount: amountList) {
                appealAmount = new AppealAmount();
                appealAmount.setAmountOnDispute(new BigDecimal(amount.get("amount")));
                Currency currency = currencyRepository.findByCurrencyShortName(amount.get("currency"));
                appealAmount.setCurrency(currency);
                appealAmount.setCurrencyName(currency.getCurrencyShortName());

                AppealAmount appealAmount1 = appealsAmountRepository.save(appealAmount);
                appealAmountSet.add(appealAmount1);
            }
        }
        return appealAmountSet;
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


    public Response uploadAppealManually(Map<String, String> request){
        Appeals appeals = new Appeals();
        Response response = new Response<>();
        List < Map < String, String >> amountList;
        ObjectMapper mapper = new ObjectMapper();
        Set AppealAmountSet = new HashSet();

        try {
            Region region = regionRepository.findById(request.get("region")).get();
            String appealNo = region.getCode() + "." + request.get("appealNo");
            TaxType taxType = taxTypeRepository.findById(request.get("tax")).get();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


            if (appealsRepository.findAppealsByAppealNoAndTax_Id(appealNo,
                    request.get("tax")).size()>0) {

                response = new Response();
                response.setCode(ResponseCode.FAILURE);
                response.setDescription("Appeal already exist");
                response.setStatus(false);
                return response;
            }

            appeals.setAppealNo(appealNo);
            appeals.setAppellantName(request.get("appellantName"));

            appeals.setDateOfFilling(simpleDateFormat.parse(request.get("dateFilling").split("T")[0]));
            appeals.setDecidedDate(simpleDateFormat.parse(request.get("decidedDate").split("T")[0]));
            appeals.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName(request.get("statusTrend")));
            appeals.setDecidedBy(request.get("decidedBy"));
            appeals.setTax(taxTypeService.findById(request.get("tax")));
            appeals.setSummaryOfDecree(request.get("summary"));
            appeals.setTinNumber(request.get("tin"));
            appeals.setPhone(request.get("phone"));
            appeals.setNatureOfAppeal(request.get("nature"));
            appeals.setBillId(null);
            appeals.setCreatedBy("System Created");


            amountList = mapper.readValue(request.get("amountList"), List.class);
            AppealAmountSet = saveAmount(AppealAmountSet, amountList);
            appeals.setAppealAmount(AppealAmountSet);
            appealsRepository.save(appeals);

            response.setDescription("Successful Uploaded");
            response.setCode(ResponseCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Failed to load appeal");
        }
        return response;
    }

}