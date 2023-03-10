package tz.go.mof.trab.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "OtherBills")
@NamedQuery(name = "OtherBills.findAll", query = "SELECT a FROM OtherBills a")

public class OtherBills {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long otherBillsId;
	
	@Temporal(TemporalType.DATE)
	private Date loggedAt;

	
	private String  noticeNo;

	@JsonIgnore
	@OneToOne(optional = true)
	@JoinColumn(name = "billId", nullable = true)
	private  Bill billId;


}
