package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.Judge;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "judges", path = "judges")
public interface JudgeRepository extends CrudRepository<Judge, String> {

    public Judge findByName(String judgeName);
    public List<Judge> findByActiveTrue();


}
