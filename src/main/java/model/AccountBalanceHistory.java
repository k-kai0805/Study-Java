package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountBalanceHistory(
        Long id,
        Long accountId,
        Long transactionId,
        BigDecimal balance,
        LocalDateTime asOf) {
}