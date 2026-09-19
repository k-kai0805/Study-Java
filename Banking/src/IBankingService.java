import java.math.BigDecimal;

public interface IBankingService {
    void deposit(Account account, BigDecimal amount);
    void withdraw(Account account, BigDecimal amount);
    BigDecimal checkAccount(Account account);
}