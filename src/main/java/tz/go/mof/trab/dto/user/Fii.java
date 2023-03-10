package tz.go.mof.trab.dto.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Fii {

    String id;
    String amount;
    String desc;
    String code;
}
