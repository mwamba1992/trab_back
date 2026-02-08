package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Appellant;

import java.util.List;
import java.util.Optional;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "appealants", path = "appealants")
public interface AppealantRepository  extends CrudRepository<Appellant, Long>{

    Appellant findAppealantByFirstName(String firstName);

    Optional<Appellant> findByTinNumber(String tinNumber);

    List<Appellant> findByFirstNameContainingIgnoreCase(String name);

    Appellant findFirstByFirstNameIgnoreCase(String firstName);
}
