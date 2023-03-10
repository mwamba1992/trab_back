package tz.go.mof.trab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.payment.ColumnMapperDto;
import tz.go.mof.trab.models.ColumnMapper;
import tz.go.mof.trab.repositories.ColumnMapperRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ColumnMapperServiceiImpl implements ColumnMapperService {

    @Autowired
    private ColumnMapperRepository mapperRepository;


    @Override
    public ColumnMapper findMapperById(String id) {
        return mapperRepository.findById(id).get();
    }

    @Override
    public Response<ColumnMapper> saveMapping(ColumnMapperDto mapperDto) {
        Response<ColumnMapper> response = new Response<ColumnMapper>();
        try {
            ColumnMapper mapper = new ColumnMapper();
            TrabHelper.copyNonNullProperties(mapperDto, mapper);
            mapper.setCreatedBy("JOEL M MSUNGU");
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(mapperRepository.save(mapper));
            response.setDescription("SUCCESS");
        } catch (Exception e) {
            response.setStatus(true);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("SUCCESS");
        }

        return response;
    }

    @Override
    public Response<ColumnMapper> editMapping(ColumnMapperDto columnMapperDto, String id) {

        Response<ColumnMapper> response = new Response<ColumnMapper>();
        try {
            ColumnMapper mapper = findMapperById(id);
            TrabHelper.copyNonNullProperties(columnMapperDto, mapper);
            mapper.setUpdatedBy("joel m gaitan");
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(mapperRepository.save(mapper));
            response.setDescription("SUCCESS");

        } catch (Exception e) {
            response.setStatus(true);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("SUCCESS");
        }
        return response;
    }

    @Override
    public ListResponse<ColumnMapper> findAll() {
        ListResponse<ColumnMapper> response = new ListResponse<ColumnMapper>();
        try {
            List<ColumnMapper> mapperList = mapperRepository.findAllByDeletedFalseAndActiveTrue();

            if (mapperList.size() > 0) {
                response.setData(mapperList);
                response.setCode(ResponseCode.SUCCESS);
                response.setStatus(true);
                response.setTotalElements(Long.valueOf(mapperList.size()));
                response.setDescription("SUCCESS");

            } else {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setStatus(true);
                response.setDescription("NO RECORD FOUND");
            }
        } catch (Exception e) {
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(true);
            response.setDescription("NO RECORD FOUND");
        }

        return response;
    }

    @Override
    public Response<ColumnMapper> deleteMapper(String id) {
        Response<ColumnMapper> response = new Response<>();
        try {
            ColumnMapper mapper = mapperRepository.findById(id).get();
            mapper.setDeleted(true);
            mapper.setDeletedAt(LocalDateTime.now());
            mapper.setActive(false);
            mapper.setDeletedBy("JOEL M GAITAN");

            response.setData(mapperRepository.save(mapper));
            response.setCode(ResponseCode.SUCCESS);
            response.setStatus(true);
            response.setDescription("DELETED");

        } catch (Exception e) {
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(true);
            response.setDescription("FAILURE");
        }
        return response;
    }
}
