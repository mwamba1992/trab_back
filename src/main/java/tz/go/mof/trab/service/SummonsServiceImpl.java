package tz.go.mof.trab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.models.Summons;
import tz.go.mof.trab.repositories.SummonsRepository;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class SummonsServiceImpl implements  SummonsService{

    private static final Logger logger = LoggerFactory.getLogger(SummonsServiceImpl.class);

    @Autowired
    private SummonsRepository summonsRepository;

    @Override
    public List<Summons> searchSummons(BillSearchDto billSearchDto) {
        logger.info("startDate: " + billSearchDto.getDateFrom() + " dateTo: " +billSearchDto.getDateTo());

        return (List<Summons>) summonsRepository.findAllByOrderBySummonStartDateDesc();
    }

    @Override
    public Page<Summons> findByPage() {
//        Page<Summons> page = summonsRepository.findAll(
//                PageRequest.of(0, 7, Sort.by(Sort.Direction.ASC, "summonId")));
        Pageable pageable = PageRequest.of(0, 7, Sort.by(Sort.Direction.ASC, "summonId"));
        Page<Summons> page = summonsRepository.findSummonsBySummonStartDateGreaterThanEqual(new Date(), pageable);
        return page;
    }
}
