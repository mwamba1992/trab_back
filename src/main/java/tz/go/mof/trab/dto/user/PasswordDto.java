package tz.go.mof.trab.dto.user;

import javax.validation.constraints.NotEmpty;

public class PasswordDto {
	
	@NotEmpty
    private String oldPassword;
  
    @NotEmpty
    @ValidPassword
    private String newPassword;
    
    
    public void setNewPassword(String newPassword) {
    	
    	this.newPassword=newPassword;
    	
    }
    
    public String getNewPassword() {
    	
    	return this.newPassword;
    	
    }
    
    public void setOldPassword(String oldPassword) {
    	
    	this.oldPassword=oldPassword;
    	
    }
    
    public String getOldPassword() {
    	
    	return this.oldPassword;
    	
    }
    
    
}