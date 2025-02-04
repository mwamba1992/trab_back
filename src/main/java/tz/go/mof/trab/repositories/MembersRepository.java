package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.models.Members;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "members", path = "members")
public interface MembersRepository extends CrudRepository<Members, String> {

    Members findMembersByName(String memberName);
    List<Members> findByActiveTrue();


}
