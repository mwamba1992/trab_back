package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.user.AppealStatusTrendDto;
import tz.go.mof.trab.models.AppealStatusTrend;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface AppealStatusTrendService {

    AppealStatusTrend findByIdIn(String trendId);

    ListResponse<AppealStatusTrend> findAllStatusTrend();

    Response<AppealStatusTrend> getOneStatusTrend(String trendId);

    Response<AppealStatusTrend>  saveStatusTrend(AppealStatusTrendDto appealStatusTrendDto);

    Response<AppealStatusTrend>  editStatusTrend(AppealStatusTrendDto appealStatusTrendDto, String trendId);

    Response<AppealStatusTrend> deleteStatusTrend(String trendId);



}
