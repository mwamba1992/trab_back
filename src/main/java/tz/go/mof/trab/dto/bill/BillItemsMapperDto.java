package tz.go.mof.trab.dto.bill;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Augustino Mwageni
 */
@XmlRootElement(name = "BillItems")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillItemsMapperDto implements Serializable{

	private static final long serialVersionUID = -9089312125139688856L;
	
	@XmlElement(name = "BillItem")
	private List<BillItemMapperDto> billItem;
	
	public BillItemsMapperDto(){
		super();
	}

	public BillItemsMapperDto(List<BillItemMapperDto> billItem) {
		super();
		this.billItem = billItem;
	}

	public List<BillItemMapperDto> getBillItem() {
		return billItem;
	}

	public void setBillItem(List<BillItemMapperDto> billItem) {
		this.billItem = billItem;
	}

	@Override
	public String toString() {
		return "BillItemsMapperDto [billItem=" + billItem + "]";
	}
}
