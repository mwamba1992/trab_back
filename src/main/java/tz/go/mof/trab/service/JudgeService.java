package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface JudgeService {


    public Judge findById(String judgeId);

    public ListResponse<Judge> findAllJudges();

    public Response<Judge> getOneJudge(String judgeId);

    public Response<Judge>  saveJudge(JudgeDto judgeDto);

    public Response<Judge>  editJudge(JudgeDto judgeDto, String judgeId);

    public Response<Judge> deleteJudge(String judgeId);

}
