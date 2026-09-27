package analyst;

import model.Transaction;
import model.TransactionCriteria;
import model.dto.OutflowSummary;
import repo.SqlBank;
import repo.AccountBalanceHistoryDao;
import service.IBankService;
import utils.MessageConstants;
import utils.MoneyUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlSummaryReportService implements ISummaryReport {

    private final repo.SqlBank bank;
    private final service.IBankService bankService;
    private final repo.AccountBalanceHistoryDao balanceHistoryDao;

    private static final Comparator<OutflowSummary> OUTFLOW_COMPARATOR = Comparator.comparing(
                    OutflowSummary::totalOutflow,
                    Comparator.reverseOrder()
            )
            .thenComparing(OutflowSummary::accountNumber);

    public SqlSummaryReportService(repo.SqlTransactionDao transactionDao, service.IBankService bankService) {
        throw new UnsupportedOperationException("Use constructor with SqlBank, IBankService, AccountBalanceHistoryDao");
    }

    public SqlSummaryReportService(repo.SqlBank bank, service.IBankService bankService, repo.AccountBalanceHistoryDao balanceHistoryDao) {
        this.bank = bank;
        this.bankService = bankService;
        this.balanceHistoryDao = balanceHistoryDao;
    }

    @Override
    public void printSummary(model.TransactionCriteria criteria) {
        List<model.Transaction> transactionList = bank.findAllTransactions(criteria);

        java.math.BigDecimal totalVolume = transactionList.stream()
                .map(model.Transaction::amount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.util.Map<String, java.math.BigDecimal> totalOutflowMap = new java.util.HashMap<>();

        for (model.Transaction trx : transactionList) {
            if (!bankService.isOutflow(trx.type())) {
                continue;
            }
            totalOutflowMap.merge(trx.accountNumber(), trx.amount(), java.math.BigDecimal::add);
        }

        java.util.Map<String, model.Transaction> latestTransactionByAccount = new java.util.HashMap<>();

        for (model.Transaction trx : transactionList) {
            java.util.Map<String, model.Transaction> map = latestTransactionByAccount;
            map.merge(trx.accountNumber(), trx,
                    (existing, candidate) -> candidate.timestamp().isAfter(existing.timestamp()) ? candidate : existing);
        }

        java.util.List<model.dto.OutflowSummary> summaries = totalOutflowMap.entrySet().stream()
                .map(entry -> {
                    model.Transaction latest = latestTransactionByAccount.get(entry.getKey());
                    java.math.BigDecimal closingBalance = balanceHistoryDao.findLatestByAccountNumber(entry.getKey())
                            .map(h -> h.balance())
                            .orElse(java.math.BigDecimal.ZERO);
                    return new model.dto.OutflowSummary(
                            entry.getKey(),
                            entry.getValue(),
                            closingBalance
                    );
                })
                .toList();

        render(summaries, totalVolume);
    }

    protected void render(java.util.List<model.dto.OutflowSummary> outflow, java.math.BigDecimal totalVolume) {
        System.out.println(utils.MessageConstants.TOTAL_VOLUME_LABEL
                + utils.MoneyUtil.format(totalVolume)
                + utils.MessageConstants.CURRENCY_SUFFIX);

        if (outflow.isEmpty()) {
            System.out.println(utils.MessageConstants.NO_OUTFLOW_IN_PERIOD);
            return;
        }

        System.out.println(utils.MessageConstants.TOP_OUTFLOW_TITLE);
        System.out.printf("%-15s %-20s %-20s%n",
                utils.MessageConstants.ACCOUNT,
                utils.MessageConstants.OUTFLOW,
                utils.MessageConstants.CLOSING_BALANCE);

        outflow.stream()
                .sorted(OUTFLOW_COMPARATOR)
                .limit(5)
                .forEach(summary ->
                        System.out.printf("%-15s %-20s %-20s%n",
                                summary.accountNumber(),
                                utils.MoneyUtil.format(summary.totalOutflow()) + utils.MessageConstants.CURRENCY_SUFFIX,
                                utils.MoneyUtil.format(summary.closingBalance()) + utils.MessageConstants.CURRENCY_SUFFIX));
    }
}