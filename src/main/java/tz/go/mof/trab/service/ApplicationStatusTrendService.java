package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.user.ApplicationStatusTrendDto;
import tz.go.mof.trab.models.ApplicationStatusTrend;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface ApplicationStatusTrendService {

    public ApplicationStatusTrend findByIdIn(String trendId);

    public ListResponse<ApplicationStatusTrend> findAllStatusTrend();

    public Response<ApplicationStatusTrend> getOneStatusTrend(String trendId);

    public Response<ApplicationStatusTrend>  saveStatusTrend(ApplicationStatusTrendDto applicationStatusTrendDto);

    public Response<ApplicationStatusTrend>  editStatusTrend(ApplicationStatusTrend applicationStatusTrend, String trendId);

    public Response<ApplicationStatusTrend> deleteStatusTrend(String trendId);


}
