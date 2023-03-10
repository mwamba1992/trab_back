package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.payment.FinancialYearDto;
import tz.go.mof.trab.models.FinancialYear;
import tz.go.mof.trab.repositories.FinancialYearRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class FinancialYearServiceImpl implements FinancialYearService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(FinancialYearServiceImpl.class);


    Response<FinancialYear> response = new Response<FinancialYear>();

    ListResponse<FinancialYear> responseList = new ListResponse<FinancialYear>();

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @Override
    public FinancialYear findById(String fincialYearId) {
        return financialYearRepository.findById(fincialYearId).get();
    }

    @Override
    public ListResponse<FinancialYear> findAllFinancialYears() {
        List<FinancialYear> financialYears = financialYearRepository.findByDeletedFalse();
        if (financialYears.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(financialYears);
            responseList.setTotalElements(Long.valueOf(financialYears.size()));
        }
        return responseList;
    }

    @Override
    public Response<FinancialYear> getOneFinancialYear(String gsfId) {
        response.setCode(ResponseCode.SUCCESS);
        response.setData(financialYearRepository.findById(gsfId).get());
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<FinancialYear> saveFinancialYear(FinancialYearDto financialYearDto) {
        try {
            if (financialYearRepository.findByFinancialYearAndActiveFalseAndDeletedFalse(
                    financialYearDto.getFinancialYear()) == null) {

                FinancialYear financialYear = new FinancialYear();
                TrabHelper.copyNonNullProperties(financialYearDto, financialYear);


                response.setCode(ResponseCode.SUCCESS);
                financialYear.setCreatedBy(loggedUser.getInfo().getUsername());
                financialYear.setActive(false);
                response.setData(financialYearRepository.save(financialYear));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Financial Year Already Exists");
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
    public Response<FinancialYear> editFinancialYear(FinancialYearDto financialYearDto, String financialYearId) {
        try {
            if (financialYearRepository.findById(financialYearId).get() != null) {
                FinancialYear financialYear = financialYearRepository.findById(financialYearId).get();
                TrabHelper.copyNonNullProperties(financialYearDto, financialYear);

                financialYear.setUpdatedAt(LocalDateTime.now());
                financialYear.setUpdatedBy(loggedUser.getInfo().getId());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(financialYearRepository.save(financialYear));
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
    public Response<FinancialYear> deleteFinancialYear(String gsfId) {
        try {
            FinancialYear financialYear = financialYearRepository.findById(gsfId).get();

            if(financialYear.getActive()){
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Active Financial Year! Could Not be Deleted");
                response.setStatus(false);
                return response;
            }
            financialYear.setDeleted(true);
            financialYear.setActive(false);
            financialYear.setDeletedBy(loggedUser.getInfo().getId());
            financialYear.setDeletedAt(LocalDateTime.now());
            financialYearRepository.save(financialYear);

            response.setData(financialYear);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("#########"+ e.getMessage() + "############");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Response<FinancialYear> changeFinancialYearPrice(String financialYearId, boolean status) {
        if(status && financialYearRepository.findByActiveTrue().size()>0){
            logger.error("######## Active Financial Present #######");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Active Financial Present");
            response.setStatus(false);
            return response;
        }

        FinancialYear financialYear = financialYearRepository.findById(financialYearId).get();
        if(financialYear !=null){
            logger.error("######## Update Financial Present #######");
            financialYear.setActive(status);
            financialYearRepository.save(financialYear);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(financialYear);
            response.setDescription("Active Financial Present");
            response.setStatus(false);
        }else{
            logger.error("######## Active Financial Present #######");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Active Financial Present");
            response.setStatus(false);
            return response;
        }
        return response;
    }

    @Override
    public Response<FinancialYear> getActiveFinalYear() {
        logger.info("financial list: " + financialYearRepository.findByActiveTrue().toString());
        if(financialYearRepository.findByActiveTrue().size()>0){
            logger.error("######## Active Financial Present #######");
            response.setCode(ResponseCode.SUCCESS);
            response.setData(financialYearRepository.findByActiveTrue().get(0));
            response.setDescription("Active Financial Present");
            response.setStatus(true);
            return response;
        }
        response.setDescription("Success");
        response.setStatus(false);
        response.setCode(ResponseCode.FAILURE);
        response.setData(null);
        return response;
    }
}
