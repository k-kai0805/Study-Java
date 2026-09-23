package report;

import model.TransactionCriteria;

public interface ITransactionReport {
    void printStatement(String accountNumber, TransactionCriteria transactionCriteria);
}
