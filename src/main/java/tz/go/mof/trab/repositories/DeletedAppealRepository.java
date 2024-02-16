package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.DeletedAppeals;



@RepositoryRestResource(path = "deleted-appeals",  collectionResourceRel = "deleted-appeals")
public interface DeletedAppealRepository extends JpaRepository<DeletedAppeals, Long>  {
}
