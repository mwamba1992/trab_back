package tz.go.mof.trab.repositories;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Appeals;



@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "appeals", path = "appeals")
@Transactional
public interface AppealsRepository extends PagingAndSortingRepository<Appeals, Long> {
	Appeals findByappealNo(String appNo);
	
	@Query(value = "SELECT * from appeals ap JOIN system_user su ON ap.created_by = su.system_user_id WHERE ap.status_trend =:trend_id AND su.token=:token", nativeQuery = true)
	List<Appeals> getAppeals(@Param("trend_id") Long trend_id , @Param("token") String token);
	
	@Query(value = "select outcome_of_decision , count(outcome_of_decision) from appeals ap  GROUP BY ap.outcome_of_decision", nativeQuery = true)
	List<Object> getCategory();


	@Query(value = "SELECT  ap.first_name , pl.amount_on_dispute_in_tzs, pl.amount_on_dispute_in_usd , pl.amount_allowed_in_tzs , pl.amount_allowed_in_usd , pl.outcome_of_decision FROM appealant ap JOIN appeals pl ON ap.appealant_id = pl.appealant_id ORDER BY pl.amount_on_dispute_in_tzs DESC LIMIT 6", nativeQuery = true)
	List<Object> getListAppeleant();
	
	@Query(value = "select tx.tax_name , SUM(ap.amount_on_dispute_in_tzs), SUM(ap.amount_on_dispute_in_usd) , SUM(ap.amount_allowed_in_tzs) , SUM(ap.amount_allowed_in_usd) FROM taxes tx JOIN appeals ap ON tx.tax_id = ap.tax_id  GROUP BY tx.tax_id" , nativeQuery = true)
	List<Object> getTaxTypeInfo();
	
	
	@Modifying
	@Query(value = "UPDATE appeals ap SET summon_id =null where ap.summon_id =:summon_id", nativeQuery = true)
	void updateAppealRemoveSummon(@Param("summon_id") Long summon_id );
	
	
	
	@Query(value = "select * from appeals ap WHERE ap.summon_id =:summon_id", nativeQuery = true)
	Appeals findBySummonId(@Param("summon_id") Long summon_id );
	
	@Query(value = "select ap from Appeals ap where ap.decidedDate is not null")
	List<Appeals> findConludedAppeal();


	@Query(value = "select * from appeals ap WHERE ap.proceding_status ='CONCLUDED' AND ap.decided_date is null", nativeQuery = true)
	List<Appeals> findDecided();

	Appeals findByAppealNo(String appealNumber);

	List<Appeals> findByIsFilledTratTrue();

	Appeals findTopByOrderByAppealIdDesc();

	@Query(value = "SELECT nextval(appeal_sequence)", nativeQuery = true)
	int getSequence();


	@Query(value = "select  a from Appeals a where a.appealNo=:appealNo AND a.tax.id =:taxType")
	Appeals findByAppealNoAndTaxType(String appealNo, String taxType);

	@Query(value = "select count(*) from Appeals a where a.billId is not null AND  convert(a.createdAt, date) BETWEEN  :startDate AND :endDate")
	int findNewAppealsOnDateRange(Date startDate, Date endDate);

	@Query(value = "select count(*) from Appeals a where a.decidedDate BETWEEN  :startDate AND :endDate")
	int findDecidedAppealsOnDateRange(Date startDate, Date endDate);

	@Query(value = "select count(*) from Appeals a where a.decidedDate is null  AND a.statusTrend.appealStatusTrendName = 'NEW' AND convert(a.createdAt, date) BETWEEN  :startDate AND :endDate")
	int findPendingAppealsOnDateRange(Date startDate, Date endDate);


	@Query(value =  "select count(*) from Appeals a where a.billId.financialYear =:financialYear")
	int getAppealsCountByFinancialYear(String financialYear);

	@Query(value = "select  ap from Appeals  ap where ap.billId.billId =:billId")
	Appeals findAppealsByBill(String billId);


	Page<Appeals> findAppealsByDateOfFillingBetweenOrderByDateOfFillingAsc(Date startDate, Date endDate, Pageable pageable);

	Page<Appeals> findAppealsByDateOfFillingBetweenAndTax_IdOrderByDateOfFillingAsc(Date startDate, Date endDate,String taxId, Pageable pageable);

	@Query(value = "select count(*) from Appeals ap where  ap.decidedDate is null")
	int findPendingForJudgement();


	@Query(value = "select ap from Appeals ap where  ap.decidedDate is null")
	List<Appeals> findPendingForJudgementWithNoSummons();

	@Query(value = "select ap from Appeals ap where  ap.decidedDate is null and ap.summons is not null")
	List<Appeals> findPendingForJudgementWithSummons();

	@Query(value = "SELECT ap FROM Appeals ap WHERE ap.procedingStatus = 'CONCLUDED' AND ap.dateOfTheLastOrder > CURDATE()")
	List<Appeals> findPendingForJudgementWithSummonsInTime();


	@Query(value = "SELECT ap FROM Appeals ap WHERE ap.procedingStatus = 'CONCLUDED' AND ap.dateOfTheLastOrder < CURDATE()")
	List<Appeals>  findPendingForJudgementWithSummonsOutOfTime();


	@Query(value = "select a from Appeals a where a.decidedDate is null")
	List<Appeals> findAllPendingAppeals();


	@Query(value = "select a from Appeals  a where a.createdBy = 'System Created'")
	Page<Appeals> findAllSystemCreatedAppeals(Pageable pageable);

	List<Appeals> findAppealsByAppealNoAndTax_Id(String appealNo, String taxId);

	Appeals findAppealsByAppealNoAndTax_TaxName(String appealNo, String taxName);
	List<Appeals> findAppealsByDecidedDateNullOrderByDateOfFillingAsc();
	@Query("SELECT a FROM Appeals a WHERE a.procedingStatus = 'CONCLUDED'")
	List <Appeals> findAppealsByProcedingStatus();


	List<Appeals> findAppealsByInitiatedForDeleteTrue();

	@Query(value = "SELECT ap FROM Appeals ap WHERE ap.statusTrend.id= 'c140eb70a2de11ed96425f92e50c79ff' AND ap.decidedDate is null AND ap.summons is null ")
	List<Appeals> findPendingAssigmentAppeals();

	@Query(value = "SELECT ap FROM Appeals ap WHERE  ap.procedingStatus is null AND ap.decidedDate is null AND ap.summons is not null ")
	List<Appeals> findHearingAppeals();


	@Query(value = "SELECT ap FROM Appeals ap WHERE ap.appealNo like %:region%  AND ap.dateOfFilling  between :startDate AND :endDate  AND ap.decidedDate is not null")
	List<Appeals> getAppealsForTrat(String region,   @Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);


}

