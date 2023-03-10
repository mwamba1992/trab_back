package tz.go.mof.trab.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.CurrencyDto;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.service.CurrencyService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import javax.validation.Valid;

/**
 * @author Joel M Gaitan
 */
@Controller
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<Currency> viewAllCurrencies(@RequestHeader HttpHeaders headers) {
        return currencyService.findAllCurrency(true, false);
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<Currency> createCurrency(@Valid @RequestBody CurrencyDto currencyDto, @RequestHeader HttpHeaders headers)
            throws Exception {

        return currencyService.saveCurrency(currencyDto);
    }


    @GetMapping(path = "/{currencyId}", produces = "application/json")
    @ResponseBody
    public Response<Currency> getOneCurrency(@PathVariable("currencyId") String currencyId, @RequestHeader HttpHeaders headers) {
        return currencyService.getOneCurrency(currencyId);
    }


    @PutMapping(path = "/{currencyId}", produces = "application/json")
    @ResponseBody
    public Response<Currency> editCurrency(@PathVariable("currencyId") String currencyId,
                                           @Valid @RequestBody CurrencyDto currencyDto, @RequestHeader HttpHeaders headers) {

        return currencyService.editCurrency(currencyDto, currencyId);
    }


    @DeleteMapping(path = "/{currencyId}", produces = "application/json")
    @ResponseBody
    public Response<Currency> deleteCurrency(@PathVariable("currencyId") String currencyId, @RequestHeader HttpHeaders headers)
            throws Exception {
        return currencyService.deleteCurrency(currencyId);

    }


}
