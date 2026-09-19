import java.math.BigDecimal;

public class BankService extends AbstractBankService{
    @Override
    protected void doDeposit(Accounts accounts, BigDecimal amount) {
        BigDecimal newBalance = accounts.getAmount().add(amount);
        accounts.setAmount(newBalance);
    }

    @Override
    protected void doWithdraw(Accounts accounts, BigDecimal amount) {
        BigDecimal newBalance = accounts.getAmount().subtract(amount);
        accounts.setAmount(newBalance);
    }
}
