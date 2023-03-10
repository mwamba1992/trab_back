package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.ApplicationStatusTrend;

import java.util.List;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "applicationtrends", path = "applicationtrends")
public interface ApplicationStatusTrendRepository extends CrudRepository<ApplicationStatusTrend, String> {

	ApplicationStatusTrend  findApplicationStatusTrendByApplicationStatusTrendName(String name);


	@Query("select a from ApplicationStatusTrend a where REPLACE(a.applicationStatusTrendName, ' ', '') = REPLACE(:name, ' ', '')")
	ApplicationStatusTrend findApplicationStatusTrendByApplicationStatusTrendNameIgnoreSpaces(String name);

	List<ApplicationStatusTrend> findByApplicationStatusTrendNameContainingIgnoreCase(String statusTrendName);
}
