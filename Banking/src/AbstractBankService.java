import java.math.BigDecimal;
import exception.*;

public abstract class AbstractBankService implements IBankingService{
    @Override
    public void deposit(Account account, BigDecimal amount){
        if (account == null){
            throw new BusinessException("Account is invalid");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }

        doDeposit(account, amount);
        System.out.println("[Log] Transaction completed");
    }
    @Override
    public void withdraw(Account account, BigDecimal amount){
        if (account == null){
            throw new BusinessException("Account is invalid");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Withdraw amount must be greater than zero");
        }
        if (amount.compareTo(account.getBalance()) > 0){
            throw new InsufficientBalanceException("INSUFFICIENT_BALANCE");
        }

        doWithdraw(account, amount);
        System.out.println("[Log] Transaction completed");
    }
    @Override
    public BigDecimal checkAccount(Account account){
        if (account == null){
            throw new BusinessException("Account is invalid");
        }
        return account.getBalance();
    }

    protected abstract void doDeposit(Account account, BigDecimal amount);
    protected abstract void doWithdraw(Account account, BigDecimal amount);
}
