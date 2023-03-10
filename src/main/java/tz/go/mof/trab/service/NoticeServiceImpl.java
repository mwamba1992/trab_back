package tz.go.mof.trab.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.utils.GepgMiddleWare;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private Date billExpireDate;

    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    private final NoticeRepository noticeRepository;

    private final LoggedUser loggedUser;

    private final FeesRepository feesRepository;

    private final UserRepository userRepository;

    private final AdressRepository adressRepository;

    private final RegionService regionService;

    private final AppealantRepository appealantRepository;

    private final CurrencyRepository currencyRepository;

    private final FinancialYearService financialYearService;

    private final BillRepository billRepository;

    private final BillItemRepository billItemRepository;

    private final GepgMiddleWare gepgMiddleWare;

    private final  ManualSequenceRepository manualSequenceRepository;

    @Value("${tz.go.trab.systemid}")
    private String systemId;

    @Value("${tz.go.trab.noOfDays}")
    private int noOfDays;

    NoticeServiceImpl(NoticeRepository noticeRepository, LoggedUser loggedUser, FeesRepository feesRepository,
                      UserRepository userRepository, AdressRepository adressRepository, RegionService regionService,
                      AppealantRepository appealantRepository, CurrencyRepository currencyRepository,
                      FinancialYearService financialYearService, BillRepository billRepository,
                      BillItemRepository billItemRepository, GepgMiddleWare gepgMiddleWare, ManualSequenceRepository manualSequenceRepository) {
        this.noticeRepository = noticeRepository;
        this.loggedUser = loggedUser;
        this.feesRepository = feesRepository;
        this.userRepository = userRepository;
        this.adressRepository = adressRepository;
        this.regionService = regionService;
        this.appealantRepository = appealantRepository;
        this.currencyRepository = currencyRepository;
        this.financialYearService = financialYearService;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.gepgMiddleWare = gepgMiddleWare;
        this.manualSequenceRepository = manualSequenceRepository;

    }


    @Override
    public Response < Notice > createNotice(Map < String, String > req) {

        System.out.println("#### inside saving notice ####");
        TrabHelper.print(req);
        Response < Notice > response = new Response < > ();
        final int currentYear = new Date().getYear() + 1900;

        try {


            Optional < Fees > fees = feesRepository.findById("NOTICE");

            if (!fees.isPresent()) {
                response.setStatus(false);
                response.setCode(ResponseCode.FAILURE);
                response.setDescription("No Fees Set For This Operation");

                return response;
            }

            Notice notice = new Notice();
            Appellant appealant = new Appellant();
            Bill bill = new Bill();
            SystemUser user = userRepository.findById(loggedUser.getInfo().getId()).get();


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            Date date1 = new Date();
            long time = date1.getTime();
            setBillExpireDate(new Date());


            String dates[] = req.get("date").split("T");
            String taxationDate[] = req.get("dateTaxation").split("T");
            String serviceTaxationDate[] = req.get("dateServiceTaxationDesicion").split("T");

            Date noticeDate = formatter.parse(dates[0]);
            Date taxDate = formatter.parse(taxationDate[0]);
            Date serviceTax = formatter.parse(serviceTaxationDate[0]);


            //setting up appealeant details
            appealant.setCreatedDate(new Date());
            appealant.setFirstName(req.get("companyName"));
            //appealant.setLastName(req.get("lastName"));
            //appealant.setTinNumber(req.get("tinNumber").isEmpty() ? "NONE" : req.get("tinNumber"));
            //appealant.setNatureOfBussiness(req.get("natOfBus").isEmpty() ? "OTHERS" : req.get("natOfBus"));
            //appealant.setVatNumber(req.get("vatNumber").isEmpty() ? "NONE" : req.get("vatNumber"));


            //setting up bill details

            bill.setAction("1");
            bill.setBillPayed(false);
            bill.setBillPayType("3");
            bill.setBillReference("NOTICE-" + time);
            bill.setBillControlNumber("0");
            bill.setBillDescription("Fee For Lodging Notice of Appeal");
            bill.setPayerEmail("registries@trab.go.tz");
            bill.setPayerPhone(req.get("phone")!=null?req.get("phone").replace("-", ""):"075310301");
            bill.setExpiryDate(getBillExpireDate());
            bill.setApprovedBy(loggedUser.getInfo().getName());
            bill.setCurrency(currencyRepository.findByCurrencyShortName("TZS").getCurrencyShortName());
            bill.setSpSystemId(systemId);
            bill.setGeneratedDate(new java.sql.Date(new java.util.Date().getTime()));
            bill.setAppType("NOTICE");
            bill.setBilledAmount(fees.get().getAmount());
            bill.setBillEquivalentAmount(fees.get().getAmount());
            bill.setMiscellaneousAmount(new BigDecimal("0.00"));
            bill.setRemarks("UNPAID");
            bill.setPayerName(req.get("companyName"));
            bill.setPaidAmount(new BigDecimal("0"));
            bill.setFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());


            Bill newBill = billRepository.save(bill);
            BillItems billItems = new BillItems();
            billItems.setBillItemDescription(bill.getBillDescription());
            billItems.setBillItemMiscAmount(new BigDecimal("0.00"));
            billItems.setBillItemRef("NOTICE" + time);
            billItems.setBillItemAmount(bill.getBilledAmount());
            billItems.setBillItemEqvAmount(bill.getBillEquivalentAmount());
            billItems.setGsfCode(fees.get().getGfs().getGfsCode());
            billItems.setBill(bill);

            billItemRepository.save(billItems);


            notice.setLoggedAt(noticeDate);
            notice.setDateOfTaxationDesicion(taxDate);
            notice.setDateOfServiceTaxationDesicion(serviceTax);
            notice.setAction("1");

            notice.setSystemUser(user);
            notice.setDes(req.get("des"));

            ManualSequence manualSequence = manualSequenceRepository.findAll().get(0);
            notice.setNoticeNo(manualSequence.getSequence() + "/" + currentYear);
            manualSequence.setSequence(manualSequence.getSequence()+1);
            manualSequenceRepository.save(manualSequence);
            Adress adress = new Adress();
            adress.setSlp(req.get("adress"));
            adress.setRegion(regionService.getRegionByCode(req.get("region")).getData());

            notice.setDes(req.get("des"));
            notice.setAdressId(adressRepository.save(adress));
            notice.setCreatedBy(loggedUser.getInfo().getName());
            notice.setAppelantName(req.get("companyName"));


            if (gepgMiddleWare.sendRequestToGepg(newBill)) {
                notice.setBillId(newBill);
                Notice newNotice = noticeRepository.save(notice);
                response.setStatus(true);
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setData(newNotice);

            } else {
                response.setStatus(false);
                response.setDescription("Problem occurred Please Contact Support! ");
                response.setCode(ResponseCode.FAILURE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(false);
            response.setDescription("Problem occurred Please Contact Support! ");
            response.setCode(ResponseCode.FAILURE);

        }


        return response;
    }

    @Override
    public Response < Notice > editNotice(Map < String, String > req) {

        Response < Notice > res = new Response < Notice > ();

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            logger.info("#### Notice Edit Details ####" + req);
            TrabHelper.print(req);


            Notice not = noticeRepository.findBynoticeNo(req.get("noticeId"));


            not.setAppelantName(req.get("companyName"));
            not.setDes(req.get("des"));


            String dates[] = req.get("date").split("T");
            String taxationDate[] = req.get("dateTaxation").split("T");
            String serviceTaxationDate[] = req.get("dateServiceTaxationDesicion").split("T");

            Date noticeDate = formatter.parse(dates[0]);
            Date taxDate = formatter.parse(taxationDate[0]);
            Date serviceTax = formatter.parse(serviceTaxationDate[0]);

            not.setAction("2");
            not.setUpdatedAt(LocalDateTime.now());
            not.setUpdatedBy(loggedUser.getInfo().getName());
            not.setLoggedAt(noticeDate);
            not.setDateOfTaxationDesicion(taxDate);
            not.setDateOfServiceTaxationDesicion(serviceTax);
            //String noticeNo = not.getNoticeNo().replace(not.getNoticeNo().substring(0, 3), req.get("region"));
            //not.setNoticeNo(noticeNo);

            Notice newNotice = noticeRepository.save(not);
            res.setStatus(true);
            res.setData(newNotice);
            res.setCode(ResponseCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            res.setDescription("Failed To Update Notice");
            res.setCode(ResponseCode.FAILURE);
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public Notice findNoticeByNoticeNo(String noticeNo) {
        return noticeRepository.findBynoticeNo(noticeNo);
    }


    public Date getBillExpireDate() {
        return billExpireDate;
    }

    public void setBillExpireDate(Date billExpireDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billExpireDate);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        this.billExpireDate = calendar.getTime();
    }
}