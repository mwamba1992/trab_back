package tz.go.mof.trab.dto.bill;

public class BillItemDto {

	private Long revenueSource;
	private Double billedAmount;
	private Double miscellaneousAmount;

	public BillItemDto() {
		super();
	}

	public Long getRevenueSource() {
		return revenueSource;
	}

	public void setRevenueSource(Long revenueSource) {
		this.revenueSource = revenueSource;
	}

	public Double getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(Double billedAmount) {
		this.billedAmount = billedAmount;
	}

	public Double getMiscellaneousAmount() {
		return miscellaneousAmount;
	}

	public void setMiscellaneousAmount(Double miscellaneousAmount) {
		this.miscellaneousAmount = miscellaneousAmount;
	}

	@Override
	public String toString() {
		return "BillItemDto [revenueSource=" + revenueSource + ", billedAmount=" + billedAmount
				+ ", miscellaneousAmount=" + miscellaneousAmount + "]";
	}

}
