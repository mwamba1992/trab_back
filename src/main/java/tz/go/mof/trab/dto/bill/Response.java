package tz.go.mof.trab.dto.bill;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.Role;
import tz.go.mof.trab.models.Summons;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Component
public class Response<B> {
   boolean success;
   String msg;
   Long userId;
   Bill bill;
   String from;
   Summons summons;
   Role role;
   String appealId;
 
}
