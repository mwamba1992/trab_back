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
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping("/appeal")
public class AppealController {

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
    public Response<Appeals> createAppeal(@RequestBody Map<String, String> req) {
        return appealService.createAppeal(req);
    }


    @PostMapping("/appealEdit")
    @ResponseBody
    public Response<Appeals> editAppealAlone(@RequestBody Map<String, String> req) {

        TrabHelper.print(req);

        Response<Appeals> res = new Response<>();
        try {
            ObjectMapper mapper = new ObjectMapper();

            Set AppealAmountSet = new HashSet();
            List<Map<String, String>> amountList;

            amountList = mapper.readValue(req.get("amountList"), List.class);
            Appeals app = appealsRepository.findById(Long.valueOf(req.get("appealId"))).get();

            Notice notice = noticeRepository.findBynoticeNo(app.getNoticeNumber());


            Appellant appellant = globalMethods.saveAppellant(req, notice);
            app.setAppellant(appellant);

            String dates[] = req.get("date").split("T");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfFilling = formatter.parse(dates[0]);


            app.setAssNo(req.get("assNo"));
            app.setBankNo(req.get("bankNo"));
            app.setNatOfBus(req.get("natOf"));
            app.setBillNo(req.get("billNo"));
            app.setTaxedOff(req.get("taxedOffice"));
            app.setNatureOfAppeal(req.get("natureOfAppeal"));
            app.setUpdatedAt(LocalDateTime.now());
            app.setUpdatedBy(loggedUser.getInfo().getName());
            app.setDateOfFilling(dateOfFilling);

            app.setAppealNo(req.get("appealNo"));
            app.setAppellantName(req.get("appealantName"));
            app.setStatusTrend(appealStatusTrendRepository.findById(req.get("status")).get());


            appealAmountRepository.deleteAppealAmounts(app.getAppealId());

            AppealAmountSet = saveAmount(AppealAmountSet, amountList);
            app.setAppealAmount(AppealAmountSet);


            app.setAppealNo(req.get("region") + "." + app.getAppealNo().split("\\.")[1]);
            res.setStatus(true);
            res.setDescription("Success");
            res.setCode(ResponseCode.SUCCESS);

            appealsRepository.save(app);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(false);
            res.setDescription("Problem occurred Please Contact Support! ");
            res.setCode(ResponseCode.FAILURE);
        }
        return res;
    }


    private Set saveAmount(Set appealAmountSet, List<Map<String, String>> amountList) {
        AppealAmount appealAmount;
        if (amountList.size() > 0) {
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
    public Response<Appeals> editAppealStatement(@RequestBody Map<String, String> req) {

        Response<Appeals> res = new Response<>();
        try {

            TrabHelper.print(req);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Appeals app = appealsRepository.findByAppealNoAndTaxType(req.get("appealId"), req.get("taxId"));
            app.setDecidedDate(formatter.parse(req.get("desicionDate").split("T")[0]));
            app.setSummaryOfDecree(req.get("remarks"));
            app.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendName(req.get("status")));
            app.setProcedingStatus(req.get("status"));
            app.setWonBy(req.get("wonBy"));
            app.setCopyOfJudgement(req.get("fileName"));


            if (req.get("judge").isEmpty() || req.get("judge") == null) {
                app.setDecidedBy(app.getSummons().getJud().getName());
            } else {
                app.setDecidedBy(req.get("judge"));
            }


            String filePath = "";
            String binaryData = "";
            if (req.get("file") != null) {
                //check if LocationSketch available then upload
                binaryData = req.get("file");
                filePath = req.get("fileName");


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


            appealsRepository.save(app);
            res.setCode(ResponseCode.SUCCESS);
            res.setDescription("Appeal Decision Updated Successful");


        } catch (Exception e) {
            res.setCode(ResponseCode.FAILURE);
            e.printStackTrace();
            res.setStatus(false);
            res.setDescription("Error occurred while editing Appeal");

        }
        return res;
    }

    @PostMapping("/internalSearch")
    @ResponseBody
    public List<Appeals> searchAppeal(@RequestBody Map<String, String> req) {
        return appealsRepository.getAppeals(Long.valueOf(req.get("statusId")), req.get("token"));

    }

    @GetMapping(value = "/getCategory", produces = "application/json")
    @ResponseBody
    public List<Map<String, String>> getCategoryStats() {

        List<Map<String, String>> result = new ArrayList<>();
        ;
        Map<String, String> res;

        String[] color = new String[10]; // declare with size
        color[0] = "#3498db";
        color[1] = "#9b59b6";
        color[2] = "#2ecc71";
        color[3] = "#f1c40f";

        List<Object> cat = appealsRepository.getCategory();
        int i = 0;
        BigInteger sum = new BigInteger("0"); //

        for (Object ob : cat) {
            Object[] fields = (Object[]) ob;
            BigInteger value = (BigInteger) fields[1];
            sum = sum.add(value);
        }

        for (Object ob : cat) {

            res = new HashMap<>();
            Object[] fields = (Object[]) ob;
            String name = (String) fields[0];
            BigInteger value = (BigInteger) fields[1];
            res.put("name", name);
            res.put("y", Double.toString(value.doubleValue() / sum.doubleValue()));
            res.put("color", color[i]);

            i++;

            result.add(res);
        }

        return result;

    }


    @GetMapping(value = "/gettopappelant", produces = "application/json")
    @ResponseBody
    public List<Map<String, String>> getTopAppealing() {

        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> res;

        List<Object> top = appealsRepository.getListAppeleant();

        for (Object ob : top) {

            Object[] fields = (Object[]) ob;

            res = new HashMap<>();


            String appeleantName = (String) fields[0];
            BigDecimal amountTZS = (BigDecimal) fields[1];
            BigDecimal amountUSD = (BigDecimal) fields[2];
            BigDecimal allowedTZS = (BigDecimal) fields[3];
            BigDecimal allowedUSD = (BigDecimal) fields[4];
            String outComeDesicion = (String) fields[5];


            res.put("name", appeleantName.toUpperCase());
            res.put("amtTZS", amountTZS.toString());
            res.put("amtUSD", amountUSD.toString());
            res.put("allTZS", allowedTZS.toString());
            res.put("allUSD", allowedUSD.toString());
            res.put("desicion", outComeDesicion);


            result.add(res);
        }


        return result;
    }


    @GetMapping(value = "/gettaxinfo", produces = "application/json")
    @ResponseBody
    public List<Map<String, String>> getTypeOfTaxInfo() {


        try {
            List<Map<String, String>> result = new ArrayList<>();

            Map<String, String> res;

            List<Object> top = appealsRepository.getTaxTypeInfo();

            for (Object oj : top) {
                Object[] fields = (Object[]) oj;


                res = new HashMap<>();
                String taxTypeName = (String) fields[0];
                BigDecimal amountTZS = (BigDecimal) fields[1];
                BigDecimal amountUSD = (BigDecimal) fields[2];
                BigDecimal allowedTZS = (BigDecimal) fields[3];
                BigDecimal allowedUSD = (BigDecimal) fields[4];

                res.put("name", taxTypeName);
                res.put("amtTZS", amountTZS.toString());
                res.put("amtUSD", amountUSD.toString());
                res.put("allTZS", allowedTZS.toString());
                res.put("allUSD", allowedUSD.toString());


                result.add(res);

            }


            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping("/hearingEdit")
    @ResponseBody
    public Response updateHearingInfo(@RequestBody Map<String, String> req) {

        Response res = new Response();
        try {

            System.out.println(req);

            Appeals app = appealsRepository.findById(Long.valueOf(req.get("appealId"))).get();


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            if (req.get("type").equals("1")) {
                app.setProcedingStatus("CONCLUDED");
                String concludingDate = req.get("concludingDate");
                Date concludeDate = formatter.parse(concludingDate);
                String dateOfDecision = req.get("dateDesicion");
                Date noticeDate = formatter.parse(dateOfDecision);
                app.setConcludingDate(concludeDate);
                app.setDateOfTheLastOrder(noticeDate);

                appealsRepository.save(app);
            } else {

                Summons summon = app.getSummons();
                String start;
                String end;
                if (!req.get("start").isEmpty()) {
                    start = req.get("start").split("T")[0];
                    Date newStart = formatter.parse(start);
                    summon.setSummonStartDate(newStart);
                }

                if (!req.get("end").isEmpty()) {
                    end = req.get("end").split("T")[0];
                    Date newEnd = formatter.parse(end);
                    summon.setSummonEndDate(newEnd);
                }

                if (!req.get("time").isEmpty()) {
                    summon.setTime(req.get("time") + " HRS");
                }
                app.setSummons(summon);
                appealsRepository.save(app);
            }

            res.setStatus(true);
            res.setCode(ResponseCode.SUCCESS);


        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(ResponseCode.FAILURE);
            res.setStatus(false);
            res.setDescription("Error Occurred During Update");

        }
        return res;
    }


    @PostMapping(value = "/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Response uploadFile(@RequestParam MultipartFile file, @RequestParam String appeal, @RequestParam String remarks) throws IllegalStateException, IOException {


        Response res = new Response();
        Appeals appeals = appealsRepository.findByappealNo(appeal);
        System.out.println("appeal: " + appeals.toString());
        Set evidenceSet = appeals.getAnnextors();

        Evidence evi = new Evidence();
        evi.setFileName(file.getOriginalFilename());
        evi.setEvidenceType("ANNEXTORS");
        evi.setEvidenceRemarks(remarks);

        try {
            //Evidence newEvi = evidenceRepo.save(evi);
            evidenceSet.add(evi);

            appeals.setAnnextors(evidenceSet);
            appealsRepository.save(appeals);
        } catch (Exception e) {
            System.out.println("inside errors");
            e.printStackTrace();
        }

        System.out.println(String.format("File name '%s' uploaded successfully.", file.getOriginalFilename()));
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
    public List<Payment> getPayments(@RequestBody Map<String, String> req) throws IllegalStateException {

        return globalMethods.findPaymentByCreature(req.get("controlNumber"), req.get("pspRef"), req.get("bank"), req.get("startDate"), req.get("endDate"));

    }

    @GetMapping(path = "/all-payments", produces = "application/json")
    @ResponseBody
    public List<Payment> getPayments() throws IllegalStateException {


        return (List<Payment>) paymentRepository.findAll();

    }

    @PostMapping(path = "/find-by-appeal-no", produces = "application/json")
    @ResponseBody
    public Appeals findByAppealNo(@RequestBody Map<String, String> req) throws IllegalStateException {
        return appealsRepository.findByAppealNo(req.get("no"));
    }

    @PostMapping(path = "/update-filled-trat", produces = "application/json")
    @ResponseBody
    public void updateFilledTrat(@RequestBody Map<String, String> req) throws IllegalStateException {

        System.out.println("appeal number: " + req);
        Appeals appeals = appealsRepository.findByappealNo(req.get("appealNumber"));
        appeals.setIsFilledTrat(true);
        appealsRepository.save(appeals);
    }

    @PostMapping(path = "/update-served-by", produces = "application/json")
    @ResponseBody
    public Response<AppealServedBy> updateServedBy(@RequestBody Map<String, String> req) throws IllegalStateException {


        TrabHelper.print(req);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Response<AppealServedBy> appealsResponse = new Response<>();
        try {

            Appeals appeals = appealsRepository.findAppealsByAppealNoAndTax_Id(req.get("appNo"), req.get("tax")).get(0);
            AppealServedBy appealServedBy = new AppealServedBy();
            appealServedBy.setAppName(req.get("appName"));
            appealServedBy.setAppPhone(req.get("appPhone"));
            appealServedBy.setAppDate(formatter.parse(req.get("appDate").split("T")[0]));

            appealServedBy.setResoName(req.get("resoName"));
            appealServedBy.setResoPhone(req.get("resoPhone"));
            appealServedBy.setResoDate(formatter.parse(req.get("resoDate").split("T")[0]));


            String filePath = "";
            String binaryData = "";
            if (req.get("file1") != null) {
                appealServedBy.setAppellantFile(req.get("fileName1"));
                //check if LocationSketch available then upload
                binaryData = req.get("file1");
                filePath = req.get("fileName1");


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
            if (req.get("file2") != null) {
                appealServedBy.setRespondentFile(req.get("fileName2"));
                //check if LocationSketch available then upload
                binaryData2 = req.get("file2");
                filePath2 = req.get("fileName2");


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
            e.printStackTrace();
            appealsResponse.setCode(ResponseCode.FAILURE);
            appealsResponse.setStatus(false);
            appealsResponse.setData(null);
            appealsResponse.setDescription("Failure");

        }
        return appealsResponse;
    }

    @PostMapping(path = "/appeals-date", produces = "application/json")
    @ResponseBody
    public Page<Appeals> getAppealsByDateRange(@RequestBody Map<String, String> req) throws IllegalStateException {

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

        if (req.get("tax") == null || req.get("tax").isEmpty())
            return appealsRepository.findAppealsByDateOfFillingBetweenOrderByDateOfFillingAsc(start, end, pageable);
        else {
            return appealsRepository.findAppealsByDateOfFillingBetweenAndTax_IdOrderByDateOfFillingAsc(start, end, req.get("tax"), pageable);
        }

    }


    @PostMapping(path = "load-backlog", produces = "application/json")
    @ResponseBody
    public Response uploadLogBack(@RequestBody Map<String, String> requestBody) {
        TrabHelper.print(requestBody);
        return appealService.uploadAppealManually(requestBody);
    }

    @PostMapping(path = "load-backlog-appeal", produces = "application/json")
    @ResponseBody
    public Response uploadLogBackAppeal(@RequestBody Map<String, String> requestBody) {


        TrabHelper.print(requestBody);


        Region region = regionRepository.findById(requestBody.get("region")).get();
        String appealNo = region.getCode() + "." + requestBody.get("appealNo");

        System.out.println("Appeal No" + appealNo);
        TaxType taxType = taxTypeRepository.findById(requestBody.get("tax")).get();
        System.out.println("Taxes" + taxType.getTaxName());

        Response response = new Response();

        if (appealsRepository.findAppealsByAppealNoAndTax_Id(appealNo, requestBody.get("tax")).size() > 0) {
            response = new Response();
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
    public Response uploadLogBackPending(@RequestBody Map<String, String> req) {

        Response res = new Response();

        try {
            Summons summons = new Summons();
            Date startDate;
            Date endDate;
            Date lastOrderDate;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            String startDates[] = req.get("startDate").split("T");
            String endDates[] = req.get("endDate").split("T");
            String lastOrderDates[] = req.get("lastOrderDate").split("T");


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

            Judge judge = judgeService.findById(req.get("judge"));

            summons.setJudge(judge.getName());
            summons.setJud(judge);


            mapList = mapper.readValue(req.get("appList"), List.class);

            if (mapList.size() < 1) {
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

            SystemUser user = userRepository.findById(loggedUser.getInfo().getId()).get();

            summons.setAppList(appList.get());
            summons.setVenue("");
            summons.setSummonStartDate(startDate);
            summons.setSummonEndDate(endDate);
            summons.setSystemUser(user);
            summons.setCreatedDate(new Date());
            summons.setMemberOne(req.get("memberOne"));
            summons.setMemberTwo(req.get("memberTwo"));

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
            e.printStackTrace();
            res.setCode(ResponseCode.FAILURE);
            res.setDescription("Error Occured");
            res.setStatus(false);
            return res;
        }
    }


    @GetMapping(value = "/mark-delete-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response markForDelete(@PathVariable("id") Long id) {
        Response response = new Response();

        try {

            Appeals appeal = appealsRepository.findById(id).get();

            if (appeal == null) {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Appeal Doesnt exist");
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
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("System issue you can perform this now");
            return response;
        }
    }


    @GetMapping(value = "/unmark-delete-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response  removeFromMarkDelete(@PathVariable("id") Long id) {
        Response response = new Response();

        try {

            Appeals appeal = appealsRepository.findById(id).get();

            if (appeal == null) {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Appeal Doesnt exist");
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
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("System issue you can perform this now");
            return response;
        }
    }

    @GetMapping(value = "/delete-appeal/{id}", produces = "application/json")
    @ResponseBody
    public Response deleteAppeal(@PathVariable("id") Long id) {

        Response response = new Response();

        try {
            Appeals appeal = appealsRepository.findById(id).get();

            if (appeal == null) {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Appeal Doesnt exist");
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
                    "Deleted and backUp successful");

            DeletedAppeals deletedAppeals = new DeletedAppeals();
            TrabHelper.copyNonNullProperties(appeal, deletedAppeals);
            deletedAppealRepository.save(deletedAppeals);


            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("Sytem issue you can pefom this now");
            return response;
        }
    }


    @PostMapping(path = "/register-for-retrial", produces = "application/json")
    @ResponseBody
    public Response registerForRetrial(@RequestBody Map<String, String> req) {
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
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("Error occurred while fetching appeal");
        }
        return response;
    }

}

