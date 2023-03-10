package tz.go.mof.trab.service;

import org.springframework.http.ResponseEntity;
import tz.go.mof.trab.dto.bill.BillDto;
import tz.go.mof.trab.dto.bill.BillSearchDto;
import tz.go.mof.trab.dto.bill.BillSummaryReportDto;
import tz.go.mof.trab.dto.bill.PortalBillRequestDto;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.BillSummary;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import java.util.List;
import java.util.Map;

public interface BillService {

    Bill saveBill(Bill bill);

    void editBill(String controlNumber, String billId, String responseCode);

    ResponseEntity<Object> receiveBill(String billString);

    Response<BillDto> createBill(PortalBillRequestDto billRequestDto, boolean isFromConsumer);

    Bill findBillById(String billId);

    Response<Bill> cancelBill(String billId);

    List<Bill> searchBills(int page, int size, BillSearchDto billSearchDto);

    Map<String, String> getBillCount(BillSummaryReportDto billSummaryReportDto, String itemId, boolean isCount);

    List<BillSummary> getBIllSummary(BillSummaryReportDto billSummaryReportDto, boolean isCount);


    ListResponse<Bill> getResponseCodeNullResponse();

    Response<Bill> billResend(Map<String , String> req);

}
