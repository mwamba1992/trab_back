package tz.go.mof.trab.utils;

import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.models.ColumnMapper;
import tz.go.mof.trab.models.PspReconcTrx;
import tz.go.mof.trab.models.UploadedFile;
import tz.go.mof.trab.service.ColumnMapperService;
import tz.go.mof.trab.service.FileUploadService;
import tz.go.mof.trab.service.PspReconcTrxService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



@Component
public class CustomSpringEventListener implements ApplicationListener < CustomSpringEvent > {

    private static final Logger logger = LoggerFactory.getLogger(CustomSpringEventListener.class);

    @Value("${tz.go.trab.file.upload-dir}")
    private String FILE_UPLOADED_PATH;

    @Value("${rabbitmq.trab.reconciliation.out.exchange}")
    private String reconOutExchange;

    @Value("${rabbitmq.trab.reconciliation.out.routing.key}")
    private String reconOutRoutingKey;

    private final FileUploadService fileUploadService;

    private final ColumnMapperService columnMapperService;

    private final GlobalMethods globalMethods;

    private final PspReconcTrxService pspReconcTrxService;

    CustomSpringEventListener(GlobalMethods globalMethods, PspReconcTrxService pspReconcTrxService,
                              ColumnMapperService columnMapperService, FileUploadService fileUploadService) {
        this.globalMethods = globalMethods;
        this.pspReconcTrxService = pspReconcTrxService;
        this.columnMapperService = columnMapperService;
        this.fileUploadService = fileUploadService;

    }

    @Override
    public void onApplicationEvent(CustomSpringEvent event) {
        try {
            File FileName = new File(".");
            File file = new File(FILE_UPLOADED_PATH + event.getMessage());
            InputStream ExcelFileToRead = new FileInputStream(file.getPath());

            logger.info("##### finding from listener ####### id: " + FilenameUtils.removeExtension(file.getName()));
            UploadedFile uploadedFile = fileUploadService.findUploadedById(FilenameUtils.removeExtension(file.getName()));
            ColumnMapper columnMapper = columnMapperService.findMapperById(uploadedFile.getFileMapperId());


            HashMap < Integer, ArrayList < String >> readable = globalMethods.readReconFile(FILE_UPLOADED_PATH +
                    event.getMessage(), Files.getFileExtension(file.getName()));

            int numberOfRows = readable.size() - 1;
            uploadedFile.setNumReconTrxn(numberOfRows);

            int controlNumberIndex = readable.get(0).indexOf(columnMapper.getControlNumber());
            int pspReceiptIndex = readable.get(0).indexOf(columnMapper.getPspReceipt());
            int trxDateIndex = readable.get(0).indexOf(columnMapper.getTrxDateTime());
            int amount = readable.get(0).indexOf(columnMapper.getAmount());


            logger.info("  ######   Mapping ########  " + "ControlNumber: " + controlNumberIndex + " psp Receipt: " + pspReceiptIndex +
                    " trxDate: " + trxDateIndex + " amount: " + amount);

            for (Map.Entry < Integer, ArrayList < String >> entry: readable.entrySet()) {
                PspReconcTrx pspReconcTrx = new PspReconcTrx();
                int key = entry.getKey();
                ArrayList < String > value = entry.getValue();
                if (key > 0) {
                    pspReconcTrx.setBillControlNumber(globalMethods.extractControlNumber(value.get(controlNumberIndex)));
                    pspReconcTrx.setAmountPaid(value.get(amount));
                    pspReconcTrx.setPspTrxnReceipt(value.get(pspReceiptIndex));
                    pspReconcTrx.setTransactionDate(value.get(trxDateIndex));
                    pspReconcTrx.setFileId(FilenameUtils.removeExtension(file.getName()));
                    pspReconcTrxService.savePspReconTrx(pspReconcTrx);
                }
            }


            Map < String, String > mappingHeader = new HashMap < > ();
            mappingHeader.put("controlNUmber", String.valueOf(controlNumberIndex));
            mappingHeader.put("pspReceipt", String.valueOf(pspReceiptIndex));
            mappingHeader.put("trxDate", String.valueOf(trxDateIndex));
            mappingHeader.put("amount", String.valueOf(amount));
            mappingHeader.put("id", FilenameUtils.removeExtension(file.getName()));


            logger.info("########  Publishing ######");
            globalMethods.publishToExchangeWithHeaders(reconOutExchange, reconOutRoutingKey, readable, mappingHeader);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("###########" + e.getMessage());

        }
    }
}