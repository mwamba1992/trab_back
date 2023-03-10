package tz.go.mof.trab.service;


import tz.go.mof.trab.models.PspReconcTrx;

import java.util.List;


public interface PspReconcTrxService {
    public PspReconcTrx savePspReconTrx(PspReconcTrx pspReconcTrx);
    public List<PspReconcTrx> findByBillControlNumberAndPspTrxnReceipt(String controlNumber, String pspReceipt, String fileId);
}
