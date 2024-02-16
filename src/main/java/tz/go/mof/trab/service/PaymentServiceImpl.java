package tz.go.mof.trab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.PageListResponse;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.dto.payment.PaymentSummaryDto;
import tz.go.mof.trab.models.Payment;
import tz.go.mof.trab.repositories.PaymentRepository;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.ResponseCode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements  PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    GlobalMethods globalMethods;

    @PersistenceContext
    private EntityManager em;


    @Override
    public PageListResponse<Payment> findAll(int page, int size) {
        PageListResponse<Payment> billPageListResponse = new PageListResponse<Payment>();
        Pageable paging = PageRequest.of(page, size);
        Page<Payment> payments = paymentRepository.findAll(paging);

        billPageListResponse.setCode(ResponseCode.SUCCESS);
        billPageListResponse.setStatus(true);
        billPageListResponse.setData(payments);
        billPageListResponse.setTotalElements(Long.valueOf(payments.getSize()));
        return billPageListResponse;
    }


    @Override
    public List<Payment> searchPayments(PaymentSearchDto paymentSearchDto) {

        logger.info("#######  Query Params For Payments #######" + paymentSearchDto);

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";


        if((!paymentSearchDto.getPspReference().isEmpty()) && (paymentSearchDto.getPspReference())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.psp_receipt_number=:psp_receipt ";
        }

        if((!paymentSearchDto.getGepgReference().isEmpty()) && (paymentSearchDto.getGepgReference())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.pay_ref_id=:gepg_receipt ";
        }

        if((!paymentSearchDto.getRegionCode().isEmpty()) && (paymentSearchDto.getCouncilCode().isEmpty())){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " b.region_code=:region_code ";
        }

        if((!paymentSearchDto.getCouncilCode().isEmpty()) && (paymentSearchDto.getCouncilCode())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " b.region_code=:region_code AND b.council_code=:council_code ";
        }

        if((!paymentSearchDto.getControlNumber().isEmpty()) && (paymentSearchDto.getControlNumber())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.pay_ctr_num=:bill_control_number ";
        }

        if((!paymentSearchDto.getPayerName().isEmpty()) && (paymentSearchDto.getPayerName())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.pyr_name=:payer_name ";
        }

        if((!paymentSearchDto.getDateFrom().isEmpty()) && (paymentSearchDto.getDateFrom())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.trx_dtm >=:generated_date ";
        }


        if((!paymentSearchDto.getDateTo().isEmpty()) && (paymentSearchDto.getDateTo())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " d.trx_dtm <=:generate_to ";
        }

        if((!paymentSearchDto.getType().isEmpty()) && (paymentSearchDto.getType())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }

            if(paymentSearchDto.equals("XXXX")){
                 parameter = parameter + "b.app_type is null";
            }else{
                parameter = parameter + " b.app_type =:app_type ";
            }

        }




        if(paymentSearchDto.getPspReference().isEmpty()&&paymentSearchDto.getGepgReference().isEmpty()&&paymentSearchDto.getPayerName().isEmpty()
        &&paymentSearchDto.getRegionCode().isEmpty()&&paymentSearchDto.getCouncilCode().isEmpty()&&paymentSearchDto.getControlNumber().isEmpty()&&
        paymentSearchDto.getDateFrom().isEmpty()&&paymentSearchDto.getDateTo().isEmpty()){
            sqlQuery = " select * from payment ORDER BY created_date DESC";
        }else {
            parameter = " where " + parameter;
            sqlQuery = " select * from payment d JOIN bill b ON d.bill_id=b.bill_id " + parameter + " ORDER BY created_date DESC";
            logger.info("#########" + sqlQuery);
        }

        Query q = em.createNativeQuery(sqlQuery, Payment.class);


        if((!paymentSearchDto.getPayerName().isEmpty()) && (paymentSearchDto.getPayerName())!=null){
            q.setParameter("payer_name", paymentSearchDto.getPayerName());
        }

        if((!paymentSearchDto.getControlNumber().isEmpty()) && (paymentSearchDto.getControlNumber())!=null){
            q.setParameter("bill_control_number", paymentSearchDto.getControlNumber());
        }

        if((!paymentSearchDto.getDateFrom().isEmpty()) && (paymentSearchDto.getDateFrom())!=null){
            q.setParameter("generated_date", paymentSearchDto.getDateFrom());
        }
        if((!paymentSearchDto.getDateTo().isEmpty()) && (paymentSearchDto.getDateTo())!=null){
            q.setParameter("generate_to", paymentSearchDto.getDateTo());
        }


        if((!paymentSearchDto.getRegionCode().isEmpty()) && (paymentSearchDto.getCouncilCode().isEmpty())){
            q.setParameter("region_code", paymentSearchDto.getRegionCode().split("-")[0]);
        }


        if((!paymentSearchDto.getPspReference().isEmpty()) && (paymentSearchDto.getPspReference())!=null){
            q.setParameter("psp_receipt", paymentSearchDto.getPspReference());
        }

        if((!paymentSearchDto.getGepgReference().isEmpty()) && (paymentSearchDto.getGepgReference())!=null){
            q.setParameter("gepg_receipt", paymentSearchDto.getGepgReference());
        }

        if((!paymentSearchDto.getType().isEmpty()) && (paymentSearchDto.getType())!=null){
            if(paymentSearchDto.getType().equals("XXXX")) {
            } else{
            q.setParameter("app_type", paymentSearchDto.getType());
            }
        }



        if((!paymentSearchDto.getCouncilCode().isEmpty()) && (paymentSearchDto.getCouncilCode())!=null){
            q.setParameter("region_code", paymentSearchDto.getRegionCode().split("-")[0]);
            q.setParameter("council_code", paymentSearchDto.getCouncilCode().split("-")[0]);
        }




        List<Payment> paymentList = q.getResultList();

        logger.info("############  payment List count "  + paymentList.size());
        return paymentList;

    }

    @Override
    public List<PaymentSummaryDto> searchPaymentSummary(PaymentSearchDto paymentSearchDto) {

        logger.info("#######  Query Params For Payments #######" + paymentSearchDto);

        String parameter = "";
        String sqlQuery = "";
        String joiner = " AND";

        if((!paymentSearchDto.getDateFrom().isEmpty()) && (paymentSearchDto.getDateFrom())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " p.trx_dtm >=:generated_date ";
        }

        if((!paymentSearchDto.getDateTo().isEmpty()) && (paymentSearchDto.getDateTo())!=null){
            if (!parameter.isEmpty()) {
                parameter = parameter + joiner;
            }
            parameter = parameter + " p.trx_dtm <=:generate_to ";
        }

        if(paymentSearchDto.getDateTo().isEmpty()&&
                paymentSearchDto.getDateFrom().isEmpty()){

            sqlQuery = "select sum(p.paid_amt), app_type from payment p JOIN bill b ON p.bill_id = b.bill_id  group by app_type order by  sum(p.paid_amt)  desc;";

        }else {
            parameter = " where " + parameter;
            sqlQuery = "select sum(p.paid_amt), app_type from payment p JOIN bill b ON p.bill_id = b.bill_id " + parameter  + " group by app_type  order by  sum(p.paid_amt)  desc ; ";

        }

        Query q = em.createNativeQuery(sqlQuery);

        if((!paymentSearchDto.getDateFrom().isEmpty()) && (paymentSearchDto.getDateFrom())!=null){
            q.setParameter("generated_date", paymentSearchDto.getDateFrom());
        }
        if((!paymentSearchDto.getDateTo().isEmpty()) && (paymentSearchDto.getDateTo())!=null){
            q.setParameter("generate_to", paymentSearchDto.getDateTo());
        }


        List<Object> objects = q.getResultList();

        List<PaymentSummaryDto> paymentSummaryResponseDtos = new ArrayList<PaymentSummaryDto>();

        if(objects.size()>0){
            objects.forEach(o->{

                PaymentSummaryDto paymentSummaryResponseDto = new PaymentSummaryDto();
                Object[] fields = (Object[]) o;
                BigDecimal paidAmount = (BigDecimal) fields[0];
                String gfsCode = fields[1] == null || ((String) fields[1]).equalsIgnoreCase("null") ?"OTHER BILL": (String) fields[1];
                String gfsName = gfsCode;

                paymentSummaryResponseDto.setCollectedAmount(paidAmount);
                paymentSummaryResponseDto.setGfsCode(gfsCode);
                paymentSummaryResponseDto.setGfsName(gfsName);

                paymentSummaryResponseDtos.add(paymentSummaryResponseDto);

            });
        }


        logger.info("############  payment List count "  + objects.size());
        return paymentSummaryResponseDtos;

    }


    @Override
    public PageListResponse<Payment> findAllUnreconciledTransactions(int page, int size) {
        PageListResponse<Payment> billPageListResponse = new PageListResponse<Payment>();
        Pageable paging = PageRequest.of(page, size);
        Page<Payment> payments = paymentRepository.findAllByGepgReconciledFalseOrPspReconciledFalse(paging);

        billPageListResponse.setCode(ResponseCode.SUCCESS);
        billPageListResponse.setStatus(true);
        billPageListResponse.setData(payments);
        billPageListResponse.setTotalElements(Long.valueOf(payments.getSize()));
        return billPageListResponse;
    }

}
