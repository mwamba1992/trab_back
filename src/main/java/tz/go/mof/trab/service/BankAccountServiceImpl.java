package tz.go.mof.trab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.BankAccountDto;
import tz.go.mof.trab.models.BankAccount;
import tz.go.mof.trab.repositories.BankAccountRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class BankAccountServiceImpl implements BankAccountService{

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private CurrencyService currencyService;

    private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    @Override
    public BankAccount findById(String accountId) {
        return bankAccountRepository.findById(accountId).get();
    }

    @Override
    public ListResponse<BankAccount> findAllBankAccounts() {
        ListResponse<BankAccount> responseList = new ListResponse<>();
        List<BankAccount> bankAccounts = (List<BankAccount>) bankAccountRepository.findAll();
        if (bankAccounts.isEmpty()) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(bankAccounts);
            responseList.setTotalElements(Long.valueOf(bankAccounts.size()));
        }
        return responseList;
    }

    @Override
    public Response<BankAccount> getOneBankAccount(String accountId) {
        Response<BankAccount> response = new Response<>();
        BankAccount account = bankAccountRepository.findById(accountId).orElse(null);
        if (account != null) {
            response.setCode(ResponseCode.SUCCESS);
            response.setData(account);
            response.setDescription("SUCCESS");
            response.setStatus(true);
        } else {
            response.setCode(ResponseCode.NO_RECORD_FOUND);
            response.setStatus(false);
            response.setData(null);
        }
        return response;
    }

    @Override
    public Response<BankAccount> saveBankAccount(BankAccountDto bankAccountDto) {
        Response<BankAccount> response = new Response<>();
        try {
            if (bankAccountRepository.findByAccountNumber(bankAccountDto.getAccountNumber()) == null) {
                BankAccount bankAccount = new BankAccount();
                TrabHelper.copyNonNullProperties(bankAccountDto, bankAccount);

                bankAccount.setCreatedBy(loggedUser.getInfo().getId());
                bankAccount.setCurrency(currencyService.findByCurrencyShortName(bankAccountDto.getCcyId()));
                response.setCode(ResponseCode.SUCCESS);
                response.setData(bankAccountRepository.save(bankAccount));
                response.setDescription("SUCCESS");
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Account Already Exists");
                response.setStatus(false);
            }

        } catch (Exception e) {
            logger.error("Error saving bank account", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<BankAccount> editBankAccounts(BankAccountDto bankAccountDto, String accId) {
        Response<BankAccount> response = new Response<>();
        try {
            BankAccount bankAccount = bankAccountRepository.findById(accId).orElse(null);
            if (bankAccount != null) {
                TrabHelper.copyNonNullProperties(bankAccountDto, bankAccount);

                bankAccount.setUpdatedAt(LocalDateTime.now());
                bankAccount.setCurrency(currencyService.findByCurrencyShortName(bankAccountDto.getCcyId()));
                bankAccount.setUpdatedBy(loggedUser.getInfo().getId());
                response.setData(bankAccountRepository.save(bankAccount));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error updating bank account");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Bank account not found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<BankAccount> deleteBankAccount(String accId) {
        Response<BankAccount> response = new Response<>();
        try {
            BankAccount bankAccount = bankAccountRepository.findById(accId).get();
            bankAccountRepository.delete(bankAccount);
            response.setData(bankAccount);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("Error deleting bank account", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Bank account could not be deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Response<BankAccount> changeAccountStatus(String accountId, boolean status) {
        Response<BankAccount> response = new Response<>();
        try{

            BankAccount bankAccount = bankAccountRepository.findById(accountId).get();
            bankAccount.setActive(status);
            bankAccountRepository.save(bankAccount);
            response.setData(bankAccount);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);


        }catch (Exception e){
            logger.error("Error changing bank account status", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Bank account status could not be changed");
            response.setStatus(false);
        }
        return response;
    }

}
