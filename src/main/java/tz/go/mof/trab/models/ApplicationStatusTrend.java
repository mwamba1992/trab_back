package tz.go.mof.trab.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

/**
 * @author Mwamba_Mwendavano
 * 
 * 
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "ApplicationStatusTrend")
public class ApplicationStatusTrend extends BaseEntity{


	private String applicationStatusTrendDesc;

	private String applicationStatusTrendName;

	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
}
