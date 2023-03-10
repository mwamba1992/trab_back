package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.AppealServedBy;

@RepositoryRestResource(collectionResourceRel = "appserved", path = "appserved")
public interface AppealServedByRepository extends CrudRepository<AppealServedBy, String>{

}
