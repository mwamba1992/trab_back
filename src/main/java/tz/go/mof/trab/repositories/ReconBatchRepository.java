package tz.go.mof.trab.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tz.go.mof.trab.models.ReconBatch;




@RepositoryRestResource(collectionResourceRel = "recon_batch", path = "recon_batch")
public interface ReconBatchRepository extends CrudRepository<ReconBatch, String>{

}
