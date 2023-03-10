package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.OtherBills;

@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "otherbills", path = "otherbills")
public interface OtherBillsRepository extends PagingAndSortingRepository<OtherBills, Long> {
	

}
