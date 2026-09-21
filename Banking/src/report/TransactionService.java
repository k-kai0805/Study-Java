package report;

import model.Account;
import model.Transaction;
import model.TypeTransaction;
import repo.Bank;

import java.util.ArrayList;
import java.util.List;

public class TransactionService extends AbstractTransactionReport{
    public TransactionService(Bank bank){super(bank);}

    @Override
    protected void printStatement(Account account) {
        List<Transaction> transactions = account.getTransactions();
        System.out.println("For Account: " + account.getAccountNumber());
        if (transactions.isEmpty()) {
            System.out.println("No transaction yet.");
            return;
        }
        for (Transaction trx : transactions) {
            System.out.println(trx);
        }
    }

    @Override
    protected void printStatementWithType(List<Transaction> transactions, String accountName) {
        System.out.println("For Account: " + accountName);
        if (transactions.isEmpty()) {
            System.out.println("No transaction yet.");
            return;
        }
        for (Transaction trx : transactions) {
            System.out.println(trx);
        }
    }
}
