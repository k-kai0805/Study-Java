package analyst;

import model.Account;
import model.Transaction;
import model.TransactionCriteria;
import model.AccountBalanceHistory;
import model.dto.OutflowSummary;
import repo.SqlBank;
import repo.AccountBalanceHistoryDao;
import service.IBankService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSummaryReport implements ISummaryReport {
    private final SqlBank bank;
    private final IBankService bankService;
    private final AccountBalanceHistoryDao balanceHistoryDao;

    protected AbstractSummaryReport(SqlBank bank, IBankService bankService, AccountBalanceHistoryDao balanceHistoryDao) {
        this.bank = bank;
        this.bankService = bankService;
        this.balanceHistoryDao = balanceHistoryDao;
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

                            // Get closing balance from balance history
                            BigDecimal closingBalance = balanceHistoryDao.findLatestByAccountNumber(entry.getKey())
                                    .map(h -> h.balance())
                                    .orElse(BigDecimal.ZERO);

                            return new OutflowSummary(
                                    entry.getKey(),
                                    entry.getValue(),
                                    closingBalance
                            );
                        })
                        .toList();


        render(summaries, totalVolume);
    }
    protected abstract void render(List<OutflowSummary> outflow, BigDecimal totalVolume);
}