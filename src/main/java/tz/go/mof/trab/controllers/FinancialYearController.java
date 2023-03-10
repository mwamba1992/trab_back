package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.payment.FinancialYearDto;
import tz.go.mof.trab.models.FinancialYear;
import tz.go.mof.trab.service.FinancialYearService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/financial-year")
public class FinancialYearController {

    @Autowired
    private FinancialYearService financialYearService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<FinancialYear> viewAllFinancialYears(@RequestHeader HttpHeaders headers) {
        return financialYearService.findAllFinancialYears();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<FinancialYear> createFinancialYear(@Valid @RequestBody FinancialYearDto financialYearDto,
                                                       @RequestHeader HttpHeaders headers)
            throws Exception {

        return financialYearService.saveFinancialYear(financialYearDto);
    }


    @GetMapping(path = "/{financialYearId}", produces = "application/json")
    @ResponseBody
    public Response<FinancialYear> getOneFinancialYear(@PathVariable("financialYearId") String financialYearId,
                                   @RequestHeader HttpHeaders headers) {
        return financialYearService.getOneFinancialYear(financialYearId);
    }


    @PutMapping(path = "/{financialYearId}", produces = "application/json")
    @ResponseBody
    public Response<FinancialYear> editGfs(@PathVariable("financialYearId") String financialYearId,
                                           @Valid @RequestBody FinancialYearDto financialYearDto,
                                 @RequestHeader HttpHeaders headers) {
        return financialYearService.editFinancialYear(financialYearDto, financialYearId);
    }


    @DeleteMapping(path = "/{financialYearId}", produces = "application/json")
    @ResponseBody
    public Response<FinancialYear> deleteFinancialYear(@PathVariable("financialYearId") String financialYearId,
                                             @RequestHeader HttpHeaders headers)
            throws Exception {

        return financialYearService.deleteFinancialYear(financialYearId);
    }


    @RequestMapping(value = "/change-status/id/{id}/{status}", method = RequestMethod.PUT)
    @ResponseBody
    public Response<FinancialYear> changeFinancialYearStatus(@PathVariable("id") String id,
                                                            @PathVariable("status") boolean status) {
        return financialYearService.changeFinancialYearPrice(id, status);
    }

    @RequestMapping(value = "/financial-active", method = RequestMethod.GET)
    @ResponseBody
    public Response<FinancialYear> getActiveFinancialYear() {
        return financialYearService.getActiveFinalYear();
    }

}
