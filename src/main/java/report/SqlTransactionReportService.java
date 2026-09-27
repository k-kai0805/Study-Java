package report;

import model.Transaction;
import model.TransactionCriteria;
import repo.TransactionDao;
import utils.MessageConstants;

import java.util.List;

public class SqlTransactionReportService implements ITransactionReport {

    private final TransactionDao transactionDao;

    public SqlTransactionReportService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public void printStatement(String accountNumber, TransactionCriteria transactionCriteria) {
        List<Transaction> transactions = transactionDao.findByAccountNumber(accountNumber, transactionCriteria);
        printStatement(transactions, transactionCriteria, accountNumber);
    }

    protected void printStatement(List<Transaction> transactions, TransactionCriteria transactionCriteria, String accountNumber) {
        System.out.println("For Account: " + accountNumber);
        if (transactions.isEmpty()) {
            System.out.println(MessageConstants.NO_TRANSACTIONS_YET);
            return;
        }
        for (Transaction trx : transactions) {
            System.out.println(trx);
        }
    }
}