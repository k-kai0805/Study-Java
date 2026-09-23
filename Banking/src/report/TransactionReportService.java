package report;

import model.Transaction;
import model.TransactionCriteria;
import repo.Bank;

import java.util.List;

public class TransactionReportService extends AbstractTransactionReport{
    public TransactionReportService(Bank bank){super(bank);}

    @Override
    protected void printStatement(List<Transaction> transactions, TransactionCriteria transactionCriteria, String accountNumber) {
        System.out.println("For Account: " + accountNumber);
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet");
            return;
        }
        for (Transaction trx : transactions) {
            System.out.println(trx);
        }
    }
}
