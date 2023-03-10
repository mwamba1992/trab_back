package tz.go.mof.trab.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.BankAccount;


@CrossOrigin("*")
@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, String> {
    BankAccount findByAccountNumber(String accountNumber);
}
