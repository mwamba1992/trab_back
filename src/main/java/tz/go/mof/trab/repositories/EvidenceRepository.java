package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Evidence;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "evidences", path = "evidences")
public interface EvidenceRepository extends CrudRepository<Evidence, Long>{

	
}
