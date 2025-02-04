package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Joel M Gaitan
 *
 *
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "AppealServedBy")
@NamedQuery(name = "AppealServedBy.findAll", query = "SELECT a FROM Adress a")
public class AppealServedBy extends BaseEntity{
    private String appName;
    private String appPhone;
    private Date appDate;

    private String resoName;
    private String resoPhone;
    private Date resoDate;

    private String respondentFile;

    private String appellantFile;


}
