package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.payment.ReconBatchDto;
import tz.go.mof.trab.models.ReconBatch;
import tz.go.mof.trab.repositories.ReconBatchRepository;
import tz.go.mof.trab.utils.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class BatchReconServiceImpl implements BatchReconService {

    private static final Logger logger = LoggerFactory.getLogger(FeesServiceImpl.class);

    private final ReconBatchRepository reconBatchRepository;

    Response<ReconBatch> response = new Response<ReconBatch>();

    ListResponse<ReconBatch> reconBatchListResponse = new ListResponse<ReconBatch>();

    private final LoggedUser loggedUser;

    private final GepgMiddleWare gepgMiddleWare;

    BatchReconServiceImpl(GepgMiddleWare gepgMiddleWare, LoggedUser loggedUser, ReconBatchRepository reconBatchRepository){
        this.gepgMiddleWare = gepgMiddleWare;
        this.loggedUser = loggedUser;
        this.reconBatchRepository = reconBatchRepository;
    }

    @Override
    public ReconBatch findById(String batchId) {
        return reconBatchRepository.findById(batchId).get();
    }

    @Override
    public ListResponse<ReconBatch> findAllBatch() {
        List<ReconBatch> batches = (List<ReconBatch>) reconBatchRepository.findAll();


        if (batches.size() < 1) {
            reconBatchListResponse.setCode(ResponseCode.NO_RECORD_FOUND);
            reconBatchListResponse.setStatus(false);
            reconBatchListResponse.setData(null);
        } else {
            reconBatchListResponse.setCode(ResponseCode.SUCCESS);
            reconBatchListResponse.setStatus(true);
            reconBatchListResponse.setData(batches);
            reconBatchListResponse.setTotalElements(Long.valueOf(batches.size()));
        }
        return reconBatchListResponse;
    }

    @Override
    public Response<ReconBatch> getOneBatch(String batchId) {
        return null;
    }

    @Override
    public Response<ReconBatch> saveBatch(ReconBatchDto reconBatchDto) {
        logger.info("########## Req ##########" + reconBatchDto);


        try {

            ReconBatch reconBatch = new ReconBatch();
            TrabHelper.copyNonNullProperties(reconBatchDto, reconBatch);

            response.setCode(ResponseCode.SUCCESS);
            reconBatch.setCreatedBy(loggedUser.getInfo().getId());
            response.setData(reconBatchRepository.save(reconBatch));
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
    public Response<ReconBatch> editBatch(ReconBatchDto reconBatchDto, String batchId) {
        try {
            if (reconBatchRepository.findById(batchId).get() != null) {
                ReconBatch batch = reconBatchRepository.findById(batchId).get();
                TrabHelper.copyNonNullProperties(reconBatchDto, batch);

                batch.setUpdatedAt(LocalDateTime.now());
                batch.setUpdatedBy(loggedUser.getInfo().getId());
                response.setCode(ResponseCode.SUCCESS);
                response.setData(reconBatchRepository.save(batch));
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
    public Response<ReconBatch> deleteBatch(String batchId) {
        try {
            ReconBatch batch = reconBatchRepository.findById(batchId).get();
            batch.setDeleted(true);
            batch.setActive(false);
            batch.setDeletedBy(loggedUser.getInfo().getId());
            batch.setDeletedAt(LocalDateTime.now());
            reconBatchRepository.save(batch);

            response.setData(batch);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        } catch (Exception e) {
            logger.error("########" + e.getMessage() + "###########");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Recon Batch! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Response createReconManually(Map<String, String> req) {
        Response response = new Response();
        String date = req.get("date").split("T")[0];

        try {
            if (gepgMiddleWare.sendReconBatch(date)) {
                response.setCode(ResponseCode.SUCCESS);
                response.setStatus(true);
                response.setDescription("Success");
            }else {
                response.setCode(ResponseCode.FAILURE);
                response.setStatus(false);
                response.setDescription("Failure");
            }
        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
            response.setDescription("Failure");
        }

        return response;
    }
}
