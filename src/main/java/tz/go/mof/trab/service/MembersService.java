package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.models.Members;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface MembersService {


    Members findById(String judgeId);

    ListResponse<Members> findAllMembers();

    Response<Members> getOneMember(String judgeId);

    Response<Members>  saveMember(JudgeDto judgeDto);

    Response<Members>  editMember(JudgeDto judgeDto, String judgeId);

    Response<Members> deleteMember(String judgeId);

}
