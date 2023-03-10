package tz.go.mof.trab.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tz.go.mof.trab.dto.payment.ReconBatchDto;
import tz.go.mof.trab.models.ReconBatch;
import tz.go.mof.trab.service.BatchReconService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import java.util.Map;


@Controller
@RequestMapping("/api/batches")
public class ReconBatchController {

    @Autowired
    private BatchReconService batchReconService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<ReconBatch> viewAllReconBatches() {
        return batchReconService.findAllBatch();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<ReconBatch> createReconBatch(@Valid @RequestBody ReconBatchDto reconBatchDto) {

        return batchReconService.saveBatch(reconBatchDto);
    }


    @GetMapping(path = "/{batchId}", produces = "application/json")
    @ResponseBody
    public Response<ReconBatch> getOneRevenue(@PathVariable("batchId") String batchId) {
        return batchReconService.getOneBatch(batchId);
    }


    @PutMapping(path = "/{batchId}", produces = "application/json")
    @ResponseBody
    public Response<ReconBatch> editRevenue(@PathVariable("batchId") String batchId,
                                      @Valid @RequestBody ReconBatchDto reconBatchDto) {
        return batchReconService.editBatch(reconBatchDto, batchId);
    }


    @DeleteMapping(path = "/{batchId}", produces = "application/json")
    @ResponseBody
    public Response<ReconBatch> deleteBatch(@PathVariable("batchId") String batchId) {

        return batchReconService.deleteBatch(batchId);

    }


    @PostMapping(path = "/create-batch", produces = "application/json")
    @ResponseBody
    public Response manuallyCreateBatch(@RequestBody Map<String,String> req) {
        return batchReconService.createReconManually(req);

    }
    
}
