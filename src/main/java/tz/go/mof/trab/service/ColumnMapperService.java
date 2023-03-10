package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.payment.ColumnMapperDto;
import tz.go.mof.trab.models.ColumnMapper;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface ColumnMapperService {

    Response<ColumnMapper> saveMapping(ColumnMapperDto mapperDto);
    ColumnMapper findMapperById(String id);
    Response<ColumnMapper>  editMapping(ColumnMapperDto columnMapperDto, String id);
    ListResponse<ColumnMapper> findAll();
    Response<ColumnMapper> deleteMapper(String id);


}
