package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.repositories.JudgeRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(JudgeServiceImpl.class);


    Response<Judge> response = new Response<Judge>();

    ListResponse<Judge> responseList = new ListResponse<Judge>();

    @Autowired
    private JudgeRepository judgeRepository;

    @Override
    public Judge findById(String gsfId) {
        return judgeRepository.findById(gsfId).get();
    }

    @Override
    public ListResponse<Judge> findAllJudges() {
        logger.info(loggedUser.getInfo().toString());
        List<Judge> gfsList = judgeRepository.findByActiveTrue();
        if (gfsList.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(gfsList);
            responseList.setTotalElements(Long.valueOf(gfsList.size()));
        }
        return responseList;
    }

    @Override
    public Response<Judge> getOneJudge(String gsfId) {
        judgeRepository.findById(gsfId).get();
        response.setCode(ResponseCode.SUCCESS);
        response.setData(judgeRepository.findById(gsfId).get());
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<Judge> saveJudge(JudgeDto judgeDto) {
        try {
            if (judgeRepository.findByName(judgeDto.getName()) == null) {
                Judge judge = new Judge();
                TrabHelper.copyNonNullProperties(judgeDto, judge);

                response.setCode(ResponseCode.SUCCESS);
                judge.setCreatedBy(loggedUser.getInfo().getName());
                response.setData(judgeRepository.save(judge));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Gfs Code Already Exists");
                response.setStatus(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<Judge> editJudge(JudgeDto judgeDto, String gsfId) {
        try {
            if (judgeRepository.findById(gsfId).get() != null) {
                Judge  judge = judgeRepository.findById(gsfId).get();
                TrabHelper.copyNonNullProperties(judgeDto, judge);

                judge.setUpdatedAt(LocalDateTime.now());
                judge.setUpdatedBy(loggedUser.getInfo().getId());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(judgeRepository.save(judge));
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error! Updating Currency");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Not Found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<Judge> deleteJudge(String gsfId) {
        try {
            Judge judge = judgeRepository.findById(gsfId).get();
            judge.setDeleted(true);
            judge.setActive(false);
            judge.setDeletedBy(loggedUser.getInfo().getId());
            judge.setDeletedAt(LocalDateTime.now());
            judgeRepository.save(judge);

            response.setData(judge);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("#########"+ e.getMessage() + "############");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }
}
