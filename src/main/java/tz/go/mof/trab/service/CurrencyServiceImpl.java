package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.CurrencyDto;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.repositories.CurrencyRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;



@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    Response<Currency> response = new Response<Currency>();

    ListResponse<Currency> responseList = new ListResponse<Currency>();

    @Autowired
    private CurrencyRepository currencyRepository;


    @Override
    public Currency findById(String currencyId) {
        return currencyRepository.findById(currencyId).get();
    }

    @Override
    public ListResponse<Currency> findAllCurrency(Boolean active, Boolean deleted) {
        List<Currency> currencyList = currencyRepository.findByActiveAndDeleted(active, deleted);
        if (currencyList.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(currencyList);
            responseList.setTotalElements(Long.valueOf(currencyList.size()));
        }
        return responseList;
    }

    @Override
    public Response<Currency> getOneCurrency(String currencyId) {
        if (currencyRepository.findById(currencyId).get() != null) {
            response.setCode(ResponseCode.SUCCESS);
            response.setData(currencyRepository.findById(currencyId).get());
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
    public Response<Currency> saveCurrency(CurrencyDto currencyDto) {
        try {
            if (currencyRepository.findByCurrencyShortNameAndActiveAndDeleted(currencyDto.getCurrencyShortName(),
                    true, false) == null) {
                Currency currency = new Currency();
                TrabHelper.copyNonNullProperties(currencyDto, currency);

                response.setCode(ResponseCode.SUCCESS);
                currency.setCreatedBy(loggedUser.getInfo().getName());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(currencyRepository.save(currency));
                response.setDescription("SUCCESS");
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Currency Already Exists");
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
    public Response<Currency> editCurrency(CurrencyDto currencyDto, String currencyId) {

        try {
            if (currencyRepository.findById(currencyId).get() != null) {
                Currency currency = currencyRepository.findById(currencyId).get();
                TrabHelper.copyNonNullProperties(currencyDto, currency);

                currency.setUpdatedAt(LocalDateTime.now());
                currency.setUpdatedBy(loggedUser.getInfo().getId());
                response.setData(currencyRepository.save(currency));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error! Updating Currency");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Not Found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<Currency> deleteCurrency(String currencyId) {
        try {
            Currency currency = currencyRepository.findById(currencyId).get();
            currency.setDeleted(true);
            currency.setActive(false);
            currency.setDeletedBy(loggedUser.getInfo().getName());
            currency.setDeletedAt(LocalDateTime.now());
            currencyRepository.save(currency);

            response.setData(currency);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Currency findByCurrencyShortName(String shortName) {
        try {
            Currency currency = currencyRepository.findByCurrencyShortName(shortName);

            if(currency !=null){
                return currency;
            }else{
                return  null;
            }
        }catch (Exception e){
           e.printStackTrace();
           return  null;
        }
    }

}
