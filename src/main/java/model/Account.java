package model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Getter
@Setter
public class Account {
    private Long id;
    private Long customerId;
    private String accountNumber;
    private String fullName;
    private BigDecimal balance;
    private String status;
    private LocalDateTime openedAt;
    private List<Transaction> transactions = new ArrayList<>();

    public Account() {
    }

    public Account(Long id, Long customerId, String accountNumber, String fullName, BigDecimal balance, String status, LocalDateTime openedAt, List<Transaction> transactions) {
        this.id = id;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.fullName = fullName;
        this.balance = balance;
        this.status = status;
        this.openedAt = openedAt;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public Account(String accountNumber, String fullName, BigDecimal balance) {
        this(null, null, accountNumber, fullName, balance, "ACTIVE", java.time.LocalDateTime.now(), new ArrayList<>());
    }

    public Account(String accountNumber, String fullName, BigDecimal balance, List<Transaction> transactions) {
        this(null, null, accountNumber, fullName, balance, "ACTIVE", java.time.LocalDateTime.now(), transactions);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
