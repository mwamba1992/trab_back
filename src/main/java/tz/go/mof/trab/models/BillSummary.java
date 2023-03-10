package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;



/**
 * @ Termis Development Team
 *
 *
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "bill_summary")
public class BillSummary extends BaseEntity{

    private  String itemId;

    private  String name;

    private double totalBills;

    private  double paidBills;

    private double pendingBills;

    private double expiredBills;

    private BigDecimal totalBillsAmount;

    private BigDecimal paidBillsAmount;

    private BigDecimal pendingAmount;


}
