package report;

import exception.BusinessException;
import model.Account;
import model.Transaction;
import repo.Bank;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractTransactionReport implements ITransactionReport{

    private final Bank bank;
    public AbstractTransactionReport(Bank bank) {
        this.bank = bank;
    }
    @Override
    public void printStatement(String accountNumber) {
        Account account = bank.findAccount(accountNumber);
        printStatement(account);
    }

    protected abstract void printStatement(Account account);
}
