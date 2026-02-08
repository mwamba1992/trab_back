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
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.models.JudgeHistory;
import tz.go.mof.trab.models.Summons;
import tz.go.mof.trab.repositories.JudgeHistoryRepository;
import tz.go.mof.trab.repositories.JudgeRepository;
import tz.go.mof.trab.repositories.SummonsRepository;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class SummonsServiceImpl implements  SummonsService{

    private static final Logger logger = LoggerFactory.getLogger(SummonsServiceImpl.class);

    @Autowired
    private SummonsRepository summonsRepository;

    @Autowired
    private JudgeHistoryRepository judgeHistoryRepository;

    @Autowired
    private JudgeRepository judgeRepository;

    @Override
    public List<Summons> searchSummons(BillSearchDto billSearchDto) throws java.text.ParseException {
        logger.info("startDate: " + billSearchDto.getDateFrom() + " dateTo: " +billSearchDto.getDateTo());

        java.text.SimpleDateFormat dFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return summonsRepository.findSummonsBySummonStartDateBetween(
                dFormat.parse(billSearchDto.getDateFrom()), dFormat.parse(billSearchDto.getDateTo()));
    }

    @Override
    public Page<Summons> findByPage() {
//        Page<Summons> page = summonsRepository.findAll(
//                PageRequest.of(0, 7, Sort.by(Sort.Direction.ASC, "summonId")));
        Pageable pageable = PageRequest.of(0, 7, Sort.by(Sort.Direction.ASC, "summonId"));
        Page<Summons> page = summonsRepository.findSummonsBySummonStartDateGreaterThanEqual(new Date(), pageable);
        return page;
    }

    @Override
    public Response changeJudge(Long summonId, String judgeId) {
        // Retrieve the Summons instance safely
        Summons summons = summonsRepository.findById(summonId)
                .orElseThrow(() -> new EntityNotFoundException("Summons not found"));

        // Retrieve the Judge instance safely
        Judge judge = judgeRepository.findById(judgeId)
                .orElseThrow(() -> new EntityNotFoundException("Judge not found"));

        // Create a new JudgeHistory entry
        JudgeHistory judgeHistory = new JudgeHistory();
        judgeHistory.setSummons(summons);
        judgeHistory.setJudge(summons.getJud());
        judgeHistory.setChangeDate(new Date());




        // Save the JudgeHistory
        judgeHistoryRepository.save(judgeHistory);

        // Append the new history to the existing list
        summons.getJudgeHistory().add(judgeHistory);


        // Update the current judge
        summons.setJud(judge);
        summons.setJudge(judge.getName());
        summonsRepository.save(summons);

        return new Response(true, ResponseCode.SUCCESS, "Success", null);
    }

}
