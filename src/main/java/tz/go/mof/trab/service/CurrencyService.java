package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.CurrencyDto;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface CurrencyService {

    Currency findById(String currencyId);

    ListResponse<Currency> findAllCurrency(Boolean active, Boolean deleted);

    Response<Currency> getOneCurrency(String currencyId);

    Response<Currency>  saveCurrency(CurrencyDto currencyDto);

    Response<Currency>  editCurrency(CurrencyDto currencyDto, String currencyId);

    Response<Currency> deleteCurrency(String currencyId);

    Currency findByCurrencyShortName(String shortName);

}
