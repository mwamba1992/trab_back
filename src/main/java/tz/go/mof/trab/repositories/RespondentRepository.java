package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Respondent;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "respondent", path = "respondent")
public interface RespondentRepository extends CrudRepository<Respondent, Long>{

    Respondent findRespondentByName(String name);


}
