package tz.go.mof.trab.service;


import org.springframework.web.multipart.MultipartFile;
import tz.go.mof.trab.dto.payment.FileDetailsDto;
import tz.go.mof.trab.models.UploadedFile;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;


public interface FileUploadService {
    Response storeFile(MultipartFile file, FileDetailsDto fileDetailsDto);
    UploadedFile save(UploadedFile uploadedFile);
    UploadedFile findUploadedById(String id);
    ListResponse<UploadedFile> getAllUploadedFile();
    Response getReportByFileId(String format, String fileId);

}
