package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.report.AppealServedByDto;
import tz.go.mof.trab.models.AppealServedBy;
import tz.go.mof.trab.repositories.AppealServedByRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class AppealServedByServiceImpl implements AppealServedByService{

    private static final Logger logger = LoggerFactory.getLogger(AppealServedByServiceImpl.class);

    @Autowired
    private AppealServedByRepository appealServedByRepository;

    @Autowired
    private GfsService gfsService;

    @Autowired
    private LoggedUser loggedUser;


    @Override
    public AppealServedBy findById(String revenueId) {
        return appealServedByRepository.findById(revenueId).get();
    }

    @Override
    public ListResponse<AppealServedBy> findAllAppealServedBy() {
        ListResponse<AppealServedBy> responseList = new ListResponse<>();
        List<AppealServedBy> appealServedByList = (List<AppealServedBy>) appealServedByRepository.findAll();
        if (appealServedByList.isEmpty()) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(appealServedByList);
            responseList.setTotalElements(Long.valueOf(appealServedByList.size()));
        }
        return responseList;
    }

    @Override
    public Response<AppealServedBy> getOneAppealServedBy(String servedById) {
        Response<AppealServedBy> response = new Response<>();
        response.setData(appealServedByRepository.findById(servedById).get());
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<AppealServedBy> saveAppealServedBy(AppealServedByDto appealServedByDto) {
        Response<AppealServedBy> response = new Response<>();
        logger.debug("Saving appeal served by: {}", appealServedByDto);

        try {

                AppealServedBy appealServedBy = new AppealServedBy();
                TrabHelper.copyNonNullProperties(appealServedByDto, appealServedBy);

                appealServedBy.setCreatedBy(loggedUser.getInfo().getId());
                response.setData(appealServedByRepository.save(appealServedBy));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);


        } catch (Exception e) {
            logger.error("Error saving appeal served by", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<AppealServedBy> editAppealServedBy(AppealServedByDto appealServedByDto, String servedBy) {
        Response<AppealServedBy> response = new Response<>();
        try {
            AppealServedBy appealServedBy = appealServedByRepository.findById(servedBy).orElse(null);
            if (appealServedBy != null) {
                TrabHelper.copyNonNullProperties(appealServedByDto, appealServedBy);

                appealServedBy.setUpdatedAt(LocalDateTime.now());
                appealServedBy.setUpdatedBy(loggedUser.getInfo().getId());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(appealServedByRepository.save(appealServedBy));
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error updating appeal served by");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Appeal served by not found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<AppealServedBy> deleteAppealServedBy(String feeId) {
        Response<AppealServedBy> response = new Response<>();
        try {
            AppealServedBy appealServedBy = appealServedByRepository.findById(feeId).get();
            appealServedBy.setDeleted(true);
            appealServedBy.setActive(false);
            appealServedBy.setDeletedBy(loggedUser.getInfo().getId());
            appealServedBy.setDeletedAt(LocalDateTime.now());
            appealServedByRepository.save(appealServedBy);

            response.setData(appealServedBy);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("Error deleting appeal served by", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Appeal served by could not be deleted");
            response.setStatus(false);
        }

        return response;
    }

}
