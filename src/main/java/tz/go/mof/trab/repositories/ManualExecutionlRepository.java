package tz.go.mof.trab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.go.mof.trab.models.ManualSequenceExecution;


@Repository
public interface ManualExecutionlRepository extends JpaRepository<ManualSequenceExecution, Long> {
}
