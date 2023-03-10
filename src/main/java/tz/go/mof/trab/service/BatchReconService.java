package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.payment.ReconBatchDto;
import tz.go.mof.trab.models.ReconBatch;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import java.util.Map;

public interface BatchReconService {


    ReconBatch findById(String batchId);

    ListResponse<ReconBatch> findAllBatch();

    Response<ReconBatch> getOneBatch(String batchId);

    Response<ReconBatch>  saveBatch(ReconBatchDto reconBatchDto);

    Response<ReconBatch>  editBatch(ReconBatchDto reconBatchDto, String batchId);

    Response<ReconBatch> deleteBatch(String batchId);

    Response createReconManually(Map<String, String> req);

}
