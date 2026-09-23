package model;

public record ReportRequest(String accountNumber,
                            TransactionCriteria criteria) {
}
