package tz.go.mof.trab.models;


import lombok.*;
import tz.go.mof.trab.utils.CustomGeneratedData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "trr_uploaded_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UploadedFile {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String fileId = CustomGeneratedData.GenerateUniqueID();

    private String uploadedFilePath;

    private long uploadedFileSize;

    private LocalDateTime uploadedDate;

    private String uploadedFileName;

    private String uploadedFileType;

    private long numReconTrxn;

    private long reconciledTrxn;

    private int processedStatus;

    private String fileMapperId;

    private String reconReportFilePath;

    private String pspName;

    private String procesedStatus;

    private String reportFile;


}
