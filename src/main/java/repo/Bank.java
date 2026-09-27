package repo;

import model.Account;
import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Bank {
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public Account findAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new exception.AccountNotFoundException("ACCOUNT_NOT_FOUND: " + accountNumber);
        }
        return account;
    }

    public List<Transaction> findTransactions(
            String accountNumber,
            TransactionCriteria criteria) {

        Account account = findAccount(accountNumber);

        return account.getTransactions()
                .stream()
                .filter(trx -> matchesCriteria(trx, criteria))
                .toList();
    }

    public List<Account> getAllAccounts() {
        return List.copyOf(accounts.values());
    }

    public void addAccount(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            throw new exception.BusinessException("Account is invalid");
        }
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new exception.BusinessException("DUPLICATE_ACCOUNT_NUMBER: " + account.getAccountNumber());
        }
        accounts.put(account.getAccountNumber(), account);
    }

    public List<Transaction> findAllTransactions(
            TransactionCriteria criteria) {

        return accounts.values()
                .stream()
                .flatMap(account ->
                        account.getTransactions().stream())
                .filter(trx -> matchesCriteria(trx, criteria))
                .toList();
    }

    private boolean matchesCriteria(
            Transaction trx,
            TransactionCriteria criteria) {

        LocalDate trxDate =
                trx.timestamp().toLocalDate();

        boolean matchType =
                criteria.typeTransaction() == null
                        || trx.type() == criteria.typeTransaction();

        boolean matchFrom =
                criteria.fromDate() == null
                        || !trxDate.isBefore(criteria.fromDate());

        boolean matchTo =
                criteria.toDate() == null
                        || !trxDate.isAfter(criteria.toDate());

        return matchType
                && matchFrom
                && matchTo;
    }
}
