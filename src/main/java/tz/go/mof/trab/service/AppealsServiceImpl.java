package tz.go.mof.trab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.appeal.BacklogAppealDto;
import tz.go.mof.trab.dto.appeal.CreateAppealDto;
import tz.go.mof.trab.dto.appeal.RetrialDto;
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

    private static final Logger log = LoggerFactory.getLogger(AppealsServiceImpl.class);

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

    @Autowired
    private DecisionHistoryRepository decisionHistoryRepository;

    @Autowired
    private AppellantService appellantService;


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
    public Response < Appeals > createAppeal(CreateAppealDto request) {
        log.info("Processing appeal registration");
        log.debug("Appeal request: {}", request);

        Response < Appeals > res = new Response < > ();
        final int currentYear = new Date().getYear() + 1900;

        try {
            ObjectMapper mapper = new ObjectMapper();
            List < Map < String, String >> witnessList;
            List < Map < String, String >> amountList;

            Set<Witness> witnessSet = new HashSet<>();
            Set<AppealAmount> appealAmountSet = new HashSet<>();
            Appeals app = new Appeals();
            Bill bill = new Bill();

            Notice notice = noticeRepository.findBynoticeNo(request.getInvoiceNo());


            witnessList = mapper.readValue(request.getWitnessList(), List.class);
            amountList = mapper.readValue(request.getAmountList(), List.class);


            Date noticeDate = notice.getLoggedAt();

            long diff = globalMethods.getDifferenceInDays(noticeDate, new Date());


            log.debug("Appeal validation - Days since notice: {}, Exempted: {}, Reason: {}",
                    diff, notice.isExemptedToFilled(), notice.getReasonToBeExempted());

            if (diff < 45 || !notice.getReasonToBeExempted().isEmpty()) {

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
                app.setAssNo(request.getAssNo());
                app.setBankNo(request.getBankNo());
                app.setBillNo(request.getBillNo());
                app.setStatus("UNPROCESSED");
                app.setRemarks(request.getStatement() == null || request.getStatement().isEmpty() ? "NO REMARKS" : request.getStatement());
                app.setDateOfFilling(new Date());
                app.setTaxedOff(request.getTaxedOffice());
                app.setOutcomeOfDecision("NO DECISION");
                app.setNoticeNumber(request.getInvoiceNo());
                app.setNatureOfAppeal(request.getNatureOfAppeal());
                app.setIsFilledTrat(false);
                app.setPhone(request.getPhone());
                app.setEmail(request.getEmail());
                app.setTinNumber(request.getTinNumber());
                app.setNatOfBus(request.getNatOf());
                app.setAction("1");

                UserDetails loggedUserInfo = loggedUser.getInfo();
                if (loggedUserInfo == null) {
                    res.setStatus(false);
                    res.setCode(ResponseCode.FAILURE);
                    res.setDescription("User session expired. Please login again.");
                    return res;
                }
                app.setCreatedBy(loggedUserInfo.getName());
                


                Appellant appellant = globalMethods.saveAppellant(request, notice);
                app.setAppellant(appellant);


                Witness witness;
                AppealAmount appealAmount = new AppealAmount();

                if (!witnessList.isEmpty()) {

                    for (Map < String, String > witnes: witnessList) {
                        witness = new Witness();
                        witness.setFullName(witnes.get("name"));
                        witness.setPhoneNumber(witnes.get("phone"));

                        Witness savedWit = witnessRepository.save(witness);
                        witnessSet.add(savedWit);
                    }

                    app.setWitnessId(witnessSet);
                }

                appealAmountSet = saveAmount(appealAmountSet, amountList);
                app.setAppealAmount(appealAmountSet);


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
                bill.setPayerEmail(request.getEmail() != null ? request.getEmail() : "");
                bill.setPayerPhone(request.getPhone() != null ? request.getPhone().replace("-", "") : "");
                bill.setExpiryDate(getBillExpireDate());
                bill.setApprovedBy(loggedUserInfo.getName());
                bill.setCurrency(currencyRepository.findByCurrencyShortName("TZS").getCurrencyShortName());
                bill.setSpSystemId(systemId);
                bill.setGeneratedDate(new java.sql.Date(new java.util.Date().getTime()));
                bill.setAppType("STATEMENT");
                bill.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());


                if (!witnessList.isEmpty()) {
                    bill.setBilledAmount(bill.getBilledAmount().add(witnessFee.get().getAmount()
                            .multiply(new BigDecimal(witnessSet.size()))));

                    bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(witnessFee.get().getAmount()
                            .multiply(new BigDecimal(witnessSet.size()))));
                }

                if (request.getStatement() != null && !request.getStatement().isEmpty()) {
                    bill.setBilledAmount(bill.getBilledAmount().add(evidenceFee.get().getAmount()
                            .multiply(new BigDecimal(Integer.valueOf(request.getStatement())))));


                    bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(evidenceFee.get().getAmount()
                            .multiply(new BigDecimal(Integer.valueOf(request.getStatement())))));
                }

                bill.setBilledAmount(bill.getBilledAmount().add(appealFee.get().getAmount()));
                bill.setBillEquivalentAmount(bill.getBillEquivalentAmount().add(appealFee.get().getAmount()));

                bill.setMiscellaneousAmount(new BigDecimal(0.00));
                bill.setRemarks("UNPAID");
                bill.setPayerName(notice.getAppelantName());


                Bill newBill = billRepository.save(bill);
                BillItems billItems;


                if (!witnessList.isEmpty()) {
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


                if (request.getStatement() != null && !request.getStatement().isEmpty()) {
                    for (int i = 0; i < Integer.parseInt(request.getStatement()); i++) {

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
                    app.setTax(taxTypeService.findById(request.getTypeOfTax()));
                    app.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName("NEW"));


                    ManualAppealsSequence manualAppealsSequence = manualAppealsSequenceRepository.findAll().get(0);
                    if(taxTypeService.findById(request.getTypeOfTax()).getTaxName().equals("VAT")) {
                        app.setAppealNo(request.getRegion().toUpperCase().toUpperCase() + "." +
                                manualAppealsSequence.getVatSequence()+ "/" + currentYear);
                        manualAppealsSequence.setVatSequence(manualAppealsSequence.getVatSequence()+1);
                        manualAppealsSequenceRepository.save(manualAppealsSequence);

                    }else if(taxTypeService.findById(request.getTypeOfTax()).getTaxName().equals("CUSTOM AND EXCISE")){
                        app.setAppealNo(request.getRegion().toUpperCase() + "." +
                                manualAppealsSequence.getCustomSequence()+ "/" + currentYear);
                        manualAppealsSequence.setCustomSequence(manualAppealsSequence.getCustomSequence()+1);
                        manualAppealsSequenceRepository.save(manualAppealsSequence);

                    }else if(taxTypeService.findById(request.getTypeOfTax()).getTaxName().equals("INCOME TAX")){
                        app.setAppealNo(request.getRegion().toUpperCase().toUpperCase() + "." +
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
                    log.error("Failed to send request to GEPG");
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
            log.error("Failed to process appeal registration", e);
            res.setStatus(false);
            res.setDescription("Problem occurred Please Contact Support! ");
            res.setCode(ResponseCode.FAILURE);
            return res;
        }
    }

    @Override
    public Set<AppealAmount> saveAmount(Set<AppealAmount> appealAmountSet, List<Map<String, String>> amountList) {
        AppealAmount appealAmount;
        if (!amountList.isEmpty()) {
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


    public Response uploadAppealManually(BacklogAppealDto request){

        log.debug("Uploading appeal manually: {}", request);
        Appeals appeals = new Appeals();
        Response<Void> response = new Response<>();
        List < Map < String, String >> amountList;
        ObjectMapper mapper = new ObjectMapper();
        Set<AppealAmount> appealAmountSet = new HashSet<>();

        try {
            Region region = regionRepository.findById(request.getRegion()).get();
            String appealNo = region.getCode() + "." + request.getAppealNo();
            TaxType taxType = taxTypeRepository.findById(request.getTax()).get();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


            if (!appealsRepository.findAppealsByAppealNoAndTax_Id(appealNo,
                    request.getTax()).isEmpty()) {

                response = new Response<>();
                response.setCode(ResponseCode.FAILURE);
                response.setDescription("Appeal already exist");
                response.setStatus(false);
                return response;
            }

            appeals.setAppealNo(appealNo);
            appeals.setAppellantName(request.getAppellantName());

            // Link to Appellant entity
            Appellant manualAppellant = appellantService.findOrCreateByTin(
                    request.getTin(),
                    request.getAppellantName(),
                    null,
                    request.getPhone(),
                    null
            );
            appeals.setAppellant(manualAppellant);

            appeals.setDateOfFilling(simpleDateFormat.parse(request.getDateFilling().split("T")[0]));

            if(request.getDecidedDate() != null && !request.getDecidedDate().isEmpty()){
            appeals.setDecidedDate(simpleDateFormat.parse(request.getDecidedDate().split("T")[0]));
            }
            appeals.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName(request.getStatusTrend()));

            appeals.setDecidedBy(request.getDecidedBy() != null ? request.getDecidedBy() : null);
            appeals.setTax(taxTypeService.findById(request.getTax()));
            appeals.setSummaryOfDecree(request.getSummary() != null ? request.getSummary() : "");
            appeals.setTinNumber(request.getTin());
            appeals.setPhone(request.getPhone());
            appeals.setNatureOfAppeal(request.getNature());
            appeals.setBillId(null);
            appeals.setCreatedBy("System Created");


            amountList = mapper.readValue(request.getAmountList(), List.class);
            appealAmountSet = saveAmount(appealAmountSet, amountList);
            appeals.setAppealAmount(appealAmountSet);
            appealsRepository.save(appeals);

            response.setDescription("Successful Uploaded");
            response.setCode(ResponseCode.SUCCESS);
        } catch (Exception e) {
            log.error("Failed to upload appeal manually", e);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Failed to load appeal");
        }
        return response;
    }

    @Override
    public Response registerForRetrial(RetrialDto request) {
        try {
            Appeals appeal = appealsRepository.findByAppealNoAndTaxType(request.getAppealNo(), request.getTaxType());

            if (appeal == null) {
                return new Response<>(false, ResponseCode.FAILURE, "Appeal not found", null);
            }

            DecisionHistory decisionHistory = createDecisionHistory(appeal, request.getReason());
            updateAppealForRetrial(appeal, decisionHistory);
            appealsRepository.save(appeal);

            return new Response<>(true, ResponseCode.SUCCESS, "Successfully registered for retrial", null);

        } catch (Exception e) {
            log.error("Failed to register for retrial", e);
            return new Response<>(false, ResponseCode.FAILURE, "Failed to register for retrial: " + e.getMessage(), null);
        }
    }

    private DecisionHistory createDecisionHistory(Appeals appeal, String reason) {
        DecisionHistory decisionHistory = new DecisionHistory();
        decisionHistory.setAppeals(appeal);
        decisionHistory.setAppealStatusTrend(appeal.getStatusTrend());
        decisionHistory.setJudgeName(appeal.getSummons().getJudge());
        decisionHistory.setSummaryOfDecree(appeal.getSummaryOfDecree());
        decisionHistory.setHearingDate(appeal.getSummons().getSummonStartDate() + " To " + appeal.getSummons().getSummonEndDate());
        decisionHistory.setCreatedDate(new Date());
        decisionHistory.setReason(reason);
        decisionHistory.setDecidedDate(appeal.getDecidedDate());
        UserDetails decisionUserInfo = loggedUser.getInfo();
        decisionHistory.setCreatedBy(decisionUserInfo != null ? decisionUserInfo.getName() : "SYSTEM");

        // Save decision history and return it
        return decisionHistoryRepository.save(decisionHistory);
    }

    private void updateAppealForRetrial(Appeals appeal, DecisionHistory decisionHistory) {
        // Update appeal properties for retrial
        appeal.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName("NEW"));
        appeal.setDecidedDate(null);
        appeal.setConcludingDate(null);
        appeal.setDateOfTheLastOrder(null);
        appeal.setSummons(null);
        appeal.setOutcomeOfDecision("NO DECISION");
        appeal.setProcedingStatus(null);
        appeal.setWonBy("");
        appeal.setDecidedBy("");

        // Add decision history to appeal
        appeal.getDecisionHistories().add(decisionHistory);
    }


}