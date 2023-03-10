package tz.go.mof.trab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.models.Bill;
import tz.go.mof.trab.models.BillItems;
import tz.go.mof.trab.repositories.BillItemRepository;
import tz.go.mof.trab.utils.ListResponse;


import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BillItemServiceImpl implements  BillItemService{

    @Autowired
    private BillItemRepository billItemRepository;

    @Override
    public BillItems saveBillItem(BillItems billItem) {
        return billItemRepository.save(billItem);
    }

    @Override
    public List<BillItems> getListOfBillItemByBillId(String  billId) {
        return billItemRepository.getBillItemOfTheSameBill(billId);
    }

    @Override
    public ListResponse<Bill> findByParkingDetailsId(String parkingDetailsId) {
        return null;
    }
}
