package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.BankAccountDto;
import tz.go.mof.trab.models.BankAccount;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface BankAccountService {


    BankAccount findById(String gfsId);

    ListResponse<BankAccount> findAllBankAccounts();

    Response<BankAccount> getOneBankAccount(String accountId);

    Response<BankAccount>  saveBankAccount(BankAccountDto bankAccountDto);

    Response<BankAccount>  editBankAccounts(BankAccountDto bankAccountDto, String accountId);

    Response<BankAccount> deleteBankAccount(String accountId);

    Response<BankAccount> changeAccountStatus(String accountId, boolean status);

}
