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

@Audited
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "bank_account")
public class BankAccount extends  BaseEntity{

    private String accountNumber;

    private String bankAccountName;

    private String bankName;

    @ManyToOne
    @JoinColumn(name = "currencyId", nullable = false)
    private Currency currency;


}
