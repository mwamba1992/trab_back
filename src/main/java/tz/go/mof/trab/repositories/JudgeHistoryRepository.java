package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.go.mof.trab.models.JudgeHistory;

@Repository
public interface JudgeHistoryRepository extends JpaRepository<JudgeHistory, Long> {

}
