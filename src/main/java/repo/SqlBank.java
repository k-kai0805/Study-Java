package repo;

import config.DatabaseConnection;
import exception.AccountNotFoundException;
import exception.BusinessException;
import model.Account;
import model.AccountBalanceHistory;
import model.Transaction;
import model.TransactionCriteria;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SqlBank {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final AccountBalanceHistoryDao balanceHistoryDao;

    public SqlBank(AccountDao accountDao, TransactionDao transactionDao, AccountBalanceHistoryDao balanceHistoryDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.balanceHistoryDao = balanceHistoryDao;
    }

    public Optional<Account> findAccount(String accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return accountDao.findByNumber(accountNumber);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account: " + accountNumber, e);
        }
    }

    public Account findAccountOrThrow(String accountNumber) {
        return findAccount(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("ACCOUNT_NOT_FOUND: " + accountNumber));
    }

    public List<Transaction> findTransactions(String accountNumber, TransactionCriteria criteria) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return transactionDao.findByAccountNumber(conn, accountNumber, criteria);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transactions for account: " + accountNumber, e);
        }
    }

    public List<Transaction> findAllTransactions(TransactionCriteria criteria) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return transactionDao.findAll(conn, criteria);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all transactions", e);
        }
    }

    public List<Account> getAllAccounts() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return accountDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all accounts", e);
        }
    }

    public void addAccount(Account account) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (accountDao.findByNumber(account.getAccountNumber()).isPresent()) {
                    throw new BusinessException("DUPLICATE_ACCOUNT_NUMBER: " + account.getAccountNumber());
                }
                accountDao.save(conn, account);
                var savedAccount = accountDao.findByNumber(conn, account.getAccountNumber())
                        .orElseThrow(() -> new RuntimeException("Failed to find saved account"));
                balanceHistoryDao.save(conn, new AccountBalanceHistory(
                        null, savedAccount.getId(), null, account.getBalance(), LocalDateTime.now()
                ));
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add account: " + account.getAccountNumber(), e);
        }
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                var accountOpt = accountDao.findByNumberForUpdate(conn, accountNumber);
                if (accountOpt.isEmpty()) {
                    throw new AccountNotFoundException("ACCOUNT_NOT_FOUND: " + accountNumber);
                }
                Account account = accountOpt.get();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Deposit amount must be positive");
                }
                BigDecimal newBalance = account.getBalance().add(amount);
                accountDao.updateBalance(conn, accountNumber, newBalance);

                String ref = utils.ReferenceGenerator.nextReference();
                Transaction tx = new Transaction(
                        ref, accountNumber, amount, model.TypeTransaction.DEPOSIT, LocalDateTime.now(), newBalance
                );
                transactionDao.save(conn, tx);

                balanceHistoryDao.save(conn, new AccountBalanceHistory(
                        null, getAccountId(accountNumber), null, newBalance, LocalDateTime.now()
                ));

                conn.commit();
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deposit to account: " + accountNumber, e);
        }
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                var accountOpt = accountDao.findByNumberForUpdate(conn, accountNumber);
                BigDecimal newBalance = getNewBalance(accountNumber, amount, accountOpt);
                accountDao.updateBalance(conn, accountNumber, newBalance);

                String ref = utils.ReferenceGenerator.nextReference();
                Transaction tx = new Transaction(
                        ref, accountNumber, amount, model.TypeTransaction.WITHDRAW, LocalDateTime.now(), newBalance
                );
                transactionDao.save(conn, tx);

                balanceHistoryDao.save(conn, new AccountBalanceHistory(
                        null, getAccountId(accountNumber), null, newBalance, LocalDateTime.now()
                ));

                conn.commit();
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to withdraw from account: " + accountNumber, e);
        }
    }

    private static BigDecimal getNewBalance(String accountNumber, BigDecimal amount, Optional<Account> accountOpt) {
        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("ACCOUNT_NOT_FOUND: " + accountNumber);
        }
        Account account = accountOpt.get();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new exception.InsufficientBalanceException("INSUFFICIENT_BALANCE");
        }
        return account.getBalance().subtract(amount);
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new BusinessException("Cannot transfer to yourself");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String first = fromAccountNumber.compareTo(toAccountNumber) < 0 ? fromAccountNumber : toAccountNumber;
                String second = first.equals(fromAccountNumber) ? toAccountNumber : fromAccountNumber;

                var fromOpt = accountDao.findByNumberForUpdate(conn, first);
                var toOpt = accountDao.findByNumberForUpdate(conn, second);

                if (fromOpt.isEmpty() || toOpt.isEmpty()) {
                    throw new AccountNotFoundException("ACCOUNT_NOT_FOUND");
                }

                Account fromAcc = first.equals(fromAccountNumber) ? fromOpt.get() : toOpt.get();
                Account toAcc = first.equals(fromAccountNumber) ? toOpt.get() : fromOpt.get();

                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Transfer amount must be positive");
                }
                if (amount.compareTo(fromAcc.getBalance()) > 0) {
                    throw new exception.InsufficientBalanceException("INSUFFICIENT_BALANCE: not enough balance to transfer");
                }

                BigDecimal fromNewBalance = fromAcc.getBalance().subtract(amount);
                BigDecimal toNewBalance = toAcc.getBalance().add(amount);

                accountDao.updateBalance(conn, fromAcc.getAccountNumber(), fromNewBalance);
                accountDao.updateBalance(conn, toAcc.getAccountNumber(), toNewBalance);

                String ref = utils.ReferenceGenerator.nextReference();
                Transaction outTx = new Transaction(
                        ref, fromAcc.getAccountNumber(), amount, model.TypeTransaction.TRANSFER_OUT, LocalDateTime.now(), fromNewBalance
                );
                Transaction inTx = new Transaction(
                        ref, toAcc.getAccountNumber(), amount, model.TypeTransaction.TRANSFER_IN, LocalDateTime.now(), toNewBalance
                );
                transactionDao.save(conn, outTx);
                transactionDao.save(conn, inTx);

                balanceHistoryDao.save(conn, new AccountBalanceHistory(
                        null, getAccountId(fromAcc.getAccountNumber()), null, fromNewBalance, LocalDateTime.now()
                ));
                balanceHistoryDao.save(conn, new AccountBalanceHistory(
                        null, getAccountId(toAcc.getAccountNumber()), null, toNewBalance, LocalDateTime.now()
                ));

                conn.commit();
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to transfer", e);
        }
    }

    public void saveTransaction(Transaction transaction) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                transactionDao.save(conn, transaction);
                conn.commit();
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save transaction: " + transaction.referenceNo(), e);
        }
    }

    private Long getAccountId(String accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new RuntimeException("Account not found: " + accountNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get account id: " + accountNumber, e);
        }
    }
}