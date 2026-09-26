package analyst;

import model.TransactionCriteria;

public interface ISummaryReport {
    void printSummary(TransactionCriteria criteria);
}
