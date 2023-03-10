package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Appellant;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "appealants", path = "appealants")
public interface AppealantRepository  extends CrudRepository<Appellant, Long>{

    Appellant findAppealantByFirstName(String firstName);

}
