package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ Termis Development Team
 *
 *
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "trr_column_mapper")
public class ColumnMapper extends BaseEntity{

    private String controlNumber;

    private String pspReceipt;

    private String trxDateTime;

    private String currency;

    private String amount;

    private String  userId;


}
