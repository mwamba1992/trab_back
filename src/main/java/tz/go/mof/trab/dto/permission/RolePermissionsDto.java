package tz.go.mof.trab.dto.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionsDto {
	
	@NotNull
	@NotBlank(message="Role is mandatory")
    private String roleId;
    
	private List<PermissionIdModel> permissionList;

}
