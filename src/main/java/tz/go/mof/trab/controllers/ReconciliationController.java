package tz.go.mof.trab.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tz.go.mof.trab.dto.payment.FileDetailsDto;
import tz.go.mof.trab.models.UploadedFile;
import tz.go.mof.trab.service.FileUploadService;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import javax.validation.Valid;


@Controller
@RequestMapping("/api")
public class ReconciliationController {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private GlobalMethods globalMethods;

    @PostMapping(value = "/upload-file" ,produces = "application/json")
    @ResponseBody
    public Response receiveFile(@Valid FileDetailsDto fileDetailsDto
            , @RequestParam("file") MultipartFile file, @RequestParam("mappingId") String mappingId,
                                @RequestParam("pspName") String pspName) {

        logger.info("########  File Uploaded #######" + file.getOriginalFilename() +" ##### "+ file.getContentType() +  mappingId);
        fileDetailsDto.setMappingId(mappingId);
        fileDetailsDto.setPspName(pspName);
        return fileUploadService.storeFile(file, fileDetailsDto);

    }



    @GetMapping(value = "/uploaded-file")
    @ResponseBody
    public ListResponse<UploadedFile> getAllUploadedFiles() {
        logger.info("########  Get All Uploaded Files #######");
        return fileUploadService.getAllUploadedFile();

    }

    @GetMapping(value = "/format/{format}/file/{fileId}" ,produces = "application/json")
    @ResponseBody
    public Response getReportByFileId(@PathVariable("fileId") String fileId,
                                      @PathVariable("format") String format) {
        logger.info("########  Get Report of File By id #######");
        return fileUploadService.getReportByFileId(format, fileId);
    }

}
