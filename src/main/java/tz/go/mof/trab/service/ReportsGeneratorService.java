package tz.go.mof.trab.service;
import java.io.IOException;
import net.sf.jasperreports.engine.JRException;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.dto.bill.BillSummaryReportDto;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.utils.Response;


public interface ReportsGeneratorService {


	public Response<String> getPaymentsReports(String reportFormat, PaymentSearchDto paymentSearchDto) throws JRException,IOException;
	public Response<String> getPaymentSummary(String reportFormat, PaymentSearchDto paymentSearchDto) throws JRException,IOException;
	public Response<String> getBillSummary(String format, BillSummaryReportDto billSummaryReportDto, boolean isCount) throws JRException,IOException;
	public Response<String> getBillSummaryAmount(String format, BillSummaryReportDto billSummaryReportDto) throws JRException,IOException;
	public Response<String> getDefaulters(String format, BillSearchDto billSearchDto) throws JRException,IOException;
	public Response<String> getSummons(String format,  Long id, Boolean isRespondent) throws JRException,IOException;
	public Response<String> getPath(String format,  String id) throws JRException,IOException;
	public Response<String> getCauseListed(String reportFormat, BillSearchDto billSearchDto);

}
