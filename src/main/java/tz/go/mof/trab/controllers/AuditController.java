package tz.go.mof.trab.controllers;


import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.service.GfsServiceImpl;
import tz.go.mof.trab.utils.TrabHelper;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuditController {

    @Autowired
    private EntityManagerFactory factory;

    @Autowired
    private BillRepository billRepository;

    private static final Logger logger = LoggerFactory.getLogger(GfsServiceImpl.class);


    @PostMapping(produces = "application/json", path = "/audit/logs")
    @ResponseBody
    public List<Map<String, Object>> fetchAuditLogs(@RequestBody Map<String, String> req) {
        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, Object> map  = new HashMap<>();


        logger.info("###### Audit Logs #########");
        TrabHelper.print(req);

        String table = req.get("table");
        String dateFrom  = req.get("dateFrom");
        String dateTo = req.get("dateTo");
        AuditReader reader = AuditReaderFactory.get(factory.createEntityManager());

        switch (table){
            case "GFS":
                AuditQuery query = reader.createQuery()
                        .forRevisionsOfEntity(Gfs.class, true, true);

                query.addOrder(AuditEntity.revisionNumber().desc());
                List<Gfs> gfsList= query.getResultList();

                for (Gfs gfs: gfsList){
                    map = new HashMap<String, Object>();
                    map.put("id", gfs.getId());
                    map.put("name", gfs.getGfsName());
                    map.put("code", gfs.getGfsCode());
                    map.put("action", gfs.getAction());

                    if(gfs.getAction().equals("1")){
                        map.put("actionBy", gfs.getCreatedBy());
                        map.put("actionAt", gfs.getCreatedAt());
                    }else if(gfs.getAction().equals("2")){
                        map.put("actionBy", gfs.getUpdatedBy());
                        map.put("actionAt", gfs.getUpdatedAt());
                    }else  if(gfs.getAction().equals("3")){
                        map.put("actionBy", gfs.getDeletedBy());
                        map.put("actionAt", gfs.getDeletedAt());
                    }
                    response.add(map);
                }
                break;
            case  "REVENUE":
                AuditQuery revenueQuery = reader.createQuery()
                        .forRevisionsOfEntity(Fees.class, true, true);

                revenueQuery.addOrder(AuditEntity.revisionNumber().desc());

                List<Fees> fees = revenueQuery.getResultList();
                for (Fees fee: fees){
                    map = new HashMap<String, Object>();
                    map.put("id", fee.getId());
                    map.put("name", fee.getRevenueName());
                    map.put("code", fee.getAmount());
                    map.put("action", fee.getAction());

                    if(fee.getAction().equals("1")){
                        map.put("actionBy", fee.getCreatedBy());
                        map.put("actionAt", fee.getCreatedAt());
                    }else if(fee.getAction().equals("2")){
                        map.put("actionBy", fee.getUpdatedBy());
                        map.put("actionAt", fee.getUpdatedAt());
                    }else  if(fee.getAction().equals("3")){
                        map.put("actionBy", fee.getDeletedBy());
                        map.put("actionAt", fee.getDeletedAt());
                    }
                    response.add(map);
                }
                break;
            case "BILL":
                AuditQuery billQuery = reader.createQuery()
                        .forRevisionsOfEntity(Bill.class, true, true);

                billQuery.addOrder(AuditEntity.revisionNumber().desc());

                List<Bill> bills = billQuery.getResultList();
                for (Bill bill: bills){
                    map = new HashMap<>();
                    map.put("id", bill.getBillControlNumber());
                    map.put("name", bill.getBillDescription());
                    map.put("code", bill.getPayerName());
                    map.put("action", bill.getAction());

                    if(bill.getAction().equals("1")){
                        map.put("actionBy", bill.getApprovedBy());
                        map.put("actionAt", bill.getGeneratedDate());
                    }else if(bill.getAction().equals("2")){
                        map.put("actionBy", bill.getApprovedBy());
                        map.put("actionAt", bill.getGeneratedDate());
                    }else  if(bill.getAction().equals("3")){
                        map.put("actionBy", bill.getApprovedBy());
                        map.put("actionAt", bill.getGeneratedDate());
                    }
                    response.add(map);
                }
                break;

            case "PAYMENT":
                AuditQuery paymentQuery = reader.createQuery()
                        .forRevisionsOfEntity(Payment.class, true, true);

                paymentQuery.addOrder(AuditEntity.revisionNumber().desc());

                List<Payment> payments = paymentQuery.getResultList();
                for (Payment payment:payments){
                    map = new HashMap<>();
                    map.put("id", payment.getBill().getBillControlNumber());
                    map.put("name", payment.getPayRefId());
                    map.put("code", payment.getPspReceiptNumber());
                    map.put("action", payment.getAction());

                    if(payment.getAction().equals("1")){
                        map.put("actionBy", "GePG Sysytem");
                        map.put("actionAt", payment.getCreatedDate());
                    }else if(payment.getAction().equals("2")){
                        map.put("actionBy", "GePG Sysytem");
                        map.put("actionAt", payment.getCreatedDate());
                    }else  if(payment.getAction().equals("3")){
                        map.put("actionBy", "GePG Sysytem");
                        map.put("actionAt", payment.getCreatedDate());
                    }
                    response.add(map);
                }
                break;
            case "NOTICE":
                AuditQuery noticeQuery = reader.createQuery()
                        .forRevisionsOfEntity(Notice.class, true, true);

                noticeQuery.addOrder(AuditEntity.revisionNumber().desc());

                List<Notice> notices = noticeQuery.getResultList();
                for (Notice notice:notices){
                    map = new HashMap<>();
                    map.put("id", notice.getNoticeNo());
                    map.put("name", notice.getAppelantName());
                    map.put("code",  billRepository.findById(notice.getBillId().getBillId()).get().getBillControlNumber());
                    map.put("action", notice.getAction());

                    if(notice.getAction().equals("1")){
                        map.put("actionBy", notice.getCreatedBy());
                        map.put("actionAt", notice.getCreatedAt());
                    }else if(notice.getAction().equals("2")){
                        map.put("actionBy", notice.getUpdatedBy());
                        map.put("actionAt", notice.getUpdatedAt());
                    }else  if(notice.getAction().equals("3")){
                        map.put("actionBy", notice.getUpdatedBy());
                        map.put("actionAt", notice.getUpdatedAt());
                    }
                    response.add(map);
                }
                break;
            case "APPEAL":
                AuditQuery appealQuery = reader.createQuery()
                        .forRevisionsOfEntity(Appeals.class, true, true);

                appealQuery.addOrder(AuditEntity.revisionNumber().desc());

                List<Appeals> appeals = appealQuery.getResultList();
                for (Appeals appeal:appeals){
                    map = new HashMap<>();
                    map.put("id", appeal.getAppealNo());
                    map.put("name", appeal.getAppellantName());
                    map.put("code", billRepository.findById(appeal.getBillId().getBillId()).get().getBillControlNumber());
                    map.put("action", appeal.getAction());

                    if(appeal.getAction().equals("1")){
                        map.put("actionBy", appeal.getCreatedBy());
                        map.put("actionAt", appeal.getCreatedAt());
                    }else if(appeal.getAction().equals("2")){
                        map.put("actionBy", appeal.getUpdatedBy());
                        map.put("actionAt", appeal.getUpdatedAt());
                    }else  if(appeal.getAction().equals("3")){
                        map.put("actionBy", appeal.getUpdatedBy());
                        map.put("actionAt", appeal.getUpdatedAt());
                    }
                    response.add(map);
                }
                break;
            case "APPLICATION":
                AuditQuery  appQuery = reader.createQuery()
                        .forRevisionsOfEntity(ApplicationRegister.class, true, true);

                appQuery.addOrder(AuditEntity.revisionNumber().desc());

                List<ApplicationRegister>applicationRegisters = appQuery.getResultList();
                for (ApplicationRegister applicationRegister:applicationRegisters){
                    map = new HashMap<>();
                    map.put("id",applicationRegister.getApplicationNo());
                    map.put("name", applicationRegister.getApplicant().getFirstName() );
                    map.put("code", billRepository.findById(applicationRegister.getBillId().getBillId()).get().getBillControlNumber());
                    map.put("action", applicationRegister.getAction());

                    if(applicationRegister.getAction().equals("1")){
                        map.put("actionBy", applicationRegister.getCreatedBy());
                        map.put("actionAt", applicationRegister.getCreatedAt());
                    }else if(applicationRegister.getAction().equals("2")){
                        map.put("actionBy", applicationRegister.getUpdatedBy());
                        map.put("actionAt", applicationRegister.getUpdatedAt());
                    }else  if(applicationRegister.getAction().equals("3")){
                        map.put("actionBy", applicationRegister.getUpdatedBy());
                        map.put("actionAt", applicationRegister.getUpdatedAt());
                    }
                    response.add(map);
                }
                break;

            default:
                logger.info("############# No choice selected ###########");
                break;
        }
        logger.info("######## Response From Audit Logs #########");
        TrabHelper.print(map);

        return response;
    }
}
