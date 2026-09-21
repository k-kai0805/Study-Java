package report;

import model.TypeTransaction;

public interface ITransactionReport {
    void printStatement(String accountNumber);
    void printStatementWithType(String accountNumber, TypeTransaction type);
}
