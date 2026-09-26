package model;

import java.time.LocalDate;

public record TransactionCriteria(TypeTransaction typeTransaction, LocalDate fromDate, LocalDate toDate) {
}
