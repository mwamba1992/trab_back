package tz.go.mof.trab.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import tz.go.mof.trab.models.Summons;

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "summons", path = "summons")

public interface SummonsRepository extends PagingAndSortingRepository<Summons, Long> {
    List<Summons> findAllByOrderBySummonStartDateDesc();

    Page<Summons> findSummonsBySummonStartDateGreaterThanEqual(Date today, Pageable pageable);
}
