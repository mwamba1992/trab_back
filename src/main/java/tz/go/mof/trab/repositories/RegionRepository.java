package tz.go.mof.trab.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.mof.trab.models.Region;

import java.util.List;
import java.util.Optional;


@Transactional
public interface RegionRepository extends JpaRepository<Region, String> {


    Optional<Region> findByCode(String code);

    List<Region> findRegionsByCode(String Code);

}
