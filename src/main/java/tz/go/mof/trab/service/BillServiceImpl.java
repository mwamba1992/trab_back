package tz.go.mof.trab.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;

import lombok.experimental.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.*;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.BillSummary;
import tz.go.mof.trab.models.Gfs;
import tz.go.mof.trab.repositories.BillItemRepository;
import tz.go.mof.trab.repositories.BillRepository;
import tz.go.mof.trab.utils.*;
import tz.go.mof.trab.utils.Response;

@Service
@Transactional
public class BillServiceImpl implements BillService {

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);
    @Value("${tz.go.trab.noOfDays}")
    private int billDays;
    private final BillRepository billRepository;
    @Autowired
    private GepgMiddleWare gepgMiddleWare;
    private final GlobalMethods globalMethods;
    private final GfsService gfsService;
    private final BillItemRepository billItemRepository;
    private Date billExpireDate;

    @PersistenceContext
    private EntityManager em;

    BillServiceImpl(BillRepository billRepository,GlobalMethods globalMethods,
                    GfsService gfsService, BillItemRepository billItemRepository){
        this.billRepository = billRepository;
        this.globalMethods = globalMethods;
        this.gfsService = gfsService;
        this.billItemRepository = billItemRepository;
    }


    @Override
    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }


    @Override
    public void editBill(String controlNumber, String billId, String responseCode) {
        billRepository.editBill(controlNumber, billId,responseCode); }

    @Override
    public Bill findBillById(String billId) { return billRepository.findById(billId).get();}


    @Override
    public Response<BillDto> createBill(PortalBillRequestDto billRequestDto, boolean isFromConsumer) {
        Response<BillDto> response = new Response<>();
        BillDto billDto = new BillDto();
        TrabHelper.print(billRequestDto);
        try {
             if(!globalMethods.validatePhoneNumber(billRequestDto.getPhoneNumber())){
                 response.setCode(ResponseCode.FAILURE);
                 response.setStatus(false);
                 response.setData(null);
                 response.setDescription("Invalid phone number supplied");
                 return  response;
            }

            if(!globalMethods.isValidEmailAddress(billRequestDto.getEmail())){
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setData(null);
                response.setDescription("Invalid Email Address supplied");
                return  response;
            }

            Bill savedBill = globalMethods.savingBillFromDto(billRequestDto);


            if(gepgMiddleWare.sendRequestToGepg(savedBill)){
                response.setCode(ResponseCode.SUCCESS);
                response.setStatus(true);
                Bill bill = findBillById(savedBill.getBillId());
                TrabHelper.copyNonNullProperties(bill, billDto);
                billDto.setBillItems(billItemRepository.getBillItemOfTheSameBill(bill.getBillId()));
                logger.info(billDto.toString());
                response.setData(billDto);
                response.setDescription("Success");

            }else{
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setData(null);
                response.setDescription("Failure");
            }

            return response;
           }
        catch (Exception e){
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(true);
            response.setData(null);
            return  response;

        }
    }

    @Override
    public ResponseEntity<Object> receiveBill(String requestBody) {

        BillRespWrapper billResp;
        try {

            String responseString;

            logger.info("##### Bill Final Response #####" + globalMethods.beautifyXmlString(requestBody));

            billResp = (BillRespWrapper) globalMethods.convertStringToXml(JAXBContext.newInstance(BillRespWrapper.class),
                    requestBody);

            if(billResp.getBillResp().getTrx().getTrxStsCode().equalsIgnoreCase("7101 ")){

            }else{

            }

            editBill(billResp.getBillResp().getTrx().getPayCntrNum(),
                    billResp.getBillResp().getTrx().getBillId(), billResp.getBillResp().getTrx().getTrxStsCode());

            responseString = gepgMiddleWare.constructAckToGepg();

            return new ResponseEntity<>(responseString, HttpStatus.ACCEPTED);
        }
        catch(Exception e ){
            e.printStackTrace();
            return  null;
        }
    }

    public Date getBillExpireDate() {
        return billExpireDate;
    }

    public void setBillExpireDate(Date billExpireDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billExpireDate);
        calendar.add(Calendar.DAY_OF_YEAR, billDays);
        this.billExpireDate = calendar.getTime();
    }


    @Override
    public Response<Bill> cancelBill(String billId) {
        Response<Bill> response = new Response<>();
        try {

            Bill bill = findBillById(billId);
            if (bill != null) {
                if (gepgMiddleWare.sendCancelRequest(bill)) {
                    response.setCode(ResponseCode.SUCCESS);
                    response.setStatus(true);
                    response.setData(bill);
                    response.setDescription("Bill Cancelled Successful");
                }else{
                    response.setCode(ResponseCode.FAILURE);
                    response.setStatus(true);
                    response.setData(bill);
                    response.setDescription("Failed To Cancel Bill");
                }
            } else {
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setStatus(false);
                response.setData(null);
                response.setDescription("No Bill To Cancel Found");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("##########" + e.getMessage());
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setData(null);
            response.setDescription("Exception occurred while Cancelling Bill");
        }
        return response;
    }

    @Override
    public List<Bill> searchBills(int page, int size, BillSearchDto billSearchDto) {

        logger.info("#######  Query Params For Bills #######" + billSearchDto);
        TrabHelper.print(billSearchDto);


        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";


        if((!billSearchDto.getRegionCode().isEmpty()) && (billSearchDto.getCouncilCode().isEmpty())){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.region_code=:region_code ";
        }

        if((!billSearchDto.getCouncilCode().isEmpty()) && (billSearchDto.getCouncilCode())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.region_code=:region_code AND d.council_code=:council_code ";
        }


        if((!billSearchDto.getStatus().isEmpty()) && (billSearchDto.getStatus())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.bill_payed=:bill_payed ";
        }

        if((!billSearchDto.getControlNumber().isEmpty()) && (billSearchDto.getControlNumber())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.bill_control_number=:bill_control_number ";
        }

        if((!billSearchDto.getPayerName().isEmpty()) && (billSearchDto.getPayerName())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.payer_name=:payer_name ";
        }

        if((!billSearchDto.getAmountFrom().isEmpty()) && (billSearchDto.getAmountFrom())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.billed_amount >=:billed_amount ";
        }

        if((!billSearchDto.getDateFrom().isEmpty()) && (billSearchDto.getDateFrom())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.generated_date >=:generated_date ";
        }


        if((!billSearchDto.getAmountTo().isEmpty()) && (billSearchDto.getAmountTo())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.billed_amount <=:billed_amount ";
        }

        if((!billSearchDto.getDateTo().isEmpty()) && (billSearchDto.getDateTo())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.generated_date <=:generated_to ";
        }

        if((!billSearchDto.getSourceId().isEmpty()) && (billSearchDto.getSourceId())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.item_id =:item_id ";
        }

        if(billSearchDto.getRegionCode().isEmpty()&&billSearchDto.getControlNumber().isEmpty()&&billSearchDto.getCouncilCode().isEmpty()&&
        billSearchDto.getPayerName().isEmpty()&&billSearchDto.getAmountFrom().isEmpty()&&billSearchDto.getAmountTo().isEmpty()&&billSearchDto.getDateFrom().isEmpty()
                &&billSearchDto.getStatus().isEmpty()&&billSearchDto.getDateTo().isEmpty()&&billSearchDto.getSourceId().isEmpty()){
            sqlQuery = " select * from bill d where d.bill_control_number !='0' ORDER BY  generated_date DESC";
        }else {
            parameter = " where " + parameter;
            sqlQuery = " select * from bill d " + parameter +" AND  d.bill_control_number !='0' ORDER BY  generated_date DESC";
        }

        logger.info("#########" + sqlQuery);
        System.out.println(sqlQuery);

        Query q = em.createNativeQuery(sqlQuery, Bill.class);


        if((!billSearchDto.getStatus().isEmpty()) && (billSearchDto.getStatus())!=null){
            if(billSearchDto.getStatus().equals("1")) {
                q.setParameter("bill_payed", 1);
            }else{
                q.setParameter("bill_payed", 0);
            }
        }

        if((!billSearchDto.getPayerName().isEmpty()) && (billSearchDto.getPayerName())!=null){
             q.setParameter("payer_name", billSearchDto.getPayerName());
        }

        if((!billSearchDto.getControlNumber().isEmpty()) && (billSearchDto.getControlNumber())!=null){
            q.setParameter("bill_control_number", billSearchDto.getControlNumber());
        }

        if((!billSearchDto.getAmountFrom().isEmpty()) && (billSearchDto.getAmountFrom())!=null){
             q.setParameter("billed_amount", billSearchDto.getAmountFrom());
        }

        if((!billSearchDto.getAmountTo().isEmpty()) && (billSearchDto.getAmountTo())!=null){
            q.setParameter("billed_amount", billSearchDto.getAmountTo());
        }

        if((!billSearchDto.getDateFrom().isEmpty()) && (billSearchDto.getDateFrom())!=null){
            q.setParameter("generated_date", billSearchDto.getDateFrom());
        }
        if((!billSearchDto.getDateTo().isEmpty()) && (billSearchDto.getDateTo())!=null){
            q.setParameter("generated_to", billSearchDto.getDateTo());
        }

        if((!billSearchDto.getSourceId().isEmpty()) && (billSearchDto.getSourceId())!=null){
            q.setParameter("item_id", billSearchDto.getSourceId().split("-")[0]);
        }

        if((!billSearchDto.getRegionCode().isEmpty()) && (billSearchDto.getCouncilCode().isEmpty())){
            q.setParameter("region_code", billSearchDto.getRegionCode().split("-")[0]);
        }


        if((!billSearchDto.getCouncilCode().isEmpty()) && (billSearchDto.getCouncilCode())!=null){
            q.setParameter("region_code", billSearchDto.getRegionCode().split("-")[0]);
            q.setParameter("council_code", billSearchDto.getCouncilCode().split("-")[0]);
        }


        List<Bill> billList = q.getResultList();

        return  billList;

    }




    @Override
    public Map<String, String> getBillCount(BillSummaryReportDto billSummaryReportDto, String itemId, boolean isCount) {

        logger.info("#######  Query Params For Bills #######" + billSummaryReportDto);

        List<Map<String, String>> list = new ArrayList<>();

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";



        if((!billSummaryReportDto.getRegionCode().isEmpty()) && (billSummaryReportDto.getCouncilCode().isEmpty())){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " region_code=:region_code ";
        }

        if((!billSummaryReportDto.getCouncilCode().isEmpty()) && (billSummaryReportDto.getCouncilCode())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " region_code=:region_code AND council_code=:council_code ";
        }

        if((!billSummaryReportDto.getDateFrom().isEmpty()) && (billSummaryReportDto.getDateFrom())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " generated_date >=:generated_date ";
        }


        if((!billSummaryReportDto.getDateTo().isEmpty()) && (billSummaryReportDto.getDateTo())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " generated_date <=:generated_date ";
        }

        if(parameter.isEmpty() || parameter.equals("")){
            parameter = parameter + " item_id=:item_id";

        }else{
            parameter = parameter + " AND item_id=:item_id";
        }

        parameter = " where " + parameter;
        if(isCount) {
            sqlQuery = " SELECT status,COUNT(*) from bill " + parameter + " GROUP BY status ";
        }else{
            sqlQuery = " SELECT status,SUM(billed_amount) from bill " + parameter +  " GROUP BY status "  ;
        }

        logger.info("#########" + sqlQuery);
        Query q = em.createNativeQuery(sqlQuery);

        if((!billSummaryReportDto.getDateFrom().isEmpty()) && (billSummaryReportDto.getDateFrom())!=null){
            q.setParameter("generated_date", billSummaryReportDto.getDateFrom());
        }
        if((!billSummaryReportDto.getDateTo().isEmpty()) && (billSummaryReportDto.getDateTo())!=null){
            q.setParameter("generated_date", billSummaryReportDto.getDateTo());
        }

        if((!billSummaryReportDto.getRegionCode().isEmpty()) && (billSummaryReportDto.getCouncilCode().isEmpty())){
            q.setParameter("region_code", billSummaryReportDto.getRegionCode().split("-")[0]);
        }


        if((!billSummaryReportDto.getCouncilCode().isEmpty()) && (billSummaryReportDto.getCouncilCode())!=null){
            q.setParameter("region_code", billSummaryReportDto.getRegionCode().split("-")[0]);
            q.setParameter("council_code", billSummaryReportDto.getCouncilCode().split("-")[0]);
        }


        q.setParameter("item_id", itemId);


        List<Object> objects = q.getResultList();




        Map<String, String> newMap = new HashMap<>();

        if(objects.size()>0) {
            objects.forEach(ob -> {
                Object[] fields = (Object[]) ob;
                String status = (String) fields[0];
                if(isCount) {
                    BigInteger count = (BigInteger) fields[1];
                    newMap.put(status, count.toString());
                }else{
                    BigDecimal count = (BigDecimal) fields[1];
                    newMap.put(status, count.toString());
                }
            });
        }
        return newMap;
    }


    @Override
    public List<BillSummary> getBIllSummary(BillSummaryReportDto billSummaryReportDto, boolean isCount) {


        List<Gfs> gfsList = gfsService.findAllGfs().getData();


        List<BillSummary> billSummaries = new ArrayList<>();



        gfsList.forEach(group->{
            logger.info("########### Item ##########" + group.getGfsName() + " item Id: " + group.getGfsCode());

            Map<String, String> map = getBillCount(billSummaryReportDto, group.getGfsCode(), isCount);



            if(isCount) {
                if (map.size() > 0) {

                    BillSummary billSummary = new BillSummary();
                    billSummary.setItemId(group.getId());
                    billSummary.setName(group.getGfsName());
                    billSummary.setPaidBills(Double.parseDouble(map.get("PAID") == null ? "0" : map.get("PAID")));
                    billSummary.setExpiredBills(Double.parseDouble(map.get("EXPIRED") == null ? "0" : map.get("EXPIRED")));
                    billSummary.setPendingBills(Double.parseDouble(map.get("PENDING") == null ? "0" : map.get("PENDING")));

                    billSummary.setTotalBills(Double.parseDouble(map.get("PAID") == null ? "0" : map.get("PAID")) +
                            Double.parseDouble(map.get("EXPIRED") == null ? "0" : map.get("EXPIRED")) +
                            Double.parseDouble(map.get("PENDING") == null ? "0" : map.get("PENDING")));
                    billSummaries.add(billSummary);


                    System.out.println("paid" + billSummaries);
                }
            }else {

                if (map.size() > 0) {

                    BillSummary billSummary = new BillSummary();
                    billSummary.setItemId(group.getId());
                    billSummary.setName(group.getGfsName());
                    billSummary.setPaidBillsAmount(new BigDecimal(map.get("PAID") == null ? "0" : map.get("PAID")));
                    billSummary.setExpiredBills(Double.parseDouble(map.get("EXPIRED") == null ? "0" : map.get("EXPIRED")));
                    billSummary.setPendingAmount(new BigDecimal(map.get("PENDING") == null ? "0" : map.get("PENDING")));
                    billSummary.setTotalBillsAmount(new BigDecimal(map.get("PAID") == null ? "0" : map.get("PAID")).add(
                            new BigDecimal(map.get("PENDING") == null ? "0" : map.get("PENDING"))
                    ));

                    billSummaries.add(billSummary);


                    System.out.println("paid" + billSummaries);
                }
            }

        });

        return billSummaries;

    }

    @Override
    public ListResponse<Bill> getResponseCodeNullResponse() {
        logger.info("#### Find Bill With Null Response Code #### ");
        ListResponse<Bill> response = new ListResponse<>();
        try {
            List<Bill> bills = billRepository.findBillByResponseCodeNot("7101");

            if (bills.size() > 0) {
                response.setCode(ResponseCode.SUCCESS);
                response.setStatus(false);
                response.setData(bills);
            } else {
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setStatus(true);
                response.setData(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("###### Response of Bill Item #####");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<Bill> billResend(Map<String, String> req) {
        Response<Bill> response = new Response<>();
        try {
            if (billRepository.findById(req.get("billId")).isPresent()) {
                Bill bill = billRepository.findById(req.get("billId")).get();
                if (gepgMiddleWare.sendRequestToGepg(bill)) {
                    response.setCode(ResponseCode.SUCCESS);
                    response.setStatus(false);
                    response.setData(bill);
                } else {
                    response.setCode(ResponseCode.FAILURE);
                    response.setStatus(true);
                    response.setData(null);
                }
            } else {
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setStatus(true);
                response.setData(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(true);
            response.setDescription("Exception occured ");
        }
        return response;
    }

}
