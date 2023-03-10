package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.bytebuddy.asm.Advice;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Audited
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "trr_financial_year")
public class FinancialYear extends BaseEntity implements Serializable {

    private String financialYear;

}
