import utils.MoneyUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankService extends AbstractBankService{
    @Override
    protected void doDeposit(Account account, BigDecimal amount) {
        BigDecimal newBalance = MoneyUtil.round(account.getBalance().add(amount));
        account.setBalance(newBalance);
        account.addTransaction(new Transaction(amount, TypeTransaction.DEPOSIT, LocalDateTime.now(), newBalance));
    }

    @Override
    protected void doWithdraw(Account account, BigDecimal amount) {
        BigDecimal newBalance = MoneyUtil.round(account.getBalance().subtract(amount));
        account.setBalance(newBalance);
        account.addTransaction(new Transaction(amount, TypeTransaction.WITHDRAW, LocalDateTime.now(), newBalance));
    }
}
