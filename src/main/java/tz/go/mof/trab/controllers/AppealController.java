package tz.go.mof.trab.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.service.*;
import tz.go.mof.trab.dto.appeal.*;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping("/appeal")
public class AppealController {

    private static final Logger log = LoggerFactory.getLogger(AppealController.class);

    @Value("${tz.go.trab.upload.dir}")
    private String uploadingDir;

    GlobalMethods globalMethods;

    private final NoticeRepository noticeRepository;
    private final AppealsRepository appealsRepository;
    private final AppealStatusTrendRepository appealStatusTrendRepository;
    private final BillItemRepository billItemRepository;
    private final LoggedUser loggedUser;
    private final CurrencyService currencyService;
    private final AppealsAmountRepository appealAmountRepository;
    private final AppealsService appealService;
    private final PaymentRepository paymentRepository;
    private final AppealServedByRepository appealServedByRepository;

    private RegionRepository regionRepository;


    private TaxTypeRepository taxTypeRepository;


    private DeletedAppealRepository deletedAppealRepository;

    @Autowired
    void setDeletedAppealRepository(DeletedAppealRepository deletedAppealRepository) {
        this.deletedAppealRepository = deletedAppealRepository;
    }


    @Autowired
    private JudgeService judgeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SummonsRepository summonsRepository;

    @Autowired
    private SummonsAppealsRepository summonsAppealsRepository;


    @Autowired
    void setRegionRepository(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Autowired
    void setTaxTypeRepository(TaxTypeRepository taxTypeRepository) {
        this.taxTypeRepository = taxTypeRepository;
    }

    AppealController(AppealsService appealService, AppealsAmountRepository appealsAmountRepository,
                     CurrencyService currencyService, LoggedUser loggedUser, BillItemRepository billItemRepository,
                     AppealStatusTrendRepository appealStatusTrendRepository, AppealsRepository appealsRepository,
                     NoticeRepository noticeRepository, GlobalMethods globalMethods, PaymentRepository paymentRepository,
                     AppealServedByRepository appealServedByRepository) {
        this.appealService = appealService;
        this.appealAmountRepository = appealsAmountRepository;
        this.currencyService = currencyService;
        this.loggedUser = loggedUser;
        this.billItemRepository = billItemRepository;
        this.appealStatusTrendRepository = appealStatusTrendRepository;
        this.appealsRepository = appealsRepository;
        this.noticeRepository = noticeRepository;
        this.globalMethods = globalMethods;
        this.paymentRepository = paymentRepository;
        this.appealServedByRepository = appealServedByRepository;
    }

    @PostMapping(path = "/internalCreate")
    @ResponseBody
    public Response<Appeals> createAppeal(@RequestBody CreateAppealDto req) {
        return appealService.createAppeal(req);
    }


    @PostMapping("/appealEdit")
    @ResponseBody
    public Response<Appeals> editAppealAlone(@RequestBody EditAppealDto req) {

        log.debug("Editing appeal: {}", req);

        Response<Appeals> res = new Response<>();
        try {
            ObjectMapper mapper = new ObjectMapper();

            Set<AppealAmount> appealAmountSet = new HashSet<>();
            List<Map<String, String>> amountList;

            amountList = mapper.readValue(req.getAmountList(), List.class);
            Appeals app = appealsRepository.findById(Long.valueOf(req.getAppealId())).get();

            Notice notice = noticeRepository.findBynoticeNo(app.getNoticeNumber());


            Appellant appellant = globalMethods.saveAppellant(req, notice);
            app.setAppellant(appellant);

            String dates[] = req.getDate().split("T");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfFilling = formatter.parse(dates[0]);


            app.setAssNo(req.getAssNo());
            app.setBankNo(req.getBankNo());
            app.setNatOfBus(req.getNatOf());
            app.setBillNo(req.getBillNo());
            app.setTaxedOff(req.getTaxedOffice());
            app.setNatureOfAppeal(req.getNatureOfAppeal());
            app.setUpdatedAt(LocalDateTime.now());
            app.setUpdatedBy(loggedUser.getInfo().getName());
            app.setDateOfFilling(dateOfFilling);

            app.setAppealNo(req.getAppealNo());
            app.setAppellantName(req.getAppealantName());
            app.setStatusTrend(appealStatusTrendRepository.findById(req.getStatus()).get());


            appealAmountRepository.deleteAppealAmounts(app.getAppealId());

            appealAmountSet = saveAmount(appealAmountSet, amountList);
            app.setAppealAmount(appealAmountSet);


            app.setAppealNo(req.getRegion() + "." + app.getAppealNo().split("\\.")[1]);
            res.setStatus(true);
            res.setDescription("Success");
            res.setCode(ResponseCode.SUCCESS);

            appealsRepository.save(app);
        } catch (Exception e) {
            log.error("Error editing appeal", e);
            res.setStatus(false);
            res.setDescription("Problem occurred Please Contact Support! ");
            res.setCode(ResponseCode.FAILURE);
        }
        return res;
    }


    private Set<AppealAmount> saveAmount(Set<AppealAmount> appealAmountSet, List<Map<String, String>> amountList) {
        AppealAmount appealAmount;
        if (!amountList.isEmpty()) {
            for (Map<String, String> amount : amountList) {
                appealAmount = new AppealAmount();
                appealAmount.setAmountOnDispute(new BigDecimal(amount.get("amount")));
                Currency currency = currencyService.findByCurrencyShortName(amount.get("currency"));
                appealAmount.setCurrency(currency);
                appealAmount.setCurrencyName(currency.getCurrencyShortName());

                AppealAmount appealAmount1 = appealAmountRepository.save(appealAmount);
                appealAmountSet.add(appealAmount1);
            }
        }
        return appealAmountSet;
    }


    @PostMapping("/internalEdit")
    @ResponseBody
    public Response<Appeals> editAppealStatement(@RequestBody EditAppealStatementDto req) {

        Response<Appeals> res = new Response<>();
        try {

            log.debug("Editing appeal statement: {}", req);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Appeals app = appealsRepository.findByAppealNoAndTaxType(req.getAppealId(), req.getTaxId());
            app.setDecidedDate(formatter.parse(req.getDesicionDate().split("T")[0]));
            app.setSummaryOfDecree(req.getRemarks());
            app.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName(req.getStatus()));
            app.setProcedingStatus(req.getStatus());
            app.setWonBy(req.getWonBy());
            app.setCopyOfJudgement(req.getFileName());


            if (req.getJudge() != null && !req.getJudge().isEmpty()) {
                app.setDecidedBy(req.getJudge());
            } else if (app.getSummons() != null && app.getSummons().getJud() != null) {
                app.setDecidedBy(app.getSummons().getJud().getName());
            }


            String filePath = "";
            String binaryData = "";
            if (req.getFile() != null) {
                binaryData = req.getFile();
                filePath = "Appeal_" + app.getAppealId() + ".pdf";
                app.setCopyOfJudgement(filePath);

                if (!new File(uploadingDir).exists()) {
                    new File(uploadingDir).mkdirs();
                }

                File fileToCreate = new File(uploadingDir, filePath);
                byte[] decodedBytes = Base64.getDecoder().decode(binaryData);
                FileUtils.writeByteArrayToFile(fileToCreate, decodedBytes);
            }


            appealsRepository.save(app);

            // Process aggregated/consolidated appeals - aggregatedAppeals is a JSON array of appeal database IDs e.g. "[40413, 40414]"
            if (req.getAggregatedAppeals() != null && !req.getAggregatedAppeals().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<Long> aggregatedIds = mapper.readValue(req.getAggregatedAppeals(), new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {});

                for (Long aggId : aggregatedIds) {
                    // Skip the main appeal already processed above
                    if (app.getAppealId().equals(aggId)) {
                        continue;
                    }

                    Appeals aggAppeal = appealsRepository.findByAppealId(aggId);
                    if (aggAppeal == null) {
                        log.warn("Aggregated appeal not found for id: {}", aggId);
                        continue;
                    }

                    // Apply same decision details to all consolidated appeals
                    aggAppeal.setDecidedDate(formatter.parse(req.getDesicionDate().split("T")[0]));
                    aggAppeal.setSummaryOfDecree(req.getRemarks());
                    aggAppeal.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName(req.getStatus()));
                    aggAppeal.setProcedingStatus(req.getStatus());
                    aggAppeal.setWonBy(req.getWonBy());

                    // Set judge
                    if (req.getJudge() != null && !req.getJudge().isEmpty()) {
                        aggAppeal.setDecidedBy(req.getJudge());
                    } else if (aggAppeal.getSummons() != null && aggAppeal.getSummons().getJud() != null) {
                        aggAppeal.setDecidedBy(aggAppeal.getSummons().getJud().getName());
                    }

                    // Assign copy of judgement file - each consolidated appeal gets its own file copy
                    if (req.getFile() != null) {
                        String aggFilePath = "Appeal_" + aggAppeal.getAppealId() + ".pdf";
                        aggAppeal.setCopyOfJudgement(aggFilePath);

                        File aggFileToCreate = new File(uploadingDir, aggFilePath);
                        byte[] aggDecodedBytes = Base64.getDecoder().decode(req.getFile());
                        FileUtils.writeByteArrayToFile(aggFileToCreate, aggDecodedBytes);
                    }

                    appealsRepository.save(aggAppeal);
                    log.debug("Aggregated appeal {} decision updated", aggAppeal.getAppealNo());
                }
            }

            res.setCode(ResponseCode.SUCCESS);
            res.setDescription("Appeal Decision Updated Successful");


        } catch (Exception e) {
            log.error("Error updating appeal decision", e);
            res.setCode(ResponseCode.FAILURE);
            res.setStatus(false);
            res.setDescription("Error occurred while editing Appeal");
        }
        return res;
    }

    @PostMapping("/internalSearch")
    @ResponseBody
    public List<Appeals> searchAppeal(@RequestBody AppealSearchDto req) {
        return appealsRepository.getAppeals(Long.valueOf(req.getStatusId()), req.getToken());

    }

    @GetMapping(value = "/getCategory", produces = "application/json")
    @ResponseBody
    public List<CategoryStatsDto> getCategoryStats() {

        List<CategoryStatsDto> result = new ArrayList<>();

        String[] color = new String[10];
        color[0] = "#3498db";
        color[1] = "#9b59b6";
        color[2] = "#2ecc71";
        color[3] = "#f1c40f";

        List<Object> cat = appealsRepository.getCategory();
        int i = 0;
        BigInteger sum = new BigInteger("0");

        for (Object ob : cat) {
            Object[] fields = (Object[]) ob;
            BigInteger value = (BigInteger) fields[1];
            sum = sum.add(value);
        }

        for (Object ob : cat) {
            CategoryStatsDto dto = new CategoryStatsDto();
            Object[] fields = (Object[]) ob;
            String name = (String) fields[0];
            BigInteger value = (BigInteger) fields[1];
            dto.setName(name);
            dto.setY(Double.toString(value.doubleValue() / sum.doubleValue()));
            dto.setColor(color[i]);

            i++;

            result.add(dto);
        }

        return result;

    }


    @GetMapping(value = "/gettopappelant", produces = "application/json")
    @ResponseBody
    public List<TopAppellantDto> getTopAppealing() {

        List<TopAppellantDto> result = new ArrayList<>();

        List<Object> top = appealsRepository.getListAppeleant();

        for (Object ob : top) {
            Object[] fields = (Object[]) ob;
            TopAppellantDto dto = new TopAppellantDto();

            String appeleantName = (String) fields[0];
            BigDecimal amountTZS = (BigDecimal) fields[1];
            BigDecimal amountUSD = (BigDecimal) fields[2];
            BigDecimal allowedTZS = (BigDecimal) fields[3];
            BigDecimal allowedUSD = (BigDecimal) fields[4];
            String outComeDesicion = (String) fields[5];

            dto.setName(appeleantName.toUpperCase());
            dto.setAmtTZS(amountTZS.toString());
            dto.setAmtUSD(amountUSD.toString());
            dto.setAllTZS(allowedTZS.toString());
            dto.setAllUSD(allowedUSD.toString());
            dto.setDesicion(outComeDesicion);

            result.add(dto);
        }

        return result;
    }


    @GetMapping(value = "/gettaxinfo", produces = "application/json")
    @ResponseBody
    public List<TaxTypeInfoDto> getTypeOfTaxInfo() {

        try {
            List<TaxTypeInfoDto> result = new ArrayList<>();

            List<Object> top = appealsRepository.getTaxTypeInfo();

            for (Object oj : top) {
                Object[] fields = (Object[]) oj;
                TaxTypeInfoDto dto = new TaxTypeInfoDto();

                String taxTypeName = (String) fields[0];
                BigDecimal amountTZS = (BigDecimal) fields[1];
                BigDecimal amountUSD = (BigDecimal) fields[2];
                BigDecimal allowedTZS = (BigDecimal) fields[3];
                BigDecimal allowedUSD = (BigDecimal) fields[4];

                dto.setName(taxTypeName);
                dto.setAmtTZS(amountTZS.toString());
                dto.setAmtUSD(amountUSD.toString());
                dto.setAllTZS(allowedTZS.toString());
                dto.setAllUSD(allowedUSD.toString());

                result.add(dto);
            }

            return result;
        } catch (Exception e) {
            log.error("Error getting tax type info", e);
            return null;
        }

    }

    @PostMapping("/hearingEdit")
    @ResponseBody
    public Response updateHearingInfo(@RequestBody HearingEditDto req) {

        Response<?> res = new Response<>();
        try {
            log.debug("Updating hearing info: {}", req);

            Appeals app = appealsRepository.findById(Long.valueOf(req.getAppealId())).get();


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            if ("1".equals(req.getType())) {
                app.setProcedingStatus("CONCLUDED");
                String concludingDate = req.getConcludingDate();
                Date concludeDate = formatter.parse(concludingDate);
                String dateOfDecision = req.getDateDesicion();
                Date noticeDate = formatter.parse(dateOfDecision);
                app.setConcludingDate(concludeDate);
                app.setDateOfTheLastOrder(noticeDate);

                appealsRepository.save(app);
            } else {

                Summons summon = app.getSummons();
                String start;
                String end;
                if (req.getStart() != null && !req.getStart().isEmpty()) {
                    start = req.getStart().split("T")[0];
                    Date newStart = formatter.parse(start);
                    summon.setSummonStartDate(newStart);
                }

                if (req.getEnd() != null && !req.getEnd().isEmpty()) {
                    end = req.getEnd().split("T")[0];
                    Date newEnd = formatter.parse(end);
                    summon.setSummonEndDate(newEnd);
                }

                if (req.getTime() != null && !req.getTime().isEmpty()) {
                    summon.setTime(req.getTime() + " HRS");
                }
                app.setSummons(summon);
                appealsRepository.save(app);
            }

            res.setStatus(true);
            res.setCode(ResponseCode.SUCCESS);


        } catch (Exception e) {
            log.error("Error updating hearing info", e);
            res.setCode(ResponseCode.FAILURE);
            res.setStatus(false);
            res.setDescription("Error Occurred During Update");
        }
        return res;
    }


    @PostMapping(value = "/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Response uploadFile(@RequestParam MultipartFile file, @RequestParam String appeal, @RequestParam String remarks) throws IllegalStateException, IOException {

        Response<?> res = new Response<>();
        Appeals appeals = appealsRepository.findByappealNo(appeal);
        log.debug("Processing file upload for appeal: {}", appeals.getAppealNo());
        Set<Evidence> evidenceSet = appeals.getAnnextors();

        Evidence evi = new Evidence();
        evi.setFileName(file.getOriginalFilename());
        evi.setEvidenceType("ANNEXTORS");
        evi.setEvidenceRemarks(remarks);

        try {
            evidenceSet.add(evi);
            appeals.setAnnextors(evidenceSet);
            appealsRepository.save(appeals);
        } catch (Exception e) {
            log.error("Error saving evidence for appeal", e);
        }

        log.info("File '{}' uploaded successfully", file.getOriginalFilename());
        File newFile = new File(uploadingDir + file.getOriginalFilename());
        file.transferTo(newFile);
        res.setStatus(true);
        return res;
    }

    @GetMapping(value = "/billItems/{billId}", produces = "application/json")
    @ResponseBody
    public List<BillItems> getBillItemFromBill(@PathVariable String billId) throws IllegalStateException {
        return billItemRepository.getBillItemOfTheSameBill(billId);

    }

    @GetMapping(value = "/get-appeal-from-summon/{summonId}", produces = "application/json")
    @ResponseBody
    public Appeals getAppealFromSummon(@PathVariable Long summonId) throws IllegalStateException {
        Appeals app = appealsRepository.findBySummonId(summonId);

        return app;

    }

    @PostMapping(path = "/search-payments", produces = "application/json")
    @ResponseBody
    public List<Payment> getPayments(@RequestBody PaymentSearchDto req) throws IllegalStateException {

        return globalMethods.findPaymentByCreature(req.getControlNumber(), req.getPspRef(), req.getBank(), req.getStartDate(), req.getEndDate());

    }

    @GetMapping(path = "/all-payments", produces = "application/json")
    @ResponseBody
    public List<Payment> getPayments() throws IllegalStateException {


        return (List<Payment>) paymentRepository.findAll();

    }

    @PostMapping(path = "/find-by-appeal-no", produces = "application/json")
    @ResponseBody
    public Appeals findByAppealNo(@RequestBody AppealNoDto req) throws IllegalStateException {
        return appealsRepository.findByAppealNo(req.getNo());
    }

    @PostMapping(path = "/update-filled-trat", produces = "application/json")
    @ResponseBody
    public void updateFilledTrat(@RequestBody AppealNoDto req) throws IllegalStateException {

        log.debug("Updating filled TRAT for appeal: {}", req.getAppealNumber());
        Appeals appeals = appealsRepository.findByappealNo(req.getAppealNumber());
        appeals.setIsFilledTrat(true);
        appealsRepository.save(appeals);
    }

    @PostMapping(path = "/update-served-by", produces = "application/json")
    @ResponseBody
    public Response<AppealServedBy> updateServedBy(@RequestBody UpdateServedByDto req) throws IllegalStateException {


        log.debug("Updating served by info: {}", req);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Response<AppealServedBy> appealsResponse = new Response<>();
        try {

            Appeals appeals = appealsRepository.findAppealsByAppealNoAndTax_Id(req.getAppNo(), req.getTax()).get(0);
            AppealServedBy appealServedBy = new AppealServedBy();
            appealServedBy.setAppName(req.getAppName());
            appealServedBy.setAppPhone(req.getAppPhone());
            appealServedBy.setAppDate(formatter.parse(req.getAppDate().split("T")[0]));

            appealServedBy.setResoName(req.getResoName());
            appealServedBy.setResoPhone(req.getResoPhone());
            appealServedBy.setResoDate(formatter.parse(req.getResoDate().split("T")[0]));


            String filePath = "";
            String binaryData = "";
            if (req.getFile1() != null) {
                appealServedBy.setAppellantFile(req.getFileName1());
                //check if LocationSketch available then upload
                binaryData = req.getFile1();
                filePath = req.getFileName1();


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

            }


            String filePath2 = "";
            String binaryData2 = "";
            if (req.getFile2() != null) {
                appealServedBy.setRespondentFile(req.getFileName2());
                //check if LocationSketch available then upload
                binaryData2 = req.getFile2();
                filePath2 = req.getFileName2();


                if (!new File(uploadingDir).exists()) {
                    boolean success = new File(uploadingDir).mkdirs();
                    if (!success) {
                    }
                }

                File fileToCreate = new File(uploadingDir, filePath2);

                if (!fileToCreate.exists()) {
                    byte[] decodedBytes = Base64.getDecoder().decode(binaryData2);
                    FileUtils.writeByteArrayToFile(fileToCreate, decodedBytes);
                }

            }


            AppealServedBy newAppealServedBy = appealServedByRepository.save(appealServedBy);

            appeals.setAppealServedBy(newAppealServedBy);
            appealsRepository.save(appeals);

            appealsResponse.setData(newAppealServedBy);
            appealsResponse.setCode(ResponseCode.SUCCESS);
            appealsResponse.setStatus(true);

        } catch (Exception e) {
            log.error("Error updating served by info", e);
            appealsResponse.setCode(ResponseCode.FAILURE);
            appealsResponse.setStatus(false);
            appealsResponse.setData(null);
            appealsResponse.setDescription("Failure");
        }
        return appealsResponse;
    }

    @PostMapping(path = "/appeals-date", produces = "application/json")
    @ResponseBody
    public Page<Appeals> getAppealsByDateRange(@RequestBody AppealDateRangeDto req) throws IllegalStateException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Pageable pageable = PageRequest.of(0, 10000, Sort.by("appealId").descending());
        Date start = null;
        Date end = null;
        try {
            start = formatter.parse(req.getDateFrom().split("T")[0]);
            end = formatter.parse(req.getDateTo().split("T")[0]);
        } catch (Exception e) {
            log.error("Error parsing date range", e);
        }

        if (req.getTax() == null || req.getTax().isEmpty())
            return appealsRepository.findAppealsByDateOfFillingBetweenOrderByDateOfFillingAsc(start, end, pageable);
        else {
            return appealsRepository.findAppealsByDateOfFillingBetweenAndTax_IdOrderByDateOfFillingAsc(start, end, req.getTax(), pageable);
        }

    }


    @PostMapping(path = "load-backlog", produces = "application/json")
    @ResponseBody
    public Response uploadLogBack(@RequestBody BacklogAppealDto requestBody) {
        log.debug("Loading backlog appeal: {}", requestBody);
        return appealService.uploadAppealManually(requestBody);
    }

    @PostMapping(path = "load-backlog-appeal", produces = "application/json")
    @ResponseBody
    public Response uploadLogBackAppeal(@RequestBody BacklogCheckDto requestBody) {


        log.debug("Checking backlog appeal: {}", requestBody);


        Region region = regionRepository.findById(requestBody.getRegion()).get();
        String appealNo = region.getCode() + "." + requestBody.getAppealNo();

        log.debug("Checking backlog appeal - Appeal No: {}", appealNo);
        TaxType taxType = taxTypeRepository.findById(requestBody.getTax()).get();
        log.debug("Tax type: {}", taxType.getTaxName());

        Response<?> response = new Response<>();

        if (!appealsRepository.findAppealsByAppealNoAndTax_Id(appealNo, requestBody.getTax()).isEmpty()) {
            response = new Response<>();
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Appeal already exist");
            response.setStatus(false);
            return response;
        }
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("Appeal added successfully");
        response.setStatus(true);
        return response;
    }

    @PostMapping(path = "load-backlog-pending", produces = "application/json")
    @ResponseBody
    public Response uploadLogBackPending(@RequestBody BacklogPendingDto req) {

        Response<Summons> res = new Response<>();

        try {
            Summons summons = new Summons();
            Date startDate;
            Date endDate;
            Date lastOrderDate;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            String startDates[] = req.getStartDate().split("T");
            String endDates[] = req.getEndDate().split("T");
            String lastOrderDates[] = req.getLastOrderDate().split("T");


            startDate = formatter.parse(startDates[0]);
            endDate = formatter.parse(endDates[0]);
            lastOrderDate = formatter.parse(lastOrderDates[0]);


            if (endDate.before(startDate)) {
                res.setDescription("Dates mismatch!!!");
                res.setStatus(false);
                res.setData(null);
                res.setCode(ResponseCode.FAILURE);
                return res;
            }

            List<Map<String, Integer>> mapList;
            ObjectMapper mapper = new ObjectMapper();
            AtomicReference<String> appList = new AtomicReference<>("");

            SummonsAppeal summonsAppeals = new SummonsAppeal();

            Judge judge = judgeService.findById(req.getJudge());

            summons.setJudge(judge.getName());
            summons.setJud(judge);


            mapList = mapper.readValue(req.getAppList(), List.class);

            if (mapList.isEmpty()) {
                res.setDescription("Please select Appeals or Applications for Summons creation");
                res.setStatus(false);
                res.setData(null);
                res.setCode(ResponseCode.FAILURE);
                return res;
            }

            mapList.forEach(x -> {
                appList.set(appList + " , " + x.get("id"));
            });

            SystemUser user = userRepository.findById(loggedUser.getInfo().getId()).get();

            summons.setAppList(appList.get());
            summons.setVenue("");
            summons.setSummonStartDate(startDate);
            summons.setSummonEndDate(endDate);
            summons.setSystemUser(user);
            summons.setCreatedDate(new Date());
            summons.setMemberOne(req.getMemberOne());
            summons.setMemberTwo(req.getMemberTwo());

            Summons newSummons = summonsRepository.save(summons);


            mapList.forEach(x -> {

                Appeals appeal = appealsRepository.findById(Long.valueOf(x.get("id"))).get();
                appeal.setDateOfTheLastOrder(lastOrderDate);
                appealsRepository.save(appeal);

                summonsAppeals.setAppealId(appeal.getAppealId().toString());
                summonsAppeals.setSummonId(newSummons.getSummonId().toString());
                // summonRepository.save(summonsAppeals);
                appeal.setSummons(newSummons);
                appealsRepository.save(appeal);


                newSummons.setTaxType(appeal.getTax().getTaxName());
                newSummons.setSummonNo(newSummons.getSummonId().toString());
            });


            summonsRepository.save(newSummons);
            res.setCode(ResponseCode.SUCCESS);
            res.setDescription("Summons created successfully");
            res.setStatus(true);
            res.setData(newSummons);
            return res;
        } catch (Exception e) {
            log.error("Error creating summons", e);
            res.setCode(ResponseCode.FAILURE);
            res.setDescription("Error Occurred");
            res.setStatus(false);
            return res;
        }
    }


    @GetMapping(value = "/mark-delete-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response markForDelete(@PathVariable("id") Long id) {
        Response<?> response = new Response<>();

        try {
            Appeals appeal = appealsRepository.findById(id).get();

            if (appeal == null) {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Appeal Doesnt exist");
                return response;
            }

            appeal.setInitiatedForDelete(true);
            appeal.setDeletedInitiatedBy(loggedUser.getInfo().getUsername());
            appealsRepository.save(appeal);

            response.setCode(ResponseCode.SUCCESS);
            response.setStatus(true);
            response.setDescription("Appeal No " + appeal.getAppealNo() + " " +
                    appeal.getTax().getTaxName() + " " + appeal.getAppellantName() + "  " +
                    "Create a delete request !!!!");

            return response;

        } catch (Exception e) {
            log.error("Error marking appeal for delete", e);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("System issue, cannot perform this now");
            return response;
        }
    }


    @GetMapping(value = "/unmark-delete-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response removeFromMarkDelete(@PathVariable("id") Long id) {
        Response<?> response = new Response<>();

        try {
            Appeals appeal = appealsRepository.findById(id).get();

            if (appeal == null) {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Appeal Doesnt exist");
                return response;
            }

            appeal.setInitiatedForDelete(false);
            appeal.setDeletedInitiatedBy("");
            appealsRepository.save(appeal);

            response.setCode(ResponseCode.SUCCESS);
            response.setStatus(true);
            response.setDescription("Appeal No " + appeal.getAppealNo() + " " +
                    appeal.getTax().getTaxName() + " " + appeal.getAppellantName() + "  " +
                    "Remove delete request !!!!");

            return response;

        } catch (Exception e) {
            log.error("Error unmarking appeal for delete", e);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("System issue, cannot perform this now");
            return response;
        }
    }

    @GetMapping(value = "/delete-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response deleteAppeal(@PathVariable("id") Long id) {

        Response<?> response = new Response<>();

        try {
            Appeals appeal = appealsRepository.findById(id).get();

            if (appeal == null) {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Appeal Doesnt exist");
                return response;
            }

            List<AppealAmount> appealAmounts = appealAmountRepository.findAppealAmountByAppealId(id);

            for (AppealAmount appealAmount : appealAmounts) {
                appealAmountRepository.delete(appealAmount);
            }

            appealsRepository.delete(appeal);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("Appeal No " + appeal.getAppealNo() + " " +
                    appeal.getTax().getTaxName() + " " + appeal.getAppellantName() + "  " +
                    "Deleted and backup successful");

            DeletedAppeals deletedAppeals = new DeletedAppeals();
            TrabHelper.copyNonNullProperties(appeal, deletedAppeals);
            deletedAppealRepository.save(deletedAppeals);

            return response;
        } catch (Exception e) {
            log.error("Error deleting appeal", e);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("System issue, cannot perform this now");
            return response;
        }
    }


    @PostMapping(path = "/register-for-retrial", produces = "application/json")
    @ResponseBody
    public Response registerForRetrial(@RequestBody RetrialDto req) {
        return appealService.registerForRetrial(req);
    }

    @GetMapping(value = "/get-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response<Appeals> getAppealById(@PathVariable("id") Long id) {
        Response<Appeals> response = new Response<>();
        try {
            Appeals appeal = appealsRepository.findByAppealId(id);
            if (appeal != null) {
                response.setData(appeal);
                response.setCode(ResponseCode.SUCCESS);
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setStatus(false);
                response.setDescription("Appeal not found");
            }
        } catch (Exception e) {
            log.error("Error fetching appeal by id: {}", id, e);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("Error occurred while fetching appeal");
        }
        return response;
    }

}

