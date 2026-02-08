package tz.go.mof.trab.controllers;

import java.security.Principal;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.user.PasswordDto;
import tz.go.mof.trab.dto.user.UserDto;
import tz.go.mof.trab.models.SystemUser;
import tz.go.mof.trab.service.UserService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class UserController {

    private final DefaultTokenServices defaultTokenServices;

    private final UserService userService;

    Response<String> responseTxt = new Response<>();


    UserController(DefaultTokenServices defaultTokenServices, UserService userService, TokenStore tokenStore) {
        this.defaultTokenServices = defaultTokenServices;
        this.userService = userService;
    }

    @ModelAttribute("password")
    public PasswordDto passwordDto() {
        return new PasswordDto();
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response<SystemUser> saveUser(@RequestBody @Valid UserDto userDto, BindingResult result) {
        return userService.saveUser(userDto, result);

    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public ListResponse<tz.go.mof.trab.dto.user.UserResponseDto> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/users/detail")
    public Principal user(Principal principal) {

        return principal;
    }


    @RequestMapping(value = "/users/{userId}/profile", method = RequestMethod.PUT)
    @ResponseBody
    public Response<String> profile(@PathVariable("userId") String userId, @RequestBody @Valid PasswordDto passwordDto,
                                    BindingResult result) {

        return userService.changePassword(userId, passwordDto, result);

    }


    @RequestMapping(value = "/users/disabled", method = RequestMethod.GET)
    @ResponseBody
    public ListResponse<SystemUser> getAllDisabled() {

        return userService.getAllDisabled();
    }


    @RequestMapping(value = "/users/{userId}/password", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> resetUserPassword(@PathVariable("userId") String userId) {

        return userService.resetUserPassword(userId);
    }


    @RequestMapping(value = "/oauth/token/revoke", method = RequestMethod.POST)
    public  Response<String> logout(@RequestParam("token") String token, @RequestParam("refesh-token") String refreshToken) {
        defaultTokenServices.revokeToken(token);
        defaultTokenServices.revokeToken(refreshToken);

        responseTxt.setCode(6000);
        responseTxt.setData("Success");
        responseTxt.setDescription("Successful logged out");
        responseTxt.setStatus(true);

        return responseTxt;
    }


    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response<SystemUser> editUser(@PathVariable("userId") String userId, @RequestBody UserDto userDto) {

        return userService.editUser(userDto, userId);
    }

}
