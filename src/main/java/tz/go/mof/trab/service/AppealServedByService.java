package tz.go.mof.trab.service;



import tz.go.mof.trab.dto.report.AppealServedByDto;
import tz.go.mof.trab.models.AppealServedBy;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface AppealServedByService {


    public AppealServedBy findById(String servedById);

    public ListResponse<AppealServedBy> findAllAppealServedBy();

    public Response<AppealServedBy> getOneAppealServedBy(String servedById);

    public Response<AppealServedBy> saveAppealServedBy(AppealServedByDto appealServedByDto);

    public Response<AppealServedBy>  editAppealServedBy(AppealServedByDto appealServedByDto, String servedById);

    public Response<AppealServedBy> deleteAppealServedBy(String servedById);

}
