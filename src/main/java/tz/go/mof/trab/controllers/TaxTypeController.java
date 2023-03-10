package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.TaxTypeDto;
import tz.go.mof.trab.models.TaxType;
import tz.go.mof.trab.service.TaxTypeService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/taxtypes")
public class TaxTypeController {

    @Autowired
    private TaxTypeService  taxTypeService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<TaxType> viewAllTaxTypes(@RequestHeader HttpHeaders headers) {
        return taxTypeService.findAllTaxTypes();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<TaxType> createTaxType(@Valid @RequestBody TaxTypeDto taxTypeDto, @RequestHeader HttpHeaders headers)
            throws Exception {

        return taxTypeService.saveTaxType(taxTypeDto);
    }


    @GetMapping(path = "/{taxTypeId}", produces = "application/json")
    @ResponseBody
    public Response<TaxType> getOneTaxType(@PathVariable("taxTypeId") String taxTypeId, @RequestHeader HttpHeaders headers) {
        return taxTypeService.getOneTax(taxTypeId);
    }


    @PutMapping(path = "/{taxTypeId}", produces = "application/json")
    @ResponseBody
    public Response<TaxType> editTaxType(@PathVariable("taxTypeId") String taxTypeId,
                                           @Valid @RequestBody  TaxTypeDto taxTypeDto, @RequestHeader HttpHeaders headers) {
        return taxTypeService.editTaxType(taxTypeDto, taxTypeId);
    }


    @DeleteMapping(path = "/{taxTypeId}", produces = "application/json")
    @ResponseBody
    public Response<TaxType> deleteTaxType(@PathVariable("taxTypeId") String taxTypeId, @RequestHeader HttpHeaders headers)
            throws Exception {

        return taxTypeService.deleteTaxType(taxTypeId);

    }


}
