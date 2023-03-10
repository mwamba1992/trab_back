package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.payment.FinancialYearDto;
import tz.go.mof.trab.models.FinancialYear;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;


public interface FinancialYearService {


    public FinancialYear findById(String financialYearId);

    public ListResponse<FinancialYear> findAllFinancialYears();

    public Response<FinancialYear> getOneFinancialYear(String financialYearId);

    public Response<FinancialYear>  saveFinancialYear(FinancialYearDto financialYearDto);

    public Response<FinancialYear>  editFinancialYear(FinancialYearDto financialYearDto, String financialYearId);

    public Response<FinancialYear> deleteFinancialYear(String financialYearId);

    public Response<FinancialYear> changeFinancialYearPrice(String financialYearId, boolean status);

    public Response<FinancialYear> getActiveFinalYear();

}
