package model.dto;

import java.math.BigDecimal;

public record OutflowSummary(
        String accountNumber,
        BigDecimal totalOutflow,
        BigDecimal closingBalance
) {
}
