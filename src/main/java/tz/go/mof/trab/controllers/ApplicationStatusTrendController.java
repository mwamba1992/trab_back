package tz.go.mof.trab.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.user.ApplicationStatusTrendDto;
import tz.go.mof.trab.models.ApplicationStatusTrend;
import tz.go.mof.trab.service.ApplicationStatusTrendService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/application-trends")
public class ApplicationStatusTrendController {


    @Autowired
    private ApplicationStatusTrendService applicationStatusTrendService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<ApplicationStatusTrend> viewAllAppealStatusTrend(@RequestHeader HttpHeaders headers) {
        return applicationStatusTrendService.findAllStatusTrend();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<ApplicationStatusTrend> createAppealStatusTrend(@Valid @RequestBody ApplicationStatusTrendDto applicationStatusTrendDto,
                                                               @RequestHeader HttpHeaders headers)
            throws Exception {

        return applicationStatusTrendService.saveStatusTrend(applicationStatusTrendDto);
    }


    @GetMapping(path = "/{trendId}", produces = "application/json")
    @ResponseBody
    public Response<ApplicationStatusTrend> getOneAppealStatusTrend(@PathVariable("trendId") String trendId) {
        return applicationStatusTrendService.getOneStatusTrend(trendId);
    }


    @PutMapping(path = "/{trendId}", produces = "application/json")
    @ResponseBody
    public Response<ApplicationStatusTrend> editAppealStatusTrend(@PathVariable("trendId") String trendId,
                                           @Valid @RequestBody ApplicationStatusTrend applicationStatusTrend) {

        return applicationStatusTrendService.editStatusTrend(applicationStatusTrend, trendId);
    }


    @DeleteMapping(path = "/{trendId}", produces = "application/json")
    @ResponseBody
    public Response<ApplicationStatusTrend> deleteStatus(@PathVariable("trendId") String trendId)
            throws Exception {
        return applicationStatusTrendService.deleteStatusTrend(trendId);

    }


}
