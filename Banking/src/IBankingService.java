import java.math.BigDecimal;

public interface IBankingService {
    void deposit(String accountNumber, BigDecimal amount);
    void withdraw(String accountNumber, BigDecimal amount);
    void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
    BigDecimal checkAccount(String accountNumber);
}