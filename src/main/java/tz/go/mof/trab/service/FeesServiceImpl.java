package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.FeesDto;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.repositories.FeesRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class FeesServiceImpl implements FeesService{

    private static final Logger logger = LoggerFactory.getLogger(FeesServiceImpl.class);

    Response<Fees> response = new Response<Fees>();

    ListResponse<Fees> responseList = new ListResponse<Fees>();

    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private GfsService gfsService;

    @Autowired
    private LoggedUser loggedUser;


    @Override
    public Fees findById(String revenueId) {
        return feesRepository.findById(revenueId).get();
    }

    @Override
    public ListResponse<Fees> findAllRevue() {
        List<Fees> revenueList =  feesRepository.findByActiveTrue();
        if (revenueList.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(revenueList);
            responseList.setTotalElements(Long.valueOf(revenueList.size()));
        }
        return responseList;
    }

    @Override
    public Response<Fees> getOneRevenue(String feeId) {
        feesRepository.findById(feeId).get();
        response.setCode(ResponseCode.SUCCESS);
        response.setData(feesRepository.findById(feeId).get());
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<Fees> saveRevenue(FeesDto feesDto) {
        logger.info("########## Req ##########" + feesDto);

        try {

                Fees revenue = new Fees();
                TrabHelper.copyNonNullProperties(feesDto, revenue);

                response.setCode(ResponseCode.SUCCESS);
                revenue.setCreatedBy(loggedUser.getInfo().getName());
                revenue.setGfs(gfsService.findById(feesDto.getGfsId()));
                revenue.setAction("1");
                response.setData(feesRepository.save(revenue));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);


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
    public Response<Fees> editRevenue(FeesDto feesDto, String feeId) {
        try {
            if (feesRepository.findById(feeId).get() != null) {
                Fees fees = feesRepository.findById(feeId).get();
                TrabHelper.copyNonNullProperties(feesDto, fees);

                fees.setUpdatedAt(LocalDateTime.now());
                fees.setUpdatedBy(loggedUser.getInfo().getName());
                fees.setAction("2");
                response.setCode(ResponseCode.SUCCESS);
                response.setData(feesRepository.save(fees));
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
    public Response<Fees> deleteRevenue(String feeId) {
        try {
            Fees fees = feesRepository.findById(feeId).get();
            fees.setDeleted(true);
            fees.setActive(false);
            fees.setDeletedBy(loggedUser.getInfo().getName());
            fees.setDeletedAt(LocalDateTime.now());
            fees.setAction("3");
            feesRepository.save(fees);

            response.setData(fees);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Revenue! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Response<Fees> changeFeesStatus(String accountId, boolean status) {

        try{

            Fees fees = feesRepository.findById(accountId).get();
            fees.setActive(status);
            fees.setUpdatedAt(LocalDateTime.now());
            fees.setUpdatedBy(loggedUser.getInfo().getName());
            fees.setAction("2");
            feesRepository.save(fees);
            response.setData(fees);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);


        }catch (Exception e){
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("BankAccount! Could Not be Status Changed");
            response.setStatus(false);
        }
        return response;
    }
}
