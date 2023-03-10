package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.FeesDto;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface FeesService {


    Fees findById(String revenueId);

    ListResponse<Fees> findAllRevue();

    Response<Fees> getOneRevenue(String feeId);

    Response<Fees>  saveRevenue(FeesDto feesDto);

    Response<Fees>  editRevenue(FeesDto feesDto, String feeId);

    Response<Fees> deleteRevenue(String feeId);

    Response<Fees> changeFeesStatus(String feeId, boolean status);

}
