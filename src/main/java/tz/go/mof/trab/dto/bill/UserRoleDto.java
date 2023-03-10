package tz.go.mof.trab.dto.bill;

import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRoleDto {
	
	@NotNull
	@NotBlank(message="User is mandatory")
	@Column(name="user_id",nullable=false)
    private String userId;

	private String roleId;
    
    private List<RoleIdModel> roleList;

}
