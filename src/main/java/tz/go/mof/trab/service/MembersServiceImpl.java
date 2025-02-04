package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.models.Members;
import tz.go.mof.trab.repositories.JudgeRepository;
import tz.go.mof.trab.repositories.MembersRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class MembersServiceImpl implements  MembersService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(MembersServiceImpl.class);


    Response<Members> response = new Response<Members>();

    ListResponse<Members> responseList = new ListResponse<Members>();

    @Autowired
    private MembersRepository membersRepository;

    @Override
    public Members findById(String gsfId) {
        return membersRepository.findById(gsfId).get();
    }

    @Override
    public ListResponse<Members> findAllMembers() {
        logger.info(loggedUser.getInfo().toString());
        List<Members> gfsList = membersRepository.findByActiveTrue();
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
    public Response<Members> getOneMember(String gsfId) {
        membersRepository.findById(gsfId).get();
        response.setCode(ResponseCode.SUCCESS);
        response.setData(membersRepository.findById(gsfId).get());
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<Members> saveMember(JudgeDto judgeDto) {
        try {
            if (membersRepository.findMembersByName(judgeDto.getName()) == null) {
                Members members = new Members();
                TrabHelper.copyNonNullProperties(judgeDto, members);

                response.setCode(ResponseCode.SUCCESS);
                members.setCreatedBy(loggedUser.getInfo().getName());
                response.setData(membersRepository.save(members));
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
    public Response<Members> editMember(JudgeDto judgeDto, String gsfId) {
        try {
            if (membersRepository.findById(gsfId).get() != null) {
                Members  members = membersRepository.findById(gsfId).get();
                TrabHelper.copyNonNullProperties(judgeDto, members);

                members.setUpdatedAt(LocalDateTime.now());
                members.setUpdatedBy(loggedUser.getInfo().getId());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(membersRepository.save(members));
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
    public Response<Members> deleteMember(String gsfId) {
        try {
            Members members = membersRepository.findById(gsfId).get();
            members.setDeleted(true);
            members.setActive(false);
            members.setDeletedBy(loggedUser.getInfo().getId());
            members.setDeletedAt(LocalDateTime.now());
            membersRepository.save(members);

            response.setData(members);
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
