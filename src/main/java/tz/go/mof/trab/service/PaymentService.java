package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.PageListResponse;
import tz.go.mof.trab.dto.payment.PaymentSearchDto;
import tz.go.mof.trab.dto.payment.PaymentSummaryDto;
import tz.go.mof.trab.models.Payment;
import java.util.List;




public interface PaymentService {

    PageListResponse<Payment> findAll(int page, int size);
    List<Payment> searchPayments(PaymentSearchDto paymentSearchDto);
    List<PaymentSummaryDto> searchPaymentSummary(PaymentSearchDto paymentSearchDto);
    PageListResponse<Payment> findAllUnreconciledTransactions(int page, int size);

}
