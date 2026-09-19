import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(BigDecimal amount, TypeTransaction type, LocalDateTime timestamp, BigDecimal afterBalance) {
    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", afterBalance=" + afterBalance +
                '}';
    }
}

