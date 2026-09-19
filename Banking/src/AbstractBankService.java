import java.math.BigDecimal;

public abstract class AbstractBankService implements IBankingService{
    @Override
    public void deposit(Accounts accounts, BigDecimal amount){
        if (accounts == null){
            throw new IllegalArgumentException("Account is invalid");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Deposit cannot negative");
        }

        doDeposit(accounts, amount);
        accounts.incrementTransactionCount();
        System.out.println("[Log] Transaction completed");
    }
    @Override
    public void withdraw(Accounts accounts, BigDecimal amount){
        if (accounts == null){
            throw new IllegalArgumentException("Account is invalid");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Withdraw cannot negative");
        }

        doWithdraw(accounts, amount);
        accounts.incrementTransactionCount();
        System.out.println("[Log] Transaction completed");
    }
    @Override
    public BigDecimal checkAccount(Accounts accounts){
        if (accounts == null){
            throw new IllegalArgumentException("Account is invalid");
        }
        return accounts.getAmount();
    }

    protected abstract void doDeposit(Accounts accounts, BigDecimal amount);
    protected abstract void doWithdraw(Accounts accounts, BigDecimal amount);
}
