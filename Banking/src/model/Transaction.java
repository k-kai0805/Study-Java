package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Transaction(
        BigDecimal amount,
        TypeTransaction type,
        LocalDateTime timestamp,
        BigDecimal afterBalance) {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final NumberFormat MONEY_FORMATTER =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    public String toString() {
        return String.format(
                "[%s] %s VND · %s · %s VND",
                type,
                MONEY_FORMATTER.format(amount),
                timestamp.format(DATE_FORMATTER),
                MONEY_FORMATTER.format(afterBalance)
        );
    }
}