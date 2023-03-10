package tz.go.mof.trab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.models.PspReconcTrx;
import tz.go.mof.trab.repositories.PspReconcTrxRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PspReconciliationTrxServiceImpl implements PspReconcTrxService{

    @Autowired
    private PspReconcTrxRepository pspReconcTrxRepository;

    @Override
    public PspReconcTrx savePspReconTrx(PspReconcTrx pspReconcTrx) {
        return pspReconcTrxRepository.save(pspReconcTrx);
    }

    @Override
    public List<PspReconcTrx> findByBillControlNumberAndPspTrxnReceipt(String controlNumber, String pspReceipt, String fileId) {
        return pspReconcTrxRepository.findByBillControlNumberAndPspTrxnReceiptAndFileId(controlNumber,pspReceipt,fileId);
    }
}
