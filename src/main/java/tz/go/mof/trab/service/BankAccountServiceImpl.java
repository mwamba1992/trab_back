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

    Response<BankAccount> response = new Response<BankAccount>();

    ListResponse<BankAccount> responseList = new ListResponse<BankAccount>();

    @Override
    public BankAccount findById(String accountId) {
        return bankAccountRepository.findById(accountId).get();
    }

    @Override
    public ListResponse<BankAccount> findAllBankAccounts() {
        List<BankAccount> bankAccounts = (List<BankAccount>) bankAccountRepository.findAll();
        if (bankAccounts.size() < 1) {
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
        if (bankAccountRepository.findById(accountId).get() != null) {
            response.setCode(ResponseCode.SUCCESS);
            response.setData(bankAccountRepository.findById(accountId).get());
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
        try {
            if (bankAccountRepository.findByAccountNumber(bankAccountDto.getAccountNumber()) == null) {
                BankAccount bankAccount = new BankAccount();
                TrabHelper.copyNonNullProperties(bankAccountDto, bankAccount);

                response.setCode(ResponseCode.SUCCESS);
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
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<BankAccount> editBankAccounts(BankAccountDto bankAccountDto, String accId) {
        try {
            if (bankAccountRepository.findById(accId).get() != null) {
                BankAccount bankAccount = bankAccountRepository.findById(accId).get();
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
                response.setDescription("Error! Updating BankAccount");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("BankAccount! Not Found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<BankAccount> deleteBankAccount(String accId) {
        try {
            BankAccount bankAccount = bankAccountRepository.findById(accId).get();
            bankAccountRepository.delete(bankAccount);
            response.setData(bankAccount);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("BankAccount! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Response<BankAccount> changeAccountStatus(String accountId, boolean status) {

        try{

            BankAccount bankAccount = bankAccountRepository.findById(accountId).get();
            bankAccount.setActive(status);
            bankAccountRepository.save(bankAccount);
            response.setData(bankAccount);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);


        }catch (Exception e){
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("BankAccount! Could Not be Status Changed");
            response.setStatus(false);
        }
        return response;
    }

}
