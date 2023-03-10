package tz.go.mof.trab.utils;


import lombok.*;
import tz.go.mof.trab.models.Permission;
import java.util.List;
import java.util.Map;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDetails {

	private String id;
	private String email;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private boolean isAgent;  
	private String agentCode;
	private String name; 
	private String checkNumber;  
	private String username;   
	private String regionId;    
	private String councilCode;
	private List<Map<String, String>> permissionList;
}
