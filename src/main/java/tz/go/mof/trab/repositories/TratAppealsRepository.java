package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.models.TratAppeals;

import javax.transaction.Transactional;
import java.util.List;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "trat_appeals", path = "trat_appeals")
@Transactional
public interface TratAppealsRepository extends CrudRepository<TratAppeals, Long> {
	public TratAppeals findByappealNo(String appNo);
	
	@Query(value = "SELECT * from appeals ap JOIN system_user su ON ap.created_by = su.system_user_id WHERE ap.status_trend =:trend_id AND su.token=:token", nativeQuery = true)
	public List<Appeals> getAppeals(@Param("trend_id") Long trend_id , @Param("token") String token);
	
	@Query(value = "select outcome_of_decision , count(outcome_of_decision) from appeals ap  GROUP BY ap.outcome_of_decision", nativeQuery = true)
	public List<Object> getCategory();


	@Query(value = "SELECT  ap.first_name , pl.amount_on_dispute_in_tzs, pl.amount_on_dispute_in_usd , pl.amount_allowed_in_tzs , pl.amount_allowed_in_usd , pl.outcome_of_decision FROM appealant ap JOIN appeals pl ON ap.appealant_id = pl.appealant_id ORDER BY pl.amount_on_dispute_in_tzs DESC LIMIT 6", nativeQuery = true)
	public List<Object> getListAppeleant();
	
	@Query(value = "select tx.tax_name , SUM(ap.amount_on_dispute_in_tzs), SUM(ap.amount_on_dispute_in_usd) , SUM(ap.amount_allowed_in_tzs) , SUM(ap.amount_allowed_in_usd) FROM taxes tx JOIN appeals ap ON tx.tax_id = ap.tax_id  GROUP BY tx.tax_id" , nativeQuery = true)
	public List<Object> getTaxTypeInfo();
	
	
	@Modifying
	@Query(value = "UPDATE appeals ap SET summon_id =null where ap.summon_id =:summon_id", nativeQuery = true)
	public void updateAppealRemoveSummon(@Param("summon_id") Long summon_id );
	
	
	
	@Query(value = "select * from appeals ap WHERE ap.summon_id =:summon_id", nativeQuery = true)
	public Appeals findBySummonId(@Param("summon_id") Long summon_id );
	
	@Query(value = "select * from appeals ap WHERE ap.proceding_status ='CONCLUDED'", nativeQuery = true)
	public List<Appeals> findCocludedAppeals();
	
	
	
	
	}

