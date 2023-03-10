package tz.go.mof.trab.models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;


@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name="Status")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Status {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long StatusId;
	
	@NotNull
	@Size(min=2, max=30)
	public String StatusName;
	
	@NotNull
	@Size(min=4, max=100)
	public String StatusDescription;
	
	public int RecordCreatedBy;
	public Timestamp RecordCreatedDate;
}
