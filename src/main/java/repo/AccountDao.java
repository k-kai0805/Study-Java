package repo;

import lombok.Data;
import model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface AccountDao {
    Optional<Account> findById(Long id);
    Optional<Account> findByNumber(String accountNumber);
    Optional<Account> findByNumber(Connection conn, String accountNumber);
    Optional<Account> findByNumberForUpdate(Connection conn, String accountNumber);
    List<Account> findAll();
    void save(Connection conn, Account account);
    void updateBalance(Connection conn, String accountNumber, BigDecimal newBalance);
}