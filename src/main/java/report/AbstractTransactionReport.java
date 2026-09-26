package report;

import model.Transaction;
import model.TransactionCriteria;
import repo.Bank;

import java.util.List;

public abstract class AbstractTransactionReport implements ITransactionReport{

    private final Bank bank;
    public AbstractTransactionReport(Bank bank) {
        this.bank = bank;
    }
    @Override
    public void printStatement(String accountNumber, TransactionCriteria transactionCriteria) {
        List<Transaction> listTransaction = bank.findTransactions(accountNumber, transactionCriteria);
        printStatement(listTransaction, transactionCriteria, accountNumber);
    }
    protected abstract void printStatement(List<Transaction> list, TransactionCriteria transactionCriteria, String accountNumber);
}
