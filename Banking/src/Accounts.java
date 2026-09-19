import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {
    private int bankId;
    private String bankName;
    private BigDecimal amount;
    private int transactionCount = 0;

    public void incrementTransactionCount() {
        this.transactionCount++;
    }
}
