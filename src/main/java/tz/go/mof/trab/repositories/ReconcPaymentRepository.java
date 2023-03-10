package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.ReconcPayment;

import javax.transaction.Transactional;
import java.util.List;


@CrossOrigin("*")
@Transactional
@RepositoryRestResource(collectionResourceRel = "reconc_payments", path = "reconc_payments")
public interface ReconcPaymentRepository extends PagingAndSortingRepository<ReconcPayment, Long> {

	@Query(value = "select * from payment py where py.pay_ref_id=:pay_ref_id AND py.psp_receipt_number=:psp_receipt_number AND py.bill_id=:bill_id", nativeQuery = true)
	public ReconcPayment selectOnePayment(@Param("pay_ref_id") String pay_ref_id , @Param("psp_receipt_number") String psp_receipt_number , @Param("bill_id") Long bill_id);

	@Query(value = "select count(*) from payment", nativeQuery = true)
	public int countAllPAyments();


	@Query(value = "select count(*) from payment pt JOIN bill bl on pt.bill_id=bl.bill_id WHERE bl.created_by=:created_by", nativeQuery = true)
	public int countAllPAymentsFromUser(@Param("created_by") Long created_by);

	@Query(value = "select * from payment pt JOIN bill bl on pt.bill_id=bl.bill_id WHERE bl.created_by=:created_by", nativeQuery = true)
	public List<ReconcPayment> selectPaymentOfUser(@Param("created_by") Long created_by);

	@Query(value = "select * from reconc_payment where  created_date LIKE %:created_date%", nativeQuery = true)
	public List<ReconcPayment> getPaymentFromBatchDay(@Param("created_date") String created_date);


}
