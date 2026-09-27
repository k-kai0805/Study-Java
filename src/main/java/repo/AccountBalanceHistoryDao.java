package repo;

import model.AccountBalanceHistory;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface AccountBalanceHistoryDao {
    void save(Connection conn, AccountBalanceHistory history);
    List<AccountBalanceHistory> findByAccountNumber(String accountNumber);
    List<AccountBalanceHistory> findByAccountNumberAndDateRange(String accountNumber, java.time.LocalDate fromDate, java.time.LocalDate toDate);
    List<AccountBalanceHistory> findAllByDate(java.time.LocalDate date);
    Optional<AccountBalanceHistory> findLatestByAccountNumber(String accountNumber);
}