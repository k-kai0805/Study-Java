package countTransaction;

import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;
import repo.TransactionDao;
import utils.MessageConstants;

import java.util.EnumMap;
import java.util.List;

public class SqlStatisticsReportService implements IStatisticsReport {

    private final TransactionDao transactionDao;

    public SqlStatisticsReportService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public void countTransaction(String accountNumber, TransactionCriteria transactionCriteria) {
        List<Transaction> transactions = transactionDao.findByAccountNumber(accountNumber, transactionCriteria);
        printStatement(transactions, transactionCriteria, accountNumber);
    }

    protected void printStatement(List<Transaction> transactions, TransactionCriteria transactionCriteria, String accountNumber) {
        System.out.println("For Account: " + accountNumber);
        if (transactions.isEmpty()) {
            System.out.println(MessageConstants.NO_TRANSACTIONS_YET);
            return;
        }
        EnumMap<TypeTransaction, Long> map = new EnumMap<>(TypeTransaction.class);
        transactions.forEach(trx ->
                map.merge(trx.type(), 1L, Long::sum));
        map.forEach((type, count) ->
                System.out.printf("%s | %d%n", type, count));
    }
}