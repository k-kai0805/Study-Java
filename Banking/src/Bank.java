import java.math.BigDecimal;
import java.util.*;

public class Bank {
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public Bank() {
        seedData();
    }

    public Account findAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new exception.AccountNotFoundException("ACCOUNT_NOT_FOUND: " + accountNumber);
        }
        return account;
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

    private void seedData() {
        addAccount(new Account("ACC10001", "Nguyen Quoc Khanh", new BigDecimal("1500000.00"), new ArrayList<>()));
        addAccount(new Account("ACC10002", "Tran Van A",    new BigDecimal("800000.00"),  new ArrayList<>()));
        addAccount(new Account("ACC10003", "Le Thi B",      new BigDecimal("2250000.00"), new ArrayList<>()));
        addAccount(new Account("ACC10004", "Pham Van C",    new BigDecimal("350000.00"),  new ArrayList<>()));
        addAccount(new Account("ACC10005", "Nguyen Thi D",  new BigDecimal("5000000.00"), new ArrayList<>()));
        addAccount(new Account("ACC10006", "Do Van E",      new BigDecimal("1250000.00"), new ArrayList<>()));
        addAccount(new Account("ACC10007", "Tran Quang F",  new BigDecimal("900000.00"),  new ArrayList<>()));
        addAccount(new Account("ACC10008", "Vu Thi G",      new BigDecimal("4750000.00"), new ArrayList<>()));
    }

}
