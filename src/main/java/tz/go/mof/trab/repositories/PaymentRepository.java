package tz.go.mof.trab.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.Payment;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@RepositoryRestResource(collectionResourceRel = "payments", path = "payments")
public interface PaymentRepository extends PagingAndSortingRepository<Payment, String> {


   Payment findByPayRefIdAndPspReceiptNumber(String payrefid , String pspreceiptnumber);

   @Query(value = "select * from trr_payment py where py.pay_ref_id=:pay_ref_id AND py.psp_receipt_number=:psp_receipt_number AND py.bill_id=:bill_id", nativeQuery = true)
   public Payment selectOnePayment(@Param("pay_ref_id") String pay_ref_id , @Param("psp_receipt_number") String psp_receipt_number , @Param("bill_id") String bill_id);

   Page<Payment> findAll(Pageable pageable);

   Payment findByPayCtrNumAndPspReceiptNumberAndPaidAmt(String controlNumber, String pspReceipt, BigDecimal paidAmount);

   Page<Payment> findAllByGepgReconciledFalseOrPspReconciledFalse(Pageable pageable);


   @Query(value = "select pr from Payment pr where date(pr.trxDtm)=:date")
   List<Payment> findPaymentByTrxDtm(@Param("date") Date date);

   @Query("SELECT p FROM Payment p JOIN FETCH p.bill WHERE p.trxDtm BETWEEN :startDate AND :endDate ORDER BY p.bill.appType ASC, p.trxDtm DESC")
   List<Payment> findPaymentsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
