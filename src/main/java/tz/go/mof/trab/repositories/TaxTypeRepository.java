package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.TaxType;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "taxtype", path = "taxtype")
public interface TaxTypeRepository extends CrudRepository<TaxType, String> {

    public List<TaxType> findByActiveTrue();


}
