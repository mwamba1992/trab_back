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
        ListResponse<AppealStatusTrend> responseList = new ListResponse<>();
        List<AppealStatusTrend> appealStatusTrends = appealStatusTrendRepository.findAllByOrderByAppealStatusTrendNameAsc();
        if (appealStatusTrends.isEmpty()) {
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
        Response<AppealStatusTrend> response = new Response<>();
        AppealStatusTrend trend = appealStatusTrendRepository.findById(trendId).orElse(null);
        if (trend != null) {
            response.setCode(ResponseCode.SUCCESS);
            response.setData(trend);
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
        Response<AppealStatusTrend> response = new Response<>();
        try {

                AppealStatusTrend appealStatusTrend = new AppealStatusTrend();
                TrabHelper.copyNonNullProperties(appealStatusTrendDto, appealStatusTrend);

                appealStatusTrend.setCreatedBy(loggedUser.getInfo().getName());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(appealStatusTrendRepository.save(appealStatusTrend));
                response.setDescription("SUCCESS");
                response.setStatus(true);

        } catch (Exception e) {
            logger.error("Error saving appeal status trend", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<AppealStatusTrend> editStatusTrend(AppealStatusTrendDto appealStatusTrendDto, String trendId) {
        Response<AppealStatusTrend> response = new Response<>();
        try {
            AppealStatusTrend appealStatusTrend = appealStatusTrendRepository.findById(trendId).orElse(null);
            if (appealStatusTrend != null) {
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
                response.setDescription("Error updating appeal status trend");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Appeal status trend not found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<AppealStatusTrend> deleteStatusTrend(String trendId) {
        Response<AppealStatusTrend> response = new Response<>();
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
            logger.error("Error deleting appeal status trend", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Appeal status trend could not be deleted");
            response.setStatus(false);
        }

        return response;
    }



}
