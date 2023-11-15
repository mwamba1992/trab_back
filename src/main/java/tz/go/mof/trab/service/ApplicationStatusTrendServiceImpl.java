package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.user.ApplicationStatusTrendDto;
import tz.go.mof.trab.models.ApplicationStatusTrend;
import tz.go.mof.trab.repositories.ApplicationStatusTrendRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class ApplicationStatusTrendServiceImpl implements ApplicationStatusTrendService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusTrendServiceImpl.class);

    Response<ApplicationStatusTrend> response = new Response<ApplicationStatusTrend>();
    ListResponse<ApplicationStatusTrend> responseList = new ListResponse<ApplicationStatusTrend>();

    @Autowired
    private ApplicationStatusTrendRepository applicationStatusTrendRepository;


    @Override
    public ApplicationStatusTrend findByIdIn(String currencyId) {
        return applicationStatusTrendRepository.findById(currencyId).get();
    }

    @Override
    public ListResponse<ApplicationStatusTrend> findAllStatusTrend() {
        List<ApplicationStatusTrend> appealStatusTrends = (List<ApplicationStatusTrend>) applicationStatusTrendRepository.findAll();
        if (appealStatusTrends.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(appealStatusTrends);
            responseList.setTotalElements(Long.valueOf(appealStatusTrends.size()));
        }
        return responseList;
    }

    @Override
    public Response<ApplicationStatusTrend> getOneStatusTrend(String trendId) {
        if (applicationStatusTrendRepository.findById(trendId).get() != null) {
            response.setCode(ResponseCode.SUCCESS);
            response.setData(applicationStatusTrendRepository.findById(trendId).get());
            response.setDescription("SUCCESS");
            response.setStatus(true);
        } else {
            response.setCode(ResponseCode.NO_RECORD_FOUND);
            response.setStatus(false);
            response.setData(null);
        }
        return response;
    }

    @Override
    public Response<ApplicationStatusTrend> saveStatusTrend(ApplicationStatusTrendDto appealStatusTrendDto) {
        try {

                ApplicationStatusTrend applicationStatusTrend = new ApplicationStatusTrend();
                TrabHelper.copyNonNullProperties(appealStatusTrendDto, applicationStatusTrend);

                response.setCode(ResponseCode.SUCCESS);
                applicationStatusTrend.setCreatedBy(loggedUser.getInfo().getName());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(applicationStatusTrendRepository.save(applicationStatusTrend));
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
    public Response<ApplicationStatusTrend> editStatusTrend(ApplicationStatusTrend applicationStatusTrend, String trendId) {

        try {
            if (applicationStatusTrendRepository.findById(trendId).get() != null) {
                ApplicationStatusTrend  appealStatusTrendFromDB = applicationStatusTrendRepository.findById(trendId).get();
                appealStatusTrendFromDB.setApplicationStatusTrendName(applicationStatusTrend.getApplicationStatusTrendName());
                appealStatusTrendFromDB.setApplicationStatusTrendDesc(applicationStatusTrend.getApplicationStatusTrendDesc());

                appealStatusTrendFromDB.setUpdatedAt(LocalDateTime.now());
                appealStatusTrendFromDB.setUpdatedBy(loggedUser.getInfo().getId());
                response.setData(applicationStatusTrendRepository.save(appealStatusTrendFromDB));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error! Updating Appeal Status Trends");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Appeal Status Trend! Not Found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<ApplicationStatusTrend> deleteStatusTrend(String trendId) {
        try {
            ApplicationStatusTrend applicationStatusTrend = applicationStatusTrendRepository.findById(trendId).get();
            applicationStatusTrend.setDeleted(true);
            applicationStatusTrend.setActive(false);
            applicationStatusTrend.setDeletedBy(loggedUser.getInfo().getName());
            applicationStatusTrend.setDeletedAt(LocalDateTime.now());
            applicationStatusTrendRepository.save(applicationStatusTrend);
            applicationStatusTrendRepository.delete(applicationStatusTrend);
            response.setData(applicationStatusTrend);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }



}
