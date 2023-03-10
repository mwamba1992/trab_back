package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.models.Gfs;
import tz.go.mof.trab.models.Payment;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "gsfs", path = "gsfs")
public interface GfsRepository extends CrudRepository<Gfs, String> {

    Gfs findByGfsCodeAndActiveTrueAndDeletedFalse(String gfsCode);
    List<Gfs> findByActiveTrue();
    Gfs findGfsByGfsCode(String gfsCode);


    @Query(value = "select * from gfs_aud", nativeQuery = true)
    List<Gfs> getAudTable();


}
