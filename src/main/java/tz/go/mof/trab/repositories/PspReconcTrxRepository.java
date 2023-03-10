package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.mof.trab.models.PspReconcTrx;

import java.util.List;

public interface PspReconcTrxRepository extends JpaRepository<PspReconcTrx,String> {
    public List<PspReconcTrx> findByBillControlNumberAndPspTrxnReceiptAndFileId(String controlNumber, String ppsReceipt, String fileId);

}
