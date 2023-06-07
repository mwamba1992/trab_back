package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Adress;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "adresses", path = "adresses")
public interface AdressRepository extends CrudRepository<Adress, Long>{

    @Query(value = "SELECT MAX(adress_id) FROM adress", nativeQuery = true)
    long findLastUsedId();
}
