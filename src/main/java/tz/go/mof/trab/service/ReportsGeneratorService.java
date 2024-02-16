package tz.go.mof.trab.service;
import java.io.IOException;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.dto.bill.BillSummaryReportDto;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.utils.Response;


public interface ReportsGeneratorService {


	Response<String> getPaymentsReports(String reportFormat, PaymentSearchDto paymentSearchDto) throws JRException,IOException;
	Response<String> getPaymentSummary(String reportFormat, PaymentSearchDto paymentSearchDto) throws JRException,IOException;
	Response<String> getBillSummary(String format, BillSummaryReportDto billSummaryReportDto, boolean isCount) throws JRException,IOException;
	Response<String> getBillSummaryAmount(String format, BillSummaryReportDto billSummaryReportDto) throws JRException,IOException;
	Response<String> getDefaulters(String format, BillSearchDto billSearchDto) throws JRException,IOException;
	Response<String> getSummons(String format,  Long id, Boolean isRespondent) throws JRException,IOException;
	Response<String> getPath(String format,  String id) throws JRException,IOException;
	Response<String> getCauseListed(String reportFormat, BillSearchDto billSearchDto);

	Response<String> getExcelAppealReport(List<Appeals> appealsList, String details) throws IOException;

}
