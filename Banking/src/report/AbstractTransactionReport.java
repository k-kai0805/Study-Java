package report;

import model.Account;
import model.Transaction;
import model.TypeTransaction;
import repo.Bank;

import java.util.ArrayList;
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

    @Override
    public void printStatementWithType(String accountNumber, TypeTransaction type){
        List<Transaction> transactions = bank.findByAccountAndType(accountNumber, type);
        printStatementWithType(transactions, accountNumber);
    }

    protected abstract void printStatement(Account account);
    protected abstract void printStatementWithType(List<Transaction> transactionList, String accountName);
}
