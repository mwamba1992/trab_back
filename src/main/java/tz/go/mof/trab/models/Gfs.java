package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * @ Joel M Gaitan
 *
 *
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@AuditOverride(forClass = BaseEntity.class, isAudited = true)
@ToString
@Entity
@Table(name = "gfs")
public class Gfs extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String gfsCode;

    private  String gfsName;

    
}
