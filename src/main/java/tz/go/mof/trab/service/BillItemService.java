package tz.go.mof.trab.service;

import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.BillItems;
import tz.go.mof.trab.utils.ListResponse;
import java.util.List;

public interface BillItemService {
    public BillItems saveBillItem(BillItems billItem);

    public List<BillItems> getListOfBillItemByBillId(String billId);

    public ListResponse<Bill> findByParkingDetailsId(String parkingDetailsId);

}
