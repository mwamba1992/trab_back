package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "manual_appeals_sequence")
public class ManualAppealsSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int incomeSequence;

    private int vatSequence;

    private  int customSequence;


}
