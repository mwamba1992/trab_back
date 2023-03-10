package tz.go.mof.trab.dto.user;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	@NotNull
	@NotBlank(message = "Names are mandatory")
	private String name;

	@NotBlank(message = "Sex is mandatory")
	private String sex;

	@Email(message = "Please enter a valid email address")
	@NonNull
	@NotBlank(message = "Email address is mandatory")
	@Column(nullable = false, unique = true, updatable = false)
	private String email;

	@NonNull
	private String mobileNumber;   
    
	@NonNull   
	@Column(nullable = false, updatable = false)
	private String checkNumber;   
   
	@Column(nullable = true)
	private String councilCode;   
	
	@Column(nullable = true)
	private String regionId;   
	
	@Basic(optional = true)
	private boolean accountNonExpired = false;

	@Basic(optional = true)
	private boolean accountNonLocked = false;

	@Basic(optional = true)
	private boolean credentialsNonExpired = false;

	@Basic(optional = true)
	private boolean enabled = false;

}
