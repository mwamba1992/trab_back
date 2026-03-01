package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.TaxTypeDto;
import tz.go.mof.trab.models.TaxType;
import tz.go.mof.trab.repositories.TaxTypeRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class TaxTypeServiceImpl implements TaxTypeService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(TaxTypeServiceImpl.class);

    @Autowired
    private TaxTypeRepository taxTypeRepository;

    @Override
    public TaxType findById(String gsfId) {
        return taxTypeRepository.findById(gsfId).get();
    }

    @Override
    public ListResponse<TaxType> findAllTaxTypes() {
        ListResponse<TaxType> responseList = new ListResponse<>();
        List<TaxType> taxTypes = taxTypeRepository.findByActiveTrue();
        if (taxTypes.isEmpty()) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(taxTypes);
            responseList.setTotalElements(Long.valueOf(taxTypes.size()));
        }
        return responseList;
    }

    @Override
    public Response<TaxType> getOneTax(String gsfId) {
        Response<TaxType> response = new Response<>();
        response.setData(taxTypeRepository.findById(gsfId).get());
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<TaxType> saveTaxType(TaxTypeDto taxTypeDto) {
        Response<TaxType> response = new Response<>();
        try {
                TaxType taxType = new TaxType();
                TrabHelper.copyNonNullProperties(taxTypeDto, taxType);

                taxType.setCreatedBy(loggedUser.getInfo().getName());
                response.setData(taxTypeRepository.save(taxType));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);


        } catch (Exception e) {
            logger.error("Error saving tax type", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<TaxType> editTaxType(TaxTypeDto taxTypeDto, String gsfId) {
        Response<TaxType> response = new Response<>();
        try {
            TaxType taxType = taxTypeRepository.findById(gsfId).orElse(null);
            if (taxType != null) {
                TrabHelper.copyNonNullProperties(taxTypeDto, taxType);

                taxType.setUpdatedAt(LocalDateTime.now());
                taxType.setUpdatedBy(loggedUser.getInfo().getId());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(taxTypeRepository.save(taxType));
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error updating tax type");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Tax type not found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<TaxType> deleteTaxType(String gsfId) {
        Response<TaxType> response = new Response<>();
        try {
            TaxType taxType =  taxTypeRepository.findById(gsfId).get();
            taxType.setDeleted(true);
            taxType.setActive(false);
            taxType.setDeletedBy(loggedUser.getInfo().getId());
            taxType.setDeletedAt(LocalDateTime.now());
            taxTypeRepository.save(taxType);

            response.setData(taxType);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("Error deleting tax type", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Tax type could not be deleted");
            response.setStatus(false);
        }

        return response;
    }
}
