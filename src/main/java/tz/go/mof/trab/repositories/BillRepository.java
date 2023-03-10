package tz.go.mof.trab.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Bill;

import java.util.List;


@CrossOrigin("*")
@Transactional
@RepositoryRestResource(collectionResourceRel = "bills", path = "bills")
public interface BillRepository extends PagingAndSortingRepository<Bill, String> {

	@Modifying
	@Query(value = "update bill set bill_control_number=:bill_control_number , response_code=:response_code WHERE bill_id=:bill_id", nativeQuery = true)
	 void editBill(@Param("bill_control_number") String bill_control_number , @Param("bill_id") String bill_id, @Param("response_code") String response_code);

	@Query(value = "select * from bill bl where bl.bill_id=:bill_id", nativeQuery = true)
	Bill selectOneBill(@Param("bill_id") String bill_id);

	List<Bill> findBillByBillControlNumber(String controlNumber);

	List<Bill> findBillByResponseCodeNot(String responseCode);

}
