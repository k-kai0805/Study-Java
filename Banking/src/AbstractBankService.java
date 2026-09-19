import java.math.BigDecimal;
import exception.*;

public abstract class AbstractBankService implements IBankingService{

    private final Bank bank;

    public AbstractBankService(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = bank.findAccount(accountNumber);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
        doDeposit(account, amount);
    }

    @Override
    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = bank.findAccount(accountNumber);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero");
        }
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException("INSUFFICIENT_BALANCE");
        }
        doWithdraw(account, amount);
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber == null || toAccountNumber == null || fromAccountNumber.equals(toAccountNumber)) {
            throw new BusinessException("Cannot transfer to yourself or invalid account numbers");
        }
        Account from = bank.findAccount(fromAccountNumber);
        Account to = bank.findAccount(toAccountNumber);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero");
        }
        if (amount.compareTo(from.getBalance()) > 0) {
            throw new InsufficientBalanceException("INSUFFICIENT_BALANCE: not enough balance to transfer");
        }
        doTransfer(from, to, amount);
    }

    @Override
    public BigDecimal checkAccount(String accountNumber) {
        return bank.findAccount(accountNumber).getBalance();
    }

    protected abstract void doDeposit(Account account, BigDecimal amount);
    protected abstract void doWithdraw(Account account, BigDecimal amount);
    protected abstract void doTransfer(Account from, Account to, BigDecimal amount);
}
