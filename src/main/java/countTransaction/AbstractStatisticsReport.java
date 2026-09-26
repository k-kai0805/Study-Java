package countTransaction;

import model.Transaction;
import model.TransactionCriteria;
import repo.Bank;

import java.util.List;

public abstract class AbstractStatisticsReport implements IStatisticsReport{
    private final Bank bank;

    public AbstractStatisticsReport(Bank bank) {
        this.bank = bank;
    }
    public void countTransaction(String accountNumber, TransactionCriteria transactionCriteria) {
        List<Transaction> listTransaction = bank.findTransactions(accountNumber, transactionCriteria);
        printStatement(listTransaction, transactionCriteria, accountNumber);
    }

    protected abstract void printStatement(List<Transaction> list, TransactionCriteria transactionCriteria, String accountNumber);
}
