package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mof.trab.models.AppealAmount;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Transactional
public interface AppealsAmountRepository extends JpaRepository<AppealAmount, String> {
    @Query(value = "select * from appeal_amount ap WHERE ap.appeal =:appeal", nativeQuery = true)
    public List<AppealAmount> findAppealAmountByAppealId(@Param("appeal") Long appeal );

    @Modifying
    @Query(value = "delete from appeal_amount  WHERE appeal =:appeal", nativeQuery = true)
    public void deleteAppealAmounts(@Param("appeal") Long appeal );

}
