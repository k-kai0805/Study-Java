package repo;

import model.Transaction;
import model.TransactionCriteria;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface TransactionDao {
    void save(Connection conn, Transaction transaction);
    Optional<Transaction> findByReferenceNo(Connection conn, String referenceNo);
    Optional<Transaction> findByReferenceNo(String referenceNo);
    List<Transaction> findByAccountNumber(Connection conn, String accountNumber, TransactionCriteria criteria);
    List<Transaction> findByAccountNumber(String accountNumber, TransactionCriteria criteria);
    List<Transaction> findByAccountNumber(String accountNumber);
    List<Transaction> findAll(Connection conn, TransactionCriteria criteria);
    List<Transaction> findAll(TransactionCriteria criteria);
}