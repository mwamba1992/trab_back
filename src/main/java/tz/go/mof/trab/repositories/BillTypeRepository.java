package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.BillTypes;


@Transactional
@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "billtypes", path = "billtypes")
public interface BillTypeRepository  extends CrudRepository<BillTypes, Long>{
	

	@Modifying
	@Query(value = "delete from bill_types where bill_type_id =:billtype", nativeQuery = true)
	public void deleteFee(@Param("billtype") String billtype);
	
}
