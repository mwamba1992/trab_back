package tz.go.mof.trab.models;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Joel M Gaitan
 * 
 * 
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "TratAppeals")
public class TratAppeals {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String appealNo;




}
