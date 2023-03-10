package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.BankAccountDto;
import tz.go.mof.trab.models.BankAccount;
import tz.go.mof.trab.service.BankAccountService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;


@Controller
@RequestMapping("/api/accounts")
public class BankAccountController {


    @Autowired
    private BankAccountService bankAccountService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<BankAccount> viewAllAccounts(@RequestHeader HttpHeaders headers) {
        return bankAccountService.findAllBankAccounts();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<BankAccount> createBankAccount(@Valid @RequestBody BankAccountDto bankAccountDto) {

        return bankAccountService.saveBankAccount(bankAccountDto);
    }


    @GetMapping(path = "/{accId}", produces = "application/json")
    @ResponseBody
    public Response<BankAccount> getOneBankAccount(@PathVariable("accId") String accId) {
        return bankAccountService.getOneBankAccount(accId);
    }


    @PutMapping(path = "/{accId}", produces = "application/json")
    @ResponseBody
    public Response<BankAccount> editBankAccount(@PathVariable("accId") String accId,
                                           @Valid @RequestBody BankAccountDto bankAccountDto) {

        return bankAccountService.editBankAccounts(bankAccountDto, accId);
    }


    @DeleteMapping(path = "/{accId}", produces = "application/json")
    @ResponseBody
    public Response<BankAccount> deleteCurrency(@PathVariable("accId") String accId) {
        return bankAccountService.deleteBankAccount(accId);

    }


    @PutMapping(path = "/status/{status}/active/{accId}", produces = "application/json")
    @ResponseBody
    public Response<BankAccount> changeStatus(@PathVariable("accId") String accId, @PathVariable("status") boolean status) {

        return bankAccountService.changeAccountStatus(accId, status);

    }
}
