package analyst;

import model.dto.OutflowSummary;
import repo.SqlBank;
import repo.AccountBalanceHistoryDao;
import service.IBankService;
import utils.MessageConstants;
import utils.MoneyUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class SummaryReportService extends AbstractSummaryReport{
    public SummaryReportService(SqlBank bank, IBankService IbankService, AccountBalanceHistoryDao balanceHistoryDao) {
        super(bank, IbankService, balanceHistoryDao);
    }

    private static final Comparator<OutflowSummary>
            OUTFLOW_COMPARATOR =
            Comparator.comparing(
                            OutflowSummary::totalOutflow,
                            Comparator.reverseOrder()
                    )
                    .thenComparing(
                            OutflowSummary::accountNumber
                    );

    @Override
    protected void render(List<OutflowSummary> outflow, BigDecimal totalVolume) {

        System.out.println(
                MessageConstants.TOTAL_VOLUME_LABEL
                        + MoneyUtil.format(totalVolume)
                        + MessageConstants.CURRENCY_SUFFIX
        );

        if (outflow.isEmpty()) {
            System.out.println(
                    MessageConstants.NO_OUTFLOW_IN_PERIOD
            );
            return;
        }

        System.out.println(
                MessageConstants.TOP_OUTFLOW_TITLE
        );

        System.out.printf(
                "%-15s %-20s %-20s%n",
                MessageConstants.ACCOUNT,
                MessageConstants.OUTFLOW,
                MessageConstants.CLOSING_BALANCE
        );

        outflow.stream()
                .sorted(OUTFLOW_COMPARATOR)
                .limit(5)
                .forEach(summary ->
                        System.out.printf(
                                "%-15s %-20s %-20s%n",
                                summary.accountNumber(),
                                MoneyUtil.format(
                                        summary.totalOutflow()
                                ) + MessageConstants.CURRENCY_SUFFIX,
                                MoneyUtil.format(
                                        summary.closingBalance()
                                ) + MessageConstants.CURRENCY_SUFFIX
                        ));
    }
}