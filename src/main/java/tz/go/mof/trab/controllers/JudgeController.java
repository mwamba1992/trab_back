package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.user.JudgeDto;
import tz.go.mof.trab.models.Judge;
import tz.go.mof.trab.service.JudgeService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/judge")
public class JudgeController {

    @Autowired
    private JudgeService judgeService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<Judge> viewAllJudges(@RequestHeader HttpHeaders headers) {
        return judgeService.findAllJudges();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<Judge> createJudge(@Valid @RequestBody JudgeDto judgeDto, @RequestHeader HttpHeaders headers)
            throws Exception {

        return judgeService.saveJudge(judgeDto);
    }


    @GetMapping(path = "/{judgeId}", produces = "application/json")
    @ResponseBody
    public Response<Judge> getOneGfs(@PathVariable("judgeId") String judgeId, @RequestHeader HttpHeaders headers) {
        return judgeService.getOneJudge(judgeId);
    }


    @PutMapping(path = "/{judgeId}", produces = "application/json")
    @ResponseBody
    public Response<Judge> editGfs(@PathVariable("judgeId") String judgeId,
                                           @Valid @RequestBody JudgeDto judgeDto, @RequestHeader HttpHeaders headers) {
        return judgeService.editJudge(judgeDto, judgeId);
    }


    @DeleteMapping(path = "/{judgeId}", produces = "application/json")
    @ResponseBody
    public Response<Judge> deleteGfs(@PathVariable("judgeId") String judgeId, @RequestHeader HttpHeaders headers)
            throws Exception {

        return judgeService.deleteJudge(judgeId);

    }


}
