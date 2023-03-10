package tz.go.mof.trab.dto.bill;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Dickson
 *
 */
@XmlRootElement(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillMapperHeaderDto implements Serializable {

	@Override
	public String toString() {
		return "BillMapperHeaderDto [spCode=" + spCode + ", rtrRespFlg=" + rtrRespFlg + "]";
	}

	private static final long serialVersionUID = -65424865362021653L;
	
	@XmlElement(name="SpCode")
	private String spCode;
	
	@XmlElement(name="RtrRespFlg")
	private boolean rtrRespFlg;

	public BillMapperHeaderDto() {
	}

	/**
	 * @param spCode
	 * @param subSpCode
	 * @param spSysId
	 * @param rtrRespFlg
	 */
	public BillMapperHeaderDto(String spCode, boolean rtrRespFlg) {
		this.spCode = spCode;
		this.rtrRespFlg = rtrRespFlg;
	}

	/**
	 * @return the spCode
	 */
	public String getSpCode() {
		return spCode;
	}

	/**
	 * @param spCode the spCode to set
	 */
	public void setSpCode(String spCode) {
		this.spCode = spCode;
	}
	
	/**
	 * @return the rtrRespFlg
	 */
	public boolean getRtrRespFlg() {
		return rtrRespFlg;
	}

	/**
	 * @param rtrRespFlg the rtrRespFlg to set
	 */
	public void setRtrRespFlg(boolean rtrRespFlg) {
		this.rtrRespFlg = rtrRespFlg;
	}

}
