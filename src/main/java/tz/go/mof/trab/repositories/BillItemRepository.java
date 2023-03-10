package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tz.go.mof.trab.models.BillItems;
import java.util.List;


public interface BillItemRepository extends CrudRepository<BillItems, String> {
    @Query(value = "SELECT * FROM bill_item where bill_id =:bill_id", nativeQuery = true)
    public List<BillItems> getBillItemOfTheSameBill(@Param("bill_id") String bill_id);

}
