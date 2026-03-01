package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BatchReconServiceImpl.class);

    private final ReconBatchRepository reconBatchRepository;

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
        ListResponse<ReconBatch> reconBatchListResponse = new ListResponse<>();
        List<ReconBatch> batches = (List<ReconBatch>) reconBatchRepository.findAll();


        if (batches.isEmpty()) {
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
        Response<ReconBatch> response = new Response<>();
        logger.debug("Saving recon batch: {}", reconBatchDto);

        try {

            ReconBatch reconBatch = new ReconBatch();
            TrabHelper.copyNonNullProperties(reconBatchDto, reconBatch);

            reconBatch.setCreatedBy(loggedUser.getInfo().getId());
            response.setData(reconBatchRepository.save(reconBatch));
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);


        } catch (Exception e) {
            logger.error("Error saving recon batch", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<ReconBatch> editBatch(ReconBatchDto reconBatchDto, String batchId) {
        Response<ReconBatch> response = new Response<>();
        try {
            ReconBatch batch = reconBatchRepository.findById(batchId).orElse(null);
            if (batch != null) {
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
                response.setDescription("Error updating recon batch");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Recon batch not found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<ReconBatch> deleteBatch(String batchId) {
        Response<ReconBatch> response = new Response<>();
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
            logger.error("Error deleting recon batch", e);
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Recon batch could not be deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Response<?> createReconManually(Map<String, String> req) {
        Response<Void> response = new Response<>();
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
