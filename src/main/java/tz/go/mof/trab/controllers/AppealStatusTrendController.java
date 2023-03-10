package tz.go.mof.trab.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.user.AppealStatusTrendDto;
import tz.go.mof.trab.models.AppealStatusTrend;
import tz.go.mof.trab.service.AppealStatusTrendService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/appeal-trends")
public class AppealStatusTrendController {


    @Autowired
    private AppealStatusTrendService appealStatusTrendService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<AppealStatusTrend> viewAllAppealStatusTrend(@RequestHeader HttpHeaders headers) {
        return appealStatusTrendService.findAllStatusTrend();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<AppealStatusTrend> createAppealStatusTrend(@Valid @RequestBody AppealStatusTrendDto appealStatusTrendDto,
                                                               @RequestHeader HttpHeaders headers)
            throws Exception {

        return appealStatusTrendService.saveStatusTrend(appealStatusTrendDto);
    }


    @GetMapping(path = "/{trendId}", produces = "application/json")
    @ResponseBody
    public Response<AppealStatusTrend> getOneAppealStatusTrend(@PathVariable("trendId") String trendId) {
        return appealStatusTrendService.getOneStatusTrend(trendId);
    }


    @PutMapping(path = "/{trendId}", produces = "application/json")
    @ResponseBody
    public Response<AppealStatusTrend> editAppealStatusTrend(@PathVariable("trendId") String trendId,
                                           @Valid @RequestBody AppealStatusTrendDto appealStatusTrendDto) {

        return appealStatusTrendService.editStatusTrend(appealStatusTrendDto, trendId);
    }


    @DeleteMapping(path = "/{trendId}", produces = "application/json")
    @ResponseBody
    public Response<AppealStatusTrend> deleteStatus(@PathVariable("trendId") String trendId)
            throws Exception {
        return appealStatusTrendService.deleteStatusTrend(trendId);

    }


}
