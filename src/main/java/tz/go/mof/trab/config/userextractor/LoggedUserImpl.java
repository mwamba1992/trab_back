package tz.go.mof.trab.config.userextractor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.utils.CustomGeneratedData;
import tz.go.mof.trab.utils.UserDetails;


/**
 *
 * @author TERMIS-development team
 * @date June 11, 2020
 * @version 1.0.0
 */
@Component
public class LoggedUserImpl implements LoggedUser {
    private static final Logger logger = LoggerFactory.getLogger(LoggedUserImpl.class);


    @Override
    @SuppressWarnings("unchecked")
    public UserDetails getInfo() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (null == auth) {
            return null;
        } else {
            UserDetails user = new UserDetails();
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonAuth = mapper.writeValueAsString(auth);

                HashMap<String, Object> result = new ObjectMapper().readValue(jsonAuth, HashMap.class);
                if (result.get("userAuthentication") != null) {

                    HashMap<String, Object> userAuthentication = (HashMap<String, Object>) result
                            .get("userAuthentication");

                    HashMap<String, Object> details = (HashMap<String, Object>) userAuthentication.get("details");

                    HashMap<String, Object> principal = null;

                    if (userAuthentication.get("principal") != null
                            && CustomGeneratedData.isObjectHashMap(userAuthentication.get("principal"))) {
                        principal = (HashMap<String, Object>) userAuthentication.get("principal");
                    }

                    if ((details.get("principal") != null
                            && CustomGeneratedData.isObjectHashMap(details.get("principal"))) || principal != null) {
                        HashMap<String, Object> userDetails = (HashMap<String, Object>) details.get("principal");


                        if (principal != null) {
                            userDetails = principal;
                        }


                        Object id = userDetails.get("id");
                        Object email = userDetails.get("email");
                        Object firstName = userDetails.get("name");
                        Object checkNumber = userDetails.get("checkNumber");
                        Object accountNonExpired = userDetails.get("accountNonExpired");
                        Object accountNonLocked = userDetails.get("accountNonLocked");
                        Object credentialsNonExpired = userDetails.get("credentialsNonExpired");
                        Object enabled = userDetails.get("enabled");
                        Object permissions = userDetails.get("authorities");

                        user.setId(id.toString());
                        user.setName(firstName.toString());
                        user.setEmail(email.toString());
                        user.setAccountNonExpired(Boolean.valueOf(accountNonExpired.toString()));
                        user.setAccountNonLocked(Boolean.parseBoolean(accountNonLocked.toString()));
                        user.setCredentialsNonExpired(Boolean.valueOf(credentialsNonExpired.toString()));
                        user.setEnabled(Boolean.valueOf(enabled.toString()));
                        user.setPermissionList((List<Map<String, String>>) permissions);



                        return user;
                    } else {
                        return null;
                    }

                }

            } catch (Exception e) {
                logger.error("----------------Error has occured on authentication facade" + e.getMessage()
                        + "----------------------");

                e.printStackTrace();

            }

        }
        return null;


    }
}
