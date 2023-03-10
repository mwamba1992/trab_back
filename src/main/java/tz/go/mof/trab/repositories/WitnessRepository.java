package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Witness;

@Transactional
@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "witnesses", path = "witnesses")
public interface WitnessRepository extends JpaRepository<Witness, Long>{

}
