package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Members;
import tz.go.mof.trab.service.MembersService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/members")
public class MembersController {

    @Autowired
    private MembersService membersService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<Members> viewAllMembers() {
        return membersService.findAllMembers();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<Members> createMembers(@Valid @RequestBody JudgeDto judgeDto) {

        return membersService.saveMember(judgeDto);
    }


    @GetMapping(path = "/{judgeId}", produces = "application/json")
    @ResponseBody
    public Response<Members> getOneGfs(@PathVariable("judgeId") String judgeId) {
        return membersService.getOneMember(judgeId);
    }


    @PutMapping(path = "/{judgeId}", produces = "application/json")
    @ResponseBody
    public Response<Members> editMembers(@PathVariable("judgeId") String judgeId,
                                           @Valid @RequestBody JudgeDto judgeDto) {
        return membersService.editMember(judgeDto, judgeId);
    }


    @DeleteMapping(path = "/{judgeId}", produces = "application/json")
    @ResponseBody
    public Response<Members> deleteMembers(@PathVariable("judgeId") String judgeId) {

        return membersService.deleteMember(judgeId);

    }


}
