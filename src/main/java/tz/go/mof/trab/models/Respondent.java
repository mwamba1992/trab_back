package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;


@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Respondent")
public class Respondent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long respondentId;

    private String name;

    private String natureOfBussiness;

    private String phoneNumber;

    private String emailAdress;

    private String tinNumber = "NONE";

    private String  incomeTaxFileNumber = "NONE";

    private String vatNumber = "NONE";

    @Temporal(TemporalType.DATE)
    private Date createdDate;

}
