package report;

import model.Account;
import model.Transaction;
import repo.Bank;

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
}
