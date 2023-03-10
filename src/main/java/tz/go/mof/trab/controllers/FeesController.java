package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.FeesDto;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.service.FeesService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;



/**
 * @author  Joel M Gaitan
 *
 **/
@Controller
@RequestMapping("/api/revenues")
public class FeesController {

    @Autowired
    private FeesService revenueService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<Fees> viewAllRevenues(@RequestHeader HttpHeaders headers) {
        return revenueService.findAllRevue();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<Fees> createRevenue(@Valid @RequestBody FeesDto feesDto, @RequestHeader HttpHeaders headers)
            throws Exception {

        return revenueService.saveRevenue(feesDto);
    }


    @GetMapping(path = "/{revenueId}", produces = "application/json")
    @ResponseBody
    public Response<Fees> getOneRevenue(@PathVariable("revenueId") String revenueId, @RequestHeader HttpHeaders headers) {
        return revenueService.getOneRevenue(revenueId);
    }


    @PutMapping(path = "/{revenueId}", produces = "application/json")
    @ResponseBody
    public Response<Fees> editRevenue(@PathVariable("revenueId") String revenueId,
                                      @Valid @RequestBody FeesDto feesDto, @RequestHeader HttpHeaders headers) {
        return revenueService.editRevenue(feesDto, revenueId);
    }
    @DeleteMapping(path = "/{revenueId}", produces = "application/json")
    @ResponseBody
    public Response<Fees> deleteRevenue(@PathVariable("revenueId") String revenueId, @RequestHeader HttpHeaders headers)
            throws Exception {

        return revenueService.deleteRevenue(revenueId);

    }

    @PutMapping(path = "/status/{status}/active/{accId}", produces = "application/json")
    @ResponseBody
    public Response<Fees> changeStatus(@PathVariable("accId") String accId, @PathVariable("status") boolean status) {

        return revenueService.changeFeesStatus(accId, status);

    }

}
