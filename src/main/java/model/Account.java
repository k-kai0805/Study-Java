package model;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Setter(AccessLevel.NONE)
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
