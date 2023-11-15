package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;


@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "appeal_amount")
public class AppealAmount extends BaseEntity{

    private BigDecimal amountOnDispute;

    private BigDecimal allowedAmount;

    private  String currencyName;

    @ManyToOne
    @JoinColumn(name = "currencyId", nullable = false)
    private Currency currency;

    private String amountDescription;



}
