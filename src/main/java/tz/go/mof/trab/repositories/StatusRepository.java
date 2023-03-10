package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Status;



@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "status", path = "status")
public interface StatusRepository extends CrudRepository<Status, Long>{

	
}
