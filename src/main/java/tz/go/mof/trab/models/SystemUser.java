package tz.go.mof.trab.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mof.trab.utils.CustomGeneratedData;


/**
 * @author Mwamba_Mwendavano
 *
 */


@Audited
@Getter 
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "SystemUser")
@NamedQuery(name = "SystemUser.findAll", query = "SELECT u FROM SystemUser u")
public class SystemUser implements  UserDetails, Serializable {

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id = CustomGeneratedData.GenerateUniqueID();

	@Column(unique = true, name = "username", updatable = false)
	private String username;

	@Column(nullable = true, name = "check_number", updatable = true)
	private String checkNumber;

	@Column(length = 1000)
	private String password;

	@Column(length = 100)
	private String Address;
	
	@Column(nullable = false, length = 100)
	private String email;

	@Column(nullable = true, name = "mobile_number", updatable = true)
	private String mobileNumber;

	@Basic(optional = false)
	private String name;

	@Basic(optional = true)
	@Column(name = "created_by")
	private String createdBy;

	@Basic(optional = false)
	@Column(name = "new_account")
	private boolean newAccount = true;

	@Basic(optional = true)
	@Column(name = "updated_by")
	private String updatedBy;

	@Basic(optional = true)
	@Column(name = "deleted_by")
	private String deletedBy;

	@Column(nullable = false)
	private Date RecordCreatedDate;
	
	private boolean isLogggedIn = false;
	
	@Transient
	@JsonIgnore
	private Collection<? extends GrantedAuthority> authorities;

	@JoinTable(name = "role_user", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Role> rolesList;

	
	@Basic(optional = true)
	@Column(name = "account_non_expired")
	private boolean accountNonExpired = false;

	@Basic(optional = true)
	@Column(name = "login_attempt")
	private int loginAttempt = 0;

	@Basic(optional = true)
	@Column(name = "account_non_locked")
	private boolean accountNonLocked = false;

	@Basic(optional = true)
	@Column(name = "credentials_non_expired")
	private boolean credentialsNonExpired = false;
	
	@Basic(optional = true)
	@Column(name = "enabled")
	private boolean enabled = false;

	@Override
	public String getUsername() {
		return username;
	}

}
