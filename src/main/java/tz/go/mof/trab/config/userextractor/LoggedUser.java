package tz.go.mof.trab.config.userextractor;



import org.springframework.stereotype.Component;
import tz.go.mof.trab.utils.UserDetails;

/**
 *
 * @author TERMIS-development team
 * @date Nov 30, 2019
 * @version 1.0.0
 */
@Component
public interface LoggedUser {

    public UserDetails getInfo();

}
