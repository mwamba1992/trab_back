package tz.go.mof.trab.dto.user;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String id;
    private String username;
    private String checkNumber;
    private String address;
    private String email;
    private String mobileNumber;
    private String name;
    private String createdBy;
    private Date recordCreatedDate;
    private boolean accountNonExpired;
    private int loginAttempt;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean newAccount;
}
