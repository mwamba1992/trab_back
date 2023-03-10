package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.FinancialYear;
import java.util.List;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "financialyear", path = "financialyear")
public interface FinancialYearRepository extends CrudRepository<FinancialYear, String> {
    FinancialYear findByFinancialYearAndActiveFalseAndDeletedFalse(String FinancialYear);
    List<FinancialYear> findByDeletedFalse();
    List<FinancialYear> findByActiveTrue();
}
