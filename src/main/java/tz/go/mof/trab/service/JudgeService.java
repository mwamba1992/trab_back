package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface JudgeService {


    Judge findById(String judgeId);

    ListResponse<Judge> findAllJudges();

    Response<Judge> getOneJudge(String judgeId);

    Response<Judge>  saveJudge(JudgeDto judgeDto);

    Response<Judge>  editJudge(JudgeDto judgeDto, String judgeId);

    Response<Judge> deleteJudge(String judgeId);

}
