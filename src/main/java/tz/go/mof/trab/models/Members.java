package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "members")
public class Members extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private  String email;

    private String phone;

}
