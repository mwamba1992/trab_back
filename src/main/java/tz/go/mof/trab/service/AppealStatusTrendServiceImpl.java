package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.user.AppealStatusTrendDto;
import tz.go.mof.trab.models.AppealStatusTrend;
import tz.go.mof.trab.repositories.AppealStatusTrendRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class AppealStatusTrendServiceImpl implements AppealStatusTrendService {



    private static final Logger logger = LoggerFactory.getLogger(AppealStatusTrendServiceImpl.class);

    Response<AppealStatusTrend> response = new Response<>();
    ListResponse<AppealStatusTrend> responseList = new ListResponse<>();

    private AppealStatusTrendRepository appealStatusTrendRepository;

    private LoggedUser loggedUser;

    AppealStatusTrendServiceImpl(AppealStatusTrendRepository appealStatusTrendRepository, LoggedUser loggedUser){
        this.appealStatusTrendRepository = appealStatusTrendRepository;
        this.loggedUser = loggedUser;
    }


    @Override
    public AppealStatusTrend findByIdIn(String currencyId) {
        return appealStatusTrendRepository.findById(currencyId).get();
    }

    @Override
    public ListResponse<AppealStatusTrend> findAllStatusTrend() {
        List<AppealStatusTrend> appealStatusTrends = appealStatusTrendRepository.findAllByOrderByAppealStatusTrendNameAsc();
        if (appealStatusTrends.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(appealStatusTrends);
            responseList.setTotalElements((long) appealStatusTrends.size());
        }
        return responseList;
    }

    @Override
    public Response<AppealStatusTrend> getOneStatusTrend(String trendId) {
        if (appealStatusTrendRepository.findById(trendId).get() != null) {
            response.setCode(ResponseCode.SUCCESS);
            response.setData(appealStatusTrendRepository.findById(trendId).get());
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
    public Response<AppealStatusTrend> saveStatusTrend(AppealStatusTrendDto appealStatusTrendDto) {
        try {

                AppealStatusTrend appealStatusTrend = new AppealStatusTrend();
                TrabHelper.copyNonNullProperties(appealStatusTrendDto, appealStatusTrend);

                response.setCode(ResponseCode.SUCCESS);
                appealStatusTrend.setCreatedBy(loggedUser.getInfo().getName());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(appealStatusTrendRepository.save(appealStatusTrend));
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
    public Response<AppealStatusTrend> editStatusTrend(AppealStatusTrendDto appealStatusTrendDto, String trendId) {

        try {
            if (appealStatusTrendRepository.findById(trendId).get() != null) {
                AppealStatusTrend appealStatusTrend = appealStatusTrendRepository.findById(trendId).get();
                TrabHelper.copyNonNullProperties(appealStatusTrendDto, appealStatusTrend);

                appealStatusTrend.setUpdatedAt(LocalDateTime.now());
                appealStatusTrend.setUpdatedBy(loggedUser.getInfo().getId());
                response.setData(appealStatusTrendRepository.save(appealStatusTrend));
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
    public Response<AppealStatusTrend> deleteStatusTrend(String trendId) {
        try {
            AppealStatusTrend appealStatusTrend = appealStatusTrendRepository.findById(trendId).get();
            appealStatusTrend.setDeleted(true);
            appealStatusTrend.setActive(false);
            appealStatusTrend.setDeletedBy(loggedUser.getInfo().getName());
            appealStatusTrend.setDeletedAt(LocalDateTime.now());
            appealStatusTrendRepository.save(appealStatusTrend);
            appealStatusTrendRepository.delete(appealStatusTrend);
            response.setData(appealStatusTrend);
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
