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
@Table(name = "recon_batch")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReconBatch extends  BaseEntity{

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id = CustomGeneratedData.GenerateUniqueID();

    private int transactionReceived;

    private int transactionPresent;


}
