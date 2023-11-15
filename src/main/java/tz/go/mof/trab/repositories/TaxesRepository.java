package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Taxes;


@Transactional
@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "taxes", path = "taxes")
public interface TaxesRepository extends PagingAndSortingRepository<Taxes, Long> {
	
	@Modifying
	@Query(value = "delete from taxes where  tax_no =:tax_no", nativeQuery = true)
	public void deleteFee(@Param("tax_no") String tax_no);


	Taxes findTaxesByTaxName(String taxName);
	
	
}

