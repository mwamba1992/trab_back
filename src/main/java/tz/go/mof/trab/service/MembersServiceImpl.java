package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Members;
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

    @Autowired
    private MembersRepository membersRepository;

    @Override
    public Members findById(String gsfId) {
        return membersRepository.findById(gsfId).get();
    }

    @Override
    public ListResponse<Members> findAllMembers() {
        ListResponse<Members> responseList = new ListResponse<>();
        logger.info(loggedUser.getInfo().toString());
        List<Members> gfsList = membersRepository.findByActiveTrue();
        if (gfsList.isEmpty()) {
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
        Response<Members> response = new Response<>();
        response.setData(membersRepository.findById(gsfId).get());
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<Members> saveMember(JudgeDto judgeDto) {
        Response<Members> response = new Response<>();
        try {
            if (membersRepository.findMembersByName(judgeDto.getName()) == null) {
                Members members = new Members();
                TrabHelper.copyNonNullProperties(judgeDto, members);

                members.setCreatedBy(loggedUser.getInfo().getName());
                response.setData(membersRepository.save(members));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Member already exists");
                response.setStatus(false);
            }

        } catch (Exception e) {
            logger.error("Error saving member", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<Members> editMember(JudgeDto judgeDto, String gsfId) {
        Response<Members> response = new Response<>();
        try {
            Members members = membersRepository.findById(gsfId).orElse(null);
            if (members != null) {
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
                response.setDescription("Error updating member");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Member not found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<Members> deleteMember(String gsfId) {
        Response<Members> response = new Response<>();
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
            logger.error("Error deleting member", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Member could not be deleted");
            response.setStatus(false);
        }

        return response;
    }
}
