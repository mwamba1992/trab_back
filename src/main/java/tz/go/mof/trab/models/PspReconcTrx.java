package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mof.trab.utils.CustomGeneratedData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "trr_psp_recon_trx")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PspReconcTrx {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id = CustomGeneratedData.GenerateUniqueID();

    private String billControlNumber;

    private String currencyShortCode;

    private String recordReceivedDate;

    private String collectAccntNum;

    private String reconciliationFlag;

    private String remark;

    private String transactionDate;

    private String pspTrxnReceipt;

    private String aputaTrxnRefNum;

    private String reconRemark;

    private String amountPaid;

    private String reconciliationDate;

    private String pspFileToken;

    private String reconFileBatchNumber;

    private String  fileId;

}
