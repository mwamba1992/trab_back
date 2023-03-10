package tz.go.mof.trab.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.SummonsAppeal;

@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "summonsappeals", path = "summonsappeals")
public interface SummonsAppealsRepository extends CrudRepository<SummonsAppeal, Long> {
  
	@Query(value = "SELECT * from summons_appeals ap where ap.summon_id=:summon_id ", nativeQuery = true)
	public List<SummonsAppeal> getAppealFromSummons(@Param("summon_id") Long summon_id);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE from summons_appeals  where summon_id =:summon_id", nativeQuery = true)
	public void deleteSummonsAppeals(@Param("summon_id") Long summon_id );
	
	
	
}
