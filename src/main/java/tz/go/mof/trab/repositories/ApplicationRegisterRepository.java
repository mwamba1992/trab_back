package tz.go.mof.trab.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.models.ApplicationRegister;
import tz.go.mof.trab.models.Notice;

import java.util.Date;
import java.util.List;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "applicationregister", path = "applicationregister")
public interface ApplicationRegisterRepository  extends PagingAndSortingRepository<ApplicationRegister, Long> {
	
	ApplicationRegister findByapplicationNo(String appNo);

	ApplicationRegister findByapplicationNoAndTaxesId(String appNo,  String taxId);

	@Query(value = "SELECT nextval(application_sequence)", nativeQuery = true)
	int getSequence();


	@Query(value = "select count(*) from ApplicationRegister n 	WHERE n.billId.financialYear =:financialYear")
	int countApplicationRegisterByFinancialYear(String financialYear);


	@Query(value = "select n from ApplicationRegister n where n.billId.billId =:billId")
	ApplicationRegister findApplicationRegisterByBill(String billId);

	Page<ApplicationRegister> findApplicationRegisterByDateOfFillingBetween(Date startDate, Date endDate, Pageable pageable);

	Page<ApplicationRegister> findApplicationRegisterByDateOfFillingBetweenAndTaxes_Id(Date startDate, Date endDate,String taxId, Pageable pageable);

	Page<ApplicationRegister> findApplicationRegistersByDateOfFillingBetween(Date startDate, Date endDate, Pageable pageable);


	@Query(value = "SELECT ap FROM ApplicationRegister ap WHERE ap.applicationNo like %:region%  AND ap.dateOfFilling  between :startDate AND :endDate  AND ap.dateOfDecision is not null")
	List<ApplicationRegister> getApplicationRegisters(String region, @Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);


}
