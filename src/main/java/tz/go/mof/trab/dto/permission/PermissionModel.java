package tz.go.mof.trab.dto.permission;

import java.io.Serializable;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;



public class PermissionModel implements Serializable{
	

	private static final long serialVersionUID = 1L;

		
	private String categoryName;
	
	private Boolean active;
	

	private List<Permissions> permissions=new ArrayList<Permissions>();
	
	
	public void setCategoryName(String categoryName) {
		
		this.categoryName=categoryName;
	}
	
	
	public String getCategoryName() {
		
		return this.categoryName;
	} 
	
	
	public void setActive(Boolean active) {
		
		this.active=active;
	}
	
	public Boolean getActive() {
		
		return this.active;
	}
	
	public void addPermission(Permissions permission) {
		
		permissions.add(permission);
				
	}
	
	public List<Permissions> getPermissions(){
		
		return this.permissions;
	}
	
	
	
}
