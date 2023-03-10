package tz.go.mof.trab.service;

import org.springframework.data.domain.Page;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.models.Summons;

import java.util.List;

public interface SummonsService {

    List<Summons> searchSummons(BillSearchDto billSearchDto);
    Page<Summons> findByPage();
}
