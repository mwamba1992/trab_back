package tz.go.mof.trab.models;

import javax.persistence.*;

import lombok.Data;
import java.io.Serializable;


@Entity
@Table(name = "role_user")
@IdClass(UserRoleId.class)
@Data
public class UserRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id    
	@Column(name="role_id")
	private String roleId;
    
	@Id
	@Column(name="user_id")
    private String userId;
	
	
}