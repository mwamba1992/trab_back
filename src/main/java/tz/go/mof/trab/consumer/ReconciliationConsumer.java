package tz.go.mof.trab.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.models.Payment;
import tz.go.mof.trab.models.PspReconcTrx;
import tz.go.mof.trab.models.UploadedFile;
import tz.go.mof.trab.repositories.PaymentRepository;
import tz.go.mof.trab.service.FileUploadService;
import tz.go.mof.trab.service.PspReconcTrxService;
import tz.go.mof.trab.utils.GlobalMethods;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author  Joel M Gaitan
 *
 * **/
@Component
public class ReconciliationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationConsumer.class);

    private final PspReconcTrxService pspReconcTrxService;

    private final FileUploadService fileUploadService;

    @Value("${tz.go.trab.file.upload-dir}")
    private String FILE_UPLOADED_PATH;

    @Value("${rabbitmq.trab.reconciliation.out.exchange}")
    private String reconOutExchange;

    @Value("${rabbitmq.trab.reconciliation.out.routing.key.out}")
    private String reconOutRoutingKey;

    private final PaymentRepository paymentRepository;

    @Autowired
    private GlobalMethods globalMethods;

    ReconciliationConsumer(GlobalMethods globalMethods, PspReconcTrxService pspReconcTrxService,
                           FileUploadService fileUploadService, PaymentRepository paymentRepository) {
        this.pspReconcTrxService = pspReconcTrxService;
        this.fileUploadService = fileUploadService;
        this.paymentRepository = paymentRepository;

    }

    @RabbitListener(id = "rabbitmq.trab.reconciliation.q.out", queues = "#{'${rabbitmq.trab.reconciliation.q.out}'.split(',')}")
    public void reconPspFileProcess(HashMap < Integer, ArrayList < String >> readable, @Header("mappingHeader") final Map < String, String > mappingHeader) {

        String id = mappingHeader.get("id");

        try {

            logger.info("######## Mapping Heads ####### " + mappingHeader);
            logger.info("Time: " + LocalDateTime.now());
            logger.info("#### HashMap ### " + readable);

            for (Map.Entry < Integer, ArrayList < String >> entry: readable.entrySet()) {
                int key = entry.getKey();
                ArrayList < String > value = entry.getValue();
                if (key > 0) {
                    String controlNumber = (globalMethods.extractControlNumber(value.get(Integer.parseInt(mappingHeader.get("controlNUmber")))));
                    String amount = value.get(Integer.parseInt(mappingHeader.get("amount")));
                    String pspReceipt = value.get(Integer.parseInt(mappingHeader.get("pspReceipt")));
                    String trxDate = value.get(Integer.parseInt(mappingHeader.get("trxDate")));

                    List < PspReconcTrx > pspReconciliationTrxList = pspReconcTrxService.findByBillControlNumberAndPspTrxnReceipt(controlNumber,
                            pspReceipt, id);

                    pspReconciliationTrxList.forEach(trx -> {
                            trx.setRemark(value.get(value.size() - 1));
                    pspReconcTrxService.savePspReconTrx(trx);
                    });

                }
            }


            UploadedFile uploadedFile = fileUploadService.findUploadedById(mappingHeader.get("id"));
            uploadedFile.setProcessedStatus(2);
            uploadedFile.setReconciledTrxn(0L);
            fileUploadService.save(uploadedFile);


            File FileName = new File(".");
            String pspTempFilePathCsv = FILE_UPLOADED_PATH +
                    uploadedFile.getFileId() + "_report" + "." + "csv";
            File file = new File(pspTempFilePathCsv);
            Writer writer = new FileWriter(file, true);

            String pspReconFileDtoCsv = globalMethods.convertHashMapToCsv(readable);
            globalMethods.csvWriterReconFile(writer, pspReconFileDtoCsv);
            logger.info("#######  file created successful ######");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RabbitListener(id = "rabbitmq.trab.reconciliation.q.in", queues = "#{'${rabbitmq.trab.reconciliation.q.in}'.split(',')}")
    public void findTransactions(HashMap < Integer, ArrayList < String >> readable, @Header("mappingHeader") final Map < String, String > mappingHeader) {
        logger.info("######################" + mappingHeader);

        int reconciledCounter = 0;

        for (Map.Entry < Integer, ArrayList < String >> entry: readable.entrySet()) {
            int key = entry.getKey();
            ArrayList < String > value = entry.getValue();
            if (key > 0) {
                String controlNumber = (globalMethods.extractControlNumber(value.get(Integer.parseInt(mappingHeader.get("controlNUmber")))));
                String amount = value.get(Integer.parseInt(mappingHeader.get("amount")));
                String pspReceipt = value.get(Integer.parseInt(mappingHeader.get("pspReceipt")));
                String trxDate = value.get(Integer.parseInt(mappingHeader.get("trxDate")));

                Payment payment;
                boolean exception = false;
                try {
                    payment = paymentRepository.findByPayCtrNumAndPspReceiptNumberAndPaidAmt(controlNumber,
                            pspReceipt, new BigDecimal(amount));

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("#####################" + e.getMessage());
                    exception = true;
                    payment = null;
                }


                if (payment != null) {

                    logger.error(" ##########  Transaction Found ########### " + " ControlNumber: " + controlNumber +
                            " Psp Receipt: " + pspReceipt + " Amount: " + amount);
                    reconciledCounter = reconciledCounter + 1;
                    payment.setPspReconciled(true);
                    paymentRepository.save(payment);
                    value.add("Found");
                } else {

                    logger.error(" ##########  Transaction Not Found ########### " + " ControlNumber: " + controlNumber +
                            " Psp Receipt: " + pspReceipt + " Amount: " + amount);

                    if (exception) {
                        value.add("Exception  occurred Filled Amount Missing");
                    } else {
                        value.add("Not Found");
                    }
                }

            }
        }

        logger.info("id: " + mappingHeader.get("id") + " Unreconciled: " + reconciledCounter);
        mappingHeader.put("reconciledCount", String.valueOf(reconciledCounter));
        globalMethods.publishToExchangeWithHeaders(reconOutExchange, reconOutRoutingKey, readable, mappingHeader);
        System.out.println("##################" + readable);
    }
}