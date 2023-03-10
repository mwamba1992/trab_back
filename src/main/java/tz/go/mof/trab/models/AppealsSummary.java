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
@Table(name = "appeals_summary")
public class AppealsSummary {
    @Id
    private Long id;

    private int filled;

    private int pending;

    private  int decided;

    private int filledApplication;

}
