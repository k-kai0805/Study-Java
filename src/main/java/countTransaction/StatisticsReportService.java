package countTransaction;

import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;
import repo.Bank;
import utils.MessageConstants;

import java.util.EnumMap;
import java.util.List;

public class StatisticsReportService extends AbstractStatisticsReport{

    public StatisticsReportService(Bank bank) {
        super(bank);
    }

    @Override
    protected void printStatement(List<Transaction> transactions, TransactionCriteria transactionCriteria, String accountNumber) {
        System.out.println("For Account: " + accountNumber);
        if (transactions.isEmpty()) {
            System.out.println(MessageConstants.NO_TRANSACTIONS_YET);
            return;
        }
        EnumMap<TypeTransaction, Long> map =
                new EnumMap<>(TypeTransaction.class);
        transactions
                .forEach(trx ->
                        map.merge(
                                trx.type(),
                                1L,
                                Long::sum
                        ));
        map.forEach((type, count) ->
                System.out.printf("%s | %d%n", type, count));
    }
}
