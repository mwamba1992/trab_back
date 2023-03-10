package tz.go.mof.trab.controllers;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.dto.report.SummonDto;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.service.FinancialYearService;
import tz.go.mof.trab.service.SummonsService;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;

@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping("/summon")
public class SummonsController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private SummonsService summonsService;

    @Autowired
    private SummonsRepository summonRepository;


    @Autowired
    private AppealsRepository appRepo;

    @Autowired
    private SummonsAppealsRepository sumAppRepo;


    @Value("${tz.go.trab.upload.dir}")
    private String uploadingDir;

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private GlobalMethods globalMethods;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private AppealsRepository appealsRepository;

    @Autowired
    private ApplicationRegisterRepository applicationRegisterRepository;

    @Autowired
    private FinancialYearService financialYearService;

    @PostMapping(path = "/internalCreate")
    @ResponseBody
    public Response<Summons> createSummos(@RequestBody Map<String, String> req) throws JAXBException {
        return globalMethods.createSummon(req, true);
    }

    @PostMapping(path = "/internalEdit")
    @ResponseBody
    public Response<Summons> editSummon(@RequestBody Map<String, String> req) throws JAXBException {
        System.out.println("req: " + req);
        return globalMethods.createSummon(req, false);
    }

    @PostMapping(path = "/internalDelete")
    @ResponseBody
    public Response<Summons> deleteSummon(@RequestBody Map<String, String> req) throws JAXBException {

        Response<Summons> res = new Response<Summons>();
        try {

            appRepo.updateAppealRemoveSummon(Long.valueOf(req.get("summonId")));

            sumAppRepo.deleteSummonsAppeals(Long.valueOf(req.get("summonId")));
            summonRepository.delete(summonRepository.findById(Long.valueOf(req.get("summonId"))).get());
            res.setStatus(true);
            res.setCode(ResponseCode.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(false);
            res.setCode(ResponseCode.FAILURE);
            res.setDescription("Unable To Delete Summon");

        }

        return res;
    }

    @PostMapping(path = "/uploadFile/{id}")
    @ResponseBody
    public Response<Summons> uploadFile(@PathVariable("id") Long id,
                                        @RequestBody Map<String, String> req) throws JAXBException {
        Summons summon = summonRepository.findById(id).get();

        Response<Summons> res = new Response<Summons>();

        System.out.println("#########" + req);
        try {
            summon.setFilePath(req.get("fileName"));
            summon.setReceived(true);
            summon.setReceivedAt(new Date());
            summonRepository.save(summon);

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

            res.setCode(ResponseCode.SUCCESS);
            res.setStatus(true);
            res.setDescription("SUCCESS");
            res.setData(summon);
        } catch (Exception e) {
            res.setData(null);
            res.setStatus(false);
            res.setCode(ResponseCode.FAILURE);
        }
        return res;
    }


    @GetMapping(path = "/get-summons-from-id/{summonId}")
    @ResponseBody
    public List<SummonsAppeal> getSummons(@PathVariable Long summonId) throws JAXBException {

        return sumAppRepo.getAppealFromSummons(summonId);

    }


    @GetMapping(path = "/get-summons-dtos")
    @ResponseBody
    public List<SummonDto> getSummonDto(){

        BillSearchDto billSearchDto = new BillSearchDto();

        List<Summons> summonsList = summonsService.findByPage().getContent();
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFor = new SimpleDateFormat("dd MMMM yyyy");

        List<SummonDto> summonDtoList = new ArrayList<SummonDto>();
        globalMethods.getSummonDtos(summonsList, dateFor, summonDtoList);
        return  summonDtoList;

    }

    @GetMapping(path = "/bill-payment-report")
    @ResponseBody
    public Map<String, Integer> getBillPayment(){

        int noticeCount = noticeRepository.getNoticeCountFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());
        int appealsCount = appealsRepository.getAppealsCountByFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());
        int applicationCount = applicationRegisterRepository.countApplicationRegisterByFinancialYear(financialYearService.getActiveFinalYear().getData().getFinancialYear());



        Map<String, Integer> map = new HashMap<>();
        map.put("payments", noticeCount);
        map.put("bills",  appealsCount);
        map.put("defaulters", applicationCount);

        return map;

    }

}
