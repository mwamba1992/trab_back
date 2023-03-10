package tz.go.mof.trab.dto.bill;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Daniel
 *
 */

@XmlRootElement(name = "gepgBillSubReq")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillMapperDto implements Serializable {

	private static final long serialVersionUID = 788373200300723616L;

	@XmlElement(name = "BillHdr")
	private BillMapperHeaderDto billHeaders;
	
	@XmlElement(name = "BillTrxInf")
	private List<BillMapperDetailsDto> billDetails;

	public BillMapperDto() {
	}

	/**
	 * @param billHeaders
	 * @param billDetails
	 */
	public BillMapperDto(BillMapperHeaderDto billHeaders, List<BillMapperDetailsDto> billDetails) {
		this.billHeaders = billHeaders;
		this.billDetails = billDetails;
	}

	/**
	 * @return the billHeaders
	 */
	public BillMapperHeaderDto getBillHeaders() {
		return billHeaders;
	}

	/**
	 * @param billHeaders the billHeaders to set
	 */
	public void setBillHeaders(BillMapperHeaderDto billHeaders) {
		this.billHeaders = billHeaders;
	}

	/**
	 * @return the billDetails
	 */
	public List<BillMapperDetailsDto> getBillDetails() {
		return billDetails;
	}

	/**
	 * @param billDetails the billDetails to set
	 */
	public void setBillDetails(List<BillMapperDetailsDto> billDetails) {
		this.billDetails = billDetails;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BillMapperDto [billHeaders=" + billHeaders + ", billDetails=" + billDetails + "]";
	}

}
