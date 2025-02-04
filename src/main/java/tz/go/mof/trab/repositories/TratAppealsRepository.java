package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.TratAppeal;


@RepositoryRestResource(collectionResourceRel = "trat_appeals", path = "trat_appeals")
public interface TratAppealsRepository extends CrudRepository<TratAppeal, String> {


}
