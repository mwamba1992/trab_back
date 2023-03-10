package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@ToString
@Entity
@Table(name = "yearly_cases")
public class YearlyCases {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    private String type;

    private int jan;

    private int  feb;

    private int mar;

    private int apr;

    private int may;

    private int jun;

    private int jul;

    private int aug;

    private int sep;

    private int oct;

    private int nov;

    private int dece;
}
