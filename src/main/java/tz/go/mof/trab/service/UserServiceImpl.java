package tz.go.mof.trab.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.user.PasswordDto;
import tz.go.mof.trab.dto.user.UserDto;
import tz.go.mof.trab.models.SystemUser;
import tz.go.mof.trab.repositories.UserRepository;
import tz.go.mof.trab.utils.*;


@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	LoggedUser loggedUser;

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	Response<SystemUser> response = new Response<>();

	Response<String> responseTxt = new Response<>();
	ListResponse<SystemUser> listResponse = new ListResponse<>();

	@Autowired
	UserRepository userRepository;


	@Override
	public Response<SystemUser> saveUser(UserDto userDto, BindingResult result) {

		try {

			Boolean flag = true;
//			String rawPassword=Common.generateRandomPassword(8);

			if (!result.hasErrors()) {

				SystemUser user = new SystemUser();

				TrabHelper.copyNonNullProperties(userDto, user);

				List<SystemUser> optCheckNumber = userRepository.findByCheckNumber(userDto.getCheckNumber());

				Optional<SystemUser> optEmail = userRepository.findByEmail(userDto.getEmail());

				TrabHelper.print(user);

				if ((optCheckNumber.size() > 0) && (user.getCheckNumber() != null)) {

					logger.info(" Check Number already exists ");

					response.setData(null);
					response.setCode(ResponseCode.DUPLICATE);
					response.setDescription("Check number exists");
					response.setStatus(true);
					flag = false;
				}

				if (optEmail.isPresent()) {

					logger.info(" Email address already exists");

					response.setData(null);
					response.setCode(ResponseCode.DUPLICATE);
					response.setDescription("Email address already exists");
					response.setStatus(true);
					flag = false;
				}

				if (flag) {

					String newPassword = "Trais@123456*";

					String encodedPassword = Common.encodePassword(newPassword);

					user.setUsername(user.getEmail());
					user.setRecordCreatedDate(new Date());
					user.setPassword(encodedPassword);

					if (loggedUser.getInfo() != null) {
						user.setCreatedBy(loggedUser.getInfo().getId());
					}
					SystemUser savedUser = userRepository.save(user);

					response.setData(savedUser);
					response.setCode(ResponseCode.SUCCESS);
					response.setDescription("User account was successful created");
					response.setStatus(true);


					// Send Credentials to User

					try {
						Common obj = new Common();
						String message = obj.newAccountMessage(savedUser.getName(), savedUser.getEmail(), newPassword);
						String messageSubject="TRAIS - SYSTEM";
						//String payload=obj.prepareMessage(savedUser, messageSubject, message);
						
						//logger.info("Message to Queue {}", payload);
						
					   //sending credentials ro user


					} catch (Exception e) {
						e.printStackTrace();
					}
					
					logger.info(" Registering user with Details {}: ", savedUser);

				}

			} else {
				logger.error("Data validation failure ", result.getFieldError().getDefaultMessage());

				response.setData(null);
				response.setCode(ResponseCode.FAILURE);
				response.setDescription(result.getFieldError().getDefaultMessage());
				response.setStatus(false);

			}

		} catch (Exception e) {
			logger.error("Failed to register user with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setStatus(false);
			response.setDescription(e.toString());

		}

		return response;

	}

	@Override
	public Response<SystemUser> findUserByUsername(String username) {

		try {

			Optional<SystemUser> user = userRepository.findByUsername(username);

			if (user.isPresent()) {

				response.setData(user.get());
				response.setCode(ResponseCode.SUCCESS);
				response.setStatus(true);
				response.setDescription("Successfull");

			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setStatus(false);
				response.setDescription("No record was found");

			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Failed to retrieve user by agent code with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setStatus(false);
		}

		return response;

	}

	@Override
	public Response<SystemUser> deleteUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<SystemUser> updateUser(UserDto userDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListResponse<SystemUser> getAllUsers() {

		try {

			List<SystemUser> availableUsers = userRepository.findAll();

			if (availableUsers.size() > 0) {
				listResponse.setData(availableUsers);
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setStatus(true);
			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all users with error : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setStatus(false);
		}

		return listResponse;
	}

	@Override
	public Response<SystemUser> editUser(UserDto userDto, String userId) {
		try {

			Optional<SystemUser> optUser = userRepository.findById(userId);

			SystemUser user = null;
			if (optUser.isPresent()) {
				user = optUser.get();
				TrabHelper.copyNonNullProperties(userDto, user);
				SystemUser updatedUser = userRepository.save(user);
				if (updatedUser != null) {

					response.setData(updatedUser);
					response.setCode(ResponseCode.SUCCESS);
					response.setStatus(true);

				} else {
					logger.info("User  with id " + userId + " failed to be updated");
					response.setData(null);
					response.setCode(ResponseCode.FAILURE);
					response.setStatus(false);

				}
			} else {
				logger.info("User not updated :  " + userId + " is not existing");
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setStatus(false);

			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Failed to update  applicant by user Id with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setStatus(false);
		}
		return response;
	}

	@Override
	public Response<SystemUser> findById(String userId) {
		try {

			Optional<SystemUser> user = userRepository.findById(userId);

			if (user.isPresent()) {

				response.setData(user.get());
				response.setCode(ResponseCode.SUCCESS);
				response.setStatus(true);
			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setStatus(false);
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Failed to retrieve user by user id with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setStatus(false);
		}

		return response;
	}

	@Override
	public Response<String> changePassword(String userId, PasswordDto passwordDto, BindingResult result) {

		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
			Optional<SystemUser> userOpt = userRepository.findById(userId);

			SystemUser  user = null;

			if (userOpt.isPresent()) {

				if (!result.hasErrors()) {
					user = userOpt.get();

					if (encoder.matches(passwordDto.getOldPassword(), user.getPassword())) {

						if (!encoder.matches(passwordDto.getNewPassword(), user.getPassword())) {
							user.setPassword(encoder.encode(passwordDto.getNewPassword()));
							user.setNewAccount(false);

							userRepository.save(user);
							responseTxt.setData(null);
							responseTxt.setCode(ResponseCode.SUCCESS);
							responseTxt.setDescription("Operation was successfully");
							responseTxt.setStatus(true);
						} else {
							responseTxt.setData(null);
							responseTxt.setCode(ResponseCode.FAILURE);
							responseTxt.setDescription("New password must not match with the old password");
							responseTxt.setStatus(false);

						}
					} else {
						responseTxt.setData(null);
						responseTxt.setCode(ResponseCode.FAILURE);
						responseTxt.setDescription("Incorrect current password");
						responseTxt.setStatus(false);

					}
				} else {

					responseTxt.setData(null);
					responseTxt.setCode(ResponseCode.FAILURE);
					responseTxt.setDescription("PassWord Must Contain At least One Lower Case, One Upper Case, One Special Character And One Number");
					responseTxt.setStatus(false);

				}
			} else {
				responseTxt.setData("Sorry! No record was found");
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setStatus(false);
				responseTxt.setDescription(null);

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve user by user id with error : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(null);
			responseTxt.setStatus(false);
		}

		return responseTxt;
	}


	@Override
	public Response<String> resetUserPassword(String userId) {

		try {
			Optional<SystemUser> userOpt = userRepository.findById(userId);

			String rawPassword = Common.generateRandomPassword(8);
			String encodedPassword = Common.encodePassword(rawPassword);

			if (userOpt.isPresent()) {

				SystemUser updatedUser = userOpt.get();

				updatedUser.setPassword(encodedPassword);
				updatedUser.setNewAccount(true);
				userRepository.save(updatedUser);

				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.SUCCESS);
				responseTxt.setDescription(rawPassword);
				responseTxt.setStatus(true);
				
				// Send Mail Notification
				try {
					Common obj = new Common();
					String message = obj.changePassword(updatedUser.getName(), updatedUser.getEmail(), rawPassword);
					String messageSubject="TERMIS SYSTEM - PASSWORD UPDATE";		
					//String payload=obj.prepareMessage(updatedUser, messageSubject, message);
				  // sending email for  password

				} catch (Exception e) {
					e.printStackTrace();
				}
				

			} else {
				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setDescription("Sorry! No record was found");
				responseTxt.setStatus(false);
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Failed to retrieve user by user id with error : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(null);
			responseTxt.setStatus(false);
		}

		return responseTxt;
	}

	@Override
	public ListResponse<SystemUser> getAllDisabled() {

		try {

			List<SystemUser> availableUsers = userRepository.findByEnabled(false);

			if (availableUsers.size() > 0) {
				listResponse.setData(availableUsers);
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setStatus(true);
			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all users with error : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setStatus(false);
		}

		return listResponse;
	}

	@Override
	public void updateFailAttempts(String username) {

		try {

			Optional<SystemUser> user = userRepository.findByUsername(username);

			SystemUser user_acc = null;

			if (user.isPresent()) {
				user_acc = user.get();

				int attempts = user_acc.getLoginAttempt() + 1;
				user_acc.setLoginAttempt(attempts);
				userRepository.save(user_acc);
			}

		} catch (Exception e) {

		}

	}


}
