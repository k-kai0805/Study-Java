package countTransaction;

import model.TransactionCriteria;

public interface IStatisticsReport{
    void countTransaction(String accountNumber, TransactionCriteria transactionCriteria);
}
