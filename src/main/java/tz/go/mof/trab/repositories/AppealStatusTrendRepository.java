package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.AppealStatusTrend;
import tz.go.mof.trab.models.ApplicationStatusTrend;

import java.util.List;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "appealtrends", path = "appealtrends")
public interface AppealStatusTrendRepository extends CrudRepository<AppealStatusTrend, String> {
    AppealStatusTrend findAppealStatusTrendByAppealStatusTrendName(String name);

    List<AppealStatusTrend> findAllByOrderByAppealStatusTrendNameAsc();

    @Query("select a from AppealStatusTrend a where REPLACE(a.appealStatusTrendName, ' ', '') = REPLACE(:name, ' ', '')")
    AppealStatusTrend findAppealStatusTrendByAppealStatusTrendNameIgnoreSpaces(String name);

}
