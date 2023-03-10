package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.Fees;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "fees", path = "fees")
public interface FeesRepository  extends CrudRepository<Fees, String>{

    public List<Fees> findByActiveTrue();

	
}
