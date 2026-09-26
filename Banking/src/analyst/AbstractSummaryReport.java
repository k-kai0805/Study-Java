package analyst;

import model.Account;
import model.Transaction;
import model.TransactionCriteria;
import model.dto.OutflowSummary;
import repo.Bank;
import service.IBankService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSummaryReport implements ISummaryReport {
    private final Bank bank;
    private final IBankService bankService;

    protected AbstractSummaryReport(Bank bank, IBankService bankService) {
        this.bank = bank;
        this.bankService = bankService;
    }

    @Override
    public void printSummary(TransactionCriteria criteria) {

        List<Transaction> transactionList =
                bank.findAllTransactions(criteria);

        BigDecimal totalVolume = transactionList.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> totalOutflowMap =
                new HashMap<>();

        for (Transaction trx : transactionList) {

            if (!bankService.isOutflow(trx.type())) {
                continue;
            }

            totalOutflowMap.merge(
                    trx.accountNumber(),
                    trx.amount(),
                    BigDecimal::add
            );
        }

        Map<String, Transaction> latestTransactionByAccount =
                new HashMap<>();

        for (Transaction trx : transactionList) {

            latestTransactionByAccount.merge(
                    trx.accountNumber(),
                    trx,
                    (existing, candidate) ->
                            candidate.timestamp()
                                    .isAfter(existing.timestamp())
                                    ? candidate
                                    : existing
            );
        }

        List<OutflowSummary> summaries =
                totalOutflowMap.entrySet()
                        .stream()
                        .map(entry -> {

                            Transaction latest =
                                    latestTransactionByAccount.get(
                                            entry.getKey()
                                    );

                            return new OutflowSummary(
                                    entry.getKey(),
                                    entry.getValue(),
                                    latest.afterBalance()
                            );
                        })
                        .toList();


        render(summaries, totalVolume);
    }
    protected abstract void render(List<OutflowSummary> outflow, BigDecimal totalVolume);
}
