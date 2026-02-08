package tz.go.mof.trab.service;

import org.springframework.validation.BindingResult;

import tz.go.mof.trab.dto.user.PasswordDto;
import tz.go.mof.trab.dto.user.UserDto;
import tz.go.mof.trab.models.SystemUser;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;


public interface UserService {
	
    public Response<SystemUser> saveUser(UserDto userDto, BindingResult result);

	
	public Response<SystemUser> findUserByUsername(String username);

	public Response<SystemUser> findById(String userId);

	public Response<SystemUser> updateUser(UserDto userDto);
	
	public Response<SystemUser> deleteUser(String userId);
	
	public ListResponse<tz.go.mof.trab.dto.user.UserResponseDto> getAllUsers();

	public ListResponse<SystemUser> getAllDisabled();

	public Response<SystemUser> editUser(UserDto userDto, String userId);
	
	public Response<String> changePassword(String userId, PasswordDto passwordDto, BindingResult result);

	public Response<String> resetUserPassword(String userId);

	public void updateFailAttempts(String username);
			 
}
