package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.SystemUser;



@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface SystemUserRepository extends CrudRepository<SystemUser, String>{
	
	public SystemUser findByUsername(String UserName);
	

}
