package tz.go.mof.trab.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.mof.trab.models.ColumnMapper;

import java.util.List;

public interface ColumnMapperRepository extends JpaRepository<ColumnMapper,String> {
    public List<ColumnMapper> findAllByDeletedFalseAndActiveTrue();
}
