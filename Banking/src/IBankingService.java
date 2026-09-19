import java.math.BigDecimal;

public interface IBankingService {
    void deposit(Accounts accounts, BigDecimal amount);
    void withdraw(Accounts accounts, BigDecimal amount);
    BigDecimal checkAccount(Accounts accounts);
}