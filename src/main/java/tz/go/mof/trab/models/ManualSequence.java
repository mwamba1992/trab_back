package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;



@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name= "ManualSequence")
public class ManualSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int sequence;

}
