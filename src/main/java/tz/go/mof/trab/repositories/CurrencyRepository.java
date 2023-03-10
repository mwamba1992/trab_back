package tz.go.mof.trab.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.Currency;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "currencies", path = "currencies")
public interface CurrencyRepository extends CrudRepository<Currency, String>{
    public Currency findByCurrencyShortNameAndActiveAndDeleted(String currencyShortCode, Boolean active, Boolean deleted);
    public List<Currency> findByActiveAndDeleted(Boolean active, Boolean deleted);
    public Currency findByCurrencyShortName(String shortName);
}
