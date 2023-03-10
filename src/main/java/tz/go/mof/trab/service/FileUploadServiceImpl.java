package tz.go.mof.trab.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tz.go.mof.trab.dto.payment.FileDetailsDto;
import tz.go.mof.trab.models.UploadedFile;
import tz.go.mof.trab.repositories.FileUploadedRepository;
import tz.go.mof.trab.utils.*;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;


@Service
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    private final CustomSpringEventPublisher customSpringEventPublisher;

    private final FileUploadedRepository fileUploadedRepository;

    private final GlobalMethods globalMethods;

    @Value("${tz.go.trab.file.upload-dir}")
    private String FILE_UPLOADED_PATH;

    FileUploadServiceImpl(FileUploadedRepository fileUploadedRepository, GlobalMethods globalMethods,
                          CustomSpringEventPublisher customSpringEventPublisher) {
        this.fileUploadedRepository = fileUploadedRepository;
        this.globalMethods = globalMethods;
        this.customSpringEventPublisher = customSpringEventPublisher;
    }

    @Override
    public Response storeFile(MultipartFile file, FileDetailsDto fileDetailsDto) {
        Response response = new Response();
        UploadedFile uploadedFile = new UploadedFile();
        try {
            if (!file.isEmpty()) {
                if (globalMethods.checkIfAllowedFile(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    uploadedFile.setPspName(fileDetailsDto.getPspName());
                    uploadedFile.setUploadedFileName(file.getOriginalFilename());
                    uploadedFile.setUploadedDate(LocalDateTime.now());
                    uploadedFile.setProcessedStatus(0);
                    uploadedFile.setUploadedFileSize(file.getSize());
                    uploadedFile.setUploadedFileType(extension);
                    uploadedFile.setFileMapperId(fileDetailsDto.getMappingId());

                    File FileName = new File(".");

                    UploadedFile savedUploadedFile = save(uploadedFile);
                    File fileInResource = new File(FILE_UPLOADED_PATH, savedUploadedFile.getFileId() + "." + extension);

                    Files.copy(file.getInputStream(), fileInResource.toPath());

                    customSpringEventPublisher.doStuffAndPublishAnEvent(savedUploadedFile.getFileId() + "." + extension);
                    System.out.println("##### Publishing  #####");
                    response.setStatus(true);
                    response.setDescription("SUCCESS");
                    response.setCode(ResponseCode.SUCCESS);

                } else {
                    response.setStatus(false);
                    response.setDescription("Invalid File Format Uploaded! Allowed {xls, csv, xlsx}");
                    response.setCode(ResponseCode.FAILURE);
                }
            } else {
                response.setStatus(false);
                response.setDescription("FAILURE");
                response.setCode(ResponseCode.FAILURE);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public UploadedFile save(UploadedFile uploadedFile) {
        return fileUploadedRepository.save(uploadedFile);
    }

    @Override
    public UploadedFile findUploadedById(String id) {
        return fileUploadedRepository.findById(id).get();
    }

    @Override
    public ListResponse < UploadedFile > getAllUploadedFile() {

        ListResponse < UploadedFile > uploadedFileListResponse = new ListResponse < > ();
        List < UploadedFile > uploadedFileList = (List < UploadedFile > )
                fileUploadedRepository.findAll(Sort.by(Sort.Direction.ASC, "uploadedDate"));

        if (!uploadedFileList.isEmpty()) {
            uploadedFileListResponse.setCode(ResponseCode.SUCCESS);
            uploadedFileListResponse.setData(uploadedFileList);
            uploadedFileListResponse.setStatus(true);
            uploadedFileListResponse.setTotalElements(Long.valueOf(uploadedFileList.size()));

        } else {
            uploadedFileListResponse.setCode(ResponseCode.NO_RECORD_FOUND);
            uploadedFileListResponse.setData(null);
            uploadedFileListResponse.setStatus(false);
        }
        return uploadedFileListResponse;
    }

    @Override
    public Response getReportByFileId(String format, String fileId) {
        Response response = new Response();
        String fileReport = "";
        try {
            if (format.equalsIgnoreCase("pdf")) {
                File FileName = new File(".");
                fileReport = FILE_UPLOADED_PATH +
                        fileId + "_report" + "." + "csv";

            }
            if (format.equalsIgnoreCase("csv")) {
                File FileName = new File(".");
                fileReport = FILE_UPLOADED_PATH +
                        fileId + "_report" + "." + "csv";

            }

            File fileToRead = new File(fileReport);
            byte[] fileContent = FileUtils.readFileToByteArray(fileToRead);
            String contentToSend = Base64.getEncoder().encodeToString(fileContent);

            logger.info("##### BASE 64 #####" + contentToSend);
            response.setData(contentToSend);
            response.setCode(ResponseCode.SUCCESS);
            response.setStatus(true);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
        }
        return response;
    }
}