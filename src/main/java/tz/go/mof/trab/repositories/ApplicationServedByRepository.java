package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.ApplicationServedBy;


@RepositoryRestResource(collectionResourceRel = "applicationserved", path = "applicationserved")
public interface ApplicationServedByRepository extends CrudRepository<ApplicationServedBy, String>{

}
